package com.prairie.eevernote.dom;

@SuppressWarnings("serial")
public class DOMException extends RuntimeException {

    public short code;

    public static final short INDEX_SIZE_ERR = 1;
    public static final short HIERARCHY_REQUEST_ERR = 2;
    public static final short WRONG_DOCUMENT_ERR = 3;
    public static final short NOT_FOUND_ERR = 4;
    public static final short NOT_SUPPORTED_ERR = 5;
    public static final short INUSE_ATTRIBUTE_ERR = 6;

    public DOMException(final short code, final String message) {
        super(message);
        this.code = code;
    }
}
