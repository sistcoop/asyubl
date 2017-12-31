package org.easyubl.datasource.basic;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
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
import java.util.stream.Collectors;

@Stateless
@DatasourceType(datasource = "BasicCreditNoteDS")
public class BasicCreditNoteDatasourceProvider implements DatasourceProvider {

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(file.getDocument());
        if (creditNoteType == null) {
            return null;
        }

        CreditNoteDatasource bean = new CreditNoteDatasource();

        bean.setAssignedId(creditNoteType.getIDValue());
        bean.setCurrency(creditNoteType.getDocumentCurrencyCodeValue());
        bean.setSupplier(DatasourceUtils.toSupplier(creditNoteType.getAccountingSupplierParty()));
        bean.setCustomer(DatasourceUtils.toCustomer(creditNoteType.getAccountingCustomerParty()));

        // Invoice reference
        String invoiceReference = creditNoteType.getDiscrepancyResponse().stream()
                .filter(p -> p.getReferenceIDValue() != null)
                .map(ResponseType::getReferenceIDValue)
                .collect(Collectors.joining(", "));
        bean.setInvoiceReference(invoiceReference);

        // Issue date
        bean.setIssueDate(DatasourceUtils.toDate(creditNoteType.getIssueDate(), Optional.ofNullable(creditNoteType.getIssueTime())));

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = creditNoteType.getLegalMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            bean.setMonetaryTotal(DatasourceUtils.toMonetaryTotal(legalMonetaryTotalType));
        }

        // Total tax
        float totalTax = creditNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
        bean.setTotalTax(totalTax);

        // Lines
        bean.setLines(getLines(creditNoteType.getCreditNoteLine()));

        return bean;
    }

    private List<LineBean> getLines(List<CreditNoteLineType> creditNoteLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (CreditNoteLineType creditNoteLineType : creditNoteLineTypes) {
            LineBean lineBean = new LineBean();

            // Description and product code
            DatasourceUtils.addDescriptionAndProductCode(lineBean, Optional.ofNullable(creditNoteLineType.getItem()));

            // Quantity and unit code
            if (creditNoteLineType.getCreditedQuantityValue() != null) {
                lineBean.setQuantity(creditNoteLineType.getCreditedQuantityValue().floatValue());
            }
            if (creditNoteLineType.getCreditedQuantity() != null) {
                lineBean.setUnitCode(creditNoteLineType.getCreditedQuantity().getUnitCode());
            }

            // Price amount
            if (creditNoteLineType.getPrice() != null) {
                if (creditNoteLineType.getPrice().getPriceAmountValue() != null) {
                    lineBean.setPriceAmount(creditNoteLineType.getPrice().getPriceAmountValue().floatValue());
                }
            }

            // Allowance charges
            List<AllowanceChargeType> allowanceChargeTypes = creditNoteLineType.getAllowanceCharge();
            float totalAllowanceCharge = allowanceChargeTypes.stream()
                    .map(AllowanceChargeType::getAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .floatValue();
            lineBean.setTotalAllowanceCharge(totalAllowanceCharge);

            // Total Price
            if (creditNoteLineType.getLineExtensionAmountValue() != null) {
                lineBean.setExtensionAmount(creditNoteLineType.getLineExtensionAmountValue().floatValue());
            }

            // Total tax
            float totalTax = creditNoteLineType.getTaxTotal().stream()
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
