package org.easyubl.documents.parser;

import org.clarksnut.files.XmlUBLFileModel;

public interface ParsedDocumentProvider {

    String getSupportedDocumentType();

    int getPriority();

    ParsedDocument read(XmlUBLFileModel file);

}
