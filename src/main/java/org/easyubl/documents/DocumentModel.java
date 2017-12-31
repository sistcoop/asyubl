package org.easyubl.documents;

import java.util.Date;

public interface DocumentModel {

    String ASSIGNED_ID = "assignedId";

    String SUPPLIER_NAME = "supplierName";
    String SUPPLIER_ASSIGNED_ID = "supplierAssignedId";
    String CUSTOMER_NAME = "customerName";
    String CUSTOMER_ASSIGNED_ID = "customerAssignedId";

    String TYPE = "type";
    String CURRENCY = "currency";
    String ISSUE_DATE = "issueDate";
    String AMOUNT = "amount";

    String getId();

    String getType();
    String getFileId();
    String getFileProvider();
    String getAssignedId();
    Date getIssueDate();

    String getCurrency();
    Float getAmount();
    Float getTax();

    String getSupplierName();
    String getSupplierAssignedId();

    String getCustomerName();
    String getCustomerAssignedId();

    DocumentProviderType getProvider();

    interface DocumentCreationEvent {
        String getDocumentType();

        Object getJaxb();

        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent {
        DocumentModel getDocument();
    }

}
