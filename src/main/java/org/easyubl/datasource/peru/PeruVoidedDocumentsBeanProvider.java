package org.easyubl.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.VoidedLineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.VoidedDocumentsLineType;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@DatasourceType(datasource = "PeruVoidedDocumentsDS")
public class PeruVoidedDocumentsBeanProvider implements DatasourceProvider {

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        VoidedDocumentsType voidedDocumentsType = read(file);
        if (file == null) {
            return null;
        }

        VoidedDocumentsDatasource bean = new VoidedDocumentsDatasource();

        bean.setIdAsignado(voidedDocumentsType.getID().getValue());
        bean.setFechaEmision(voidedDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());
        bean.setFechaGeneracion(voidedDocumentsType.getReferenceDate().getValue().toGregorianCalendar().getTime());
        bean.setProveedor(BeanUtils.toSupplier(voidedDocumentsType.getAccountingSupplierParty()));


        List<VoidedLineBean> lines = new ArrayList<>();

        List<VoidedDocumentsLineType> voidedDocumentsLineTypes = voidedDocumentsType.getVoidedDocumentsLine();
        for (VoidedDocumentsLineType voidedDocumentsLineType : voidedDocumentsLineTypes) {
            VoidedLineBean lineBean = new VoidedLineBean();

            lineBean.setDocumentoSerie(voidedDocumentsLineType.getDocumentSerialID().getValue());
            lineBean.setDocumentoNumero(voidedDocumentsLineType.getDocumentNumberID().getValue());
            TipoDocumento.getFromCode(voidedDocumentsLineType.getDocumentTypeCode().getValue()).ifPresent(c -> {
                lineBean.setTipoDocumento(c.getDenominacion());
            });
            lineBean.setMotivoBaja(voidedDocumentsLineType.getVoidReasonDescription().getValue());

            lines.add(lineBean);
        }

        bean.setDetalle(lines);

        return bean;
    }

    private VoidedDocumentsType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
