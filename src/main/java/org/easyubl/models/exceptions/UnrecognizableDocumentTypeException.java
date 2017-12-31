package org.easyubl.models.exceptions;

public class UnrecognizableDocumentTypeException extends Exception {

    public UnrecognizableDocumentTypeException() {
    }

    public UnrecognizableDocumentTypeException(String message) {
        super(message);
    }

    public UnrecognizableDocumentTypeException(String message, Throwable e) {
        super(message, e);
    }

}
