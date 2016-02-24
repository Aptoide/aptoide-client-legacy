package com.aptoide.amethyst.webservices.exceptions;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by fabio on 13-10-2015.
 */
public class InvalidGrantSpiceException extends SpiceException {
    private String error_description;

    public InvalidGrantSpiceException(String error_description) {
        super(error_description);
        this.error_description = error_description;
    }

    public String getError_description() {
        return error_description;
    }
}
