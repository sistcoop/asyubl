package org.easyubl.models.exceptions;

public class UnreadableDocumentException extends Exception {

    public UnreadableDocumentException() {
    }

    public UnreadableDocumentException(String message) {
        super(message);
    }

    public UnreadableDocumentException(String message, Throwable e) {
        super(message, e);
    }

}
