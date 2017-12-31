package org.easyubl.documents;

import org.easyubl.files.XmlUBLFileModel;
import org.easyubl.models.CompanyModel;
import org.easyubl.models.exceptions.UnreadableDocumentException;
import org.easyubl.models.exceptions.UnrecognizableDocumentTypeException;
import org.easyubl.models.exceptions.UnsupportedDocumentTypeException;

public interface DocumentProvider {

    /**
     * @param file that contains xml file to be persisted
     */
    DocumentModel addDocument(CompanyModel company,
                              XmlUBLFileModel file,
                              String fileProvider,
                              DocumentProviderType providerType) throws
            UnsupportedDocumentTypeException,
            UnrecognizableDocumentTypeException,
            UnreadableDocumentException;

    /**
     * @param id unique identity generated by the system
     * @return document
     */
    DocumentModel getDocument(String id);

    /**
     * @param type               document type
     * @param assignedId         document assigned id
     * @param supplierAssignedId supplier assigned id
     * @return document
     */
    DocumentModel getDocument(String type, String assignedId, String supplierAssignedId);

    /**
     * @param document document to be removed
     * @return true if document was removed
     */
    boolean removeDocument(DocumentModel document);

}
