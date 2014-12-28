package com.prairie.eemory.exception;

@SuppressWarnings("serial")
public class NoDataFoundException extends Exception {

    public NoDataFoundException() {

    }

    public NoDataFoundException(final String message) {
        super(message);
    }

}
