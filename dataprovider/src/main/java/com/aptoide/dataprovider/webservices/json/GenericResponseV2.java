package com.aptoide.dataprovider.webservices.json;

import java.util.List;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;

/**
 * Created by j-pac on 30-05-2014.
 */
public class GenericResponseV2 {

    String status;

    List<ErrorResponse> errors;

    public String getStatus() {
        return status;
    }

    public List<ErrorResponse> getErrors() {
        return errors;
    }

}
