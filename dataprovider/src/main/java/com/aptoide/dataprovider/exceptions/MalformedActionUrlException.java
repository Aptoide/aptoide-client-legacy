package com.aptoide.dataprovider.exceptions;

/**
 * Created by hsousa on 17/09/15.
 */
public class MalformedActionUrlException extends Exception {

    private static final long serialVersionUID = 4664456875599611219L;

    public String errorCode = "Unknown_Exception";

    public MalformedActionUrlException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MalformedActionUrlException(String errorCode) {
        super("MalformedActionUrl");
        this.errorCode = errorCode;
    }

}
