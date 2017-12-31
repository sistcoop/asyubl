package org.easyubl.documents;

public interface DocumentVersionModel extends Document {

    String getId();

    boolean isCurrentVersion();

    DocumentModel getDocument();

}
