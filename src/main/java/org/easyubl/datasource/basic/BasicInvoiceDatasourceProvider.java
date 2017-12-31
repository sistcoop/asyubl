package org.easyubl.datasource.basic;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.basic.beans.LineBean;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Stateless
@DatasourceType(datasource = "BasicInvoiceDS")
public class BasicInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        InvoiceType invoiceType = UBL21Reader.invoice().read(file.getDocument());
        if (invoiceType == null) {
            return null;
        }

        InvoiceDatasource bean = new InvoiceDatasource();

        bean.setAssignedId(invoiceType.getIDValue());
        bean.setCurrency(invoiceType.getDocumentCurrencyCodeValue());
        bean.setSupplier(DatasourceUtils.toSupplier(invoiceType.getAccountingSupplierParty()));
        bean.setCustomer(DatasourceUtils.toCustomer(invoiceType.getAccountingCustomerParty()));

        // Issue date
        bean.setIssueDate(DatasourceUtils.toDate(invoiceType.getIssueDate(), Optional.ofNullable(invoiceType.getIssueTime())));

        // Delivery location
        DeliveryTermsType deliveryTermsType = invoiceType.getDeliveryTerms();
        if (deliveryTermsType != null) {
            LocationType locationType = deliveryTermsType.getDeliveryLocation();
            if (locationType != null) {
                bean.setDeliveryAddress(DatasourceUtils.toAddress(locationType.getAddress()));
            }
        }

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = invoiceType.getLegalMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            bean.setMonetaryTotal(DatasourceUtils.toMonetaryTotal(legalMonetaryTotalType));
        }

        // Total tax
        float totalTax = invoiceType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
        bean.setTotalTax(totalTax);

        // Lines
        bean.setLines(getLines(invoiceType.getInvoiceLine()));

        return bean;
    }

    private List<LineBean> getLines(List<InvoiceLineType> invoiceLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (InvoiceLineType invoiceLineType : invoiceLineTypes) {
            LineBean lineBean = new LineBean();

            // Description and product code
            DatasourceUtils.addDescriptionAndProductCode(lineBean, Optional.ofNullable(invoiceLineType.getItem()));

            // Quantity and unit code
            if (invoiceLineType.getInvoicedQuantityValue() != null) {
                lineBean.setQuantity(invoiceLineType.getInvoicedQuantityValue().floatValue());
            }
            if (invoiceLineType.getInvoicedQuantity() != null) {
                lineBean.setUnitCode(invoiceLineType.getInvoicedQuantity().getUnitCode());
            }

            // Price amount
            if (invoiceLineType.getPrice() != null) {
                if (invoiceLineType.getPrice().getPriceAmountValue() != null) {
                    lineBean.setPriceAmount(invoiceLineType.getPrice().getPriceAmountValue().floatValue());
                }
            }

            // Allowance charges
            List<AllowanceChargeType> allowanceChargeTypes = invoiceLineType.getAllowanceCharge();
            float totalAllowanceCharge = allowanceChargeTypes.stream()
                    .map(AllowanceChargeType::getAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .floatValue();
            lineBean.setTotalAllowanceCharge(totalAllowanceCharge);

            // Total Price
            if (invoiceLineType.getLineExtensionAmountValue() != null) {
                lineBean.setExtensionAmount(invoiceLineType.getLineExtensionAmountValue().floatValue());
            }

            // Total tax
            float totalTax = invoiceLineType.getTaxTotal().stream()
                    .map(TaxTotalType::getTaxAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .floatValue();
            lineBean.setTotalTax(totalTax);


            result.add(lineBean);
        }

        return result;
    }
}
