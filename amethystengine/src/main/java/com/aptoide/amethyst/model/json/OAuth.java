package com.aptoide.amethyst.model.json;


import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;


/**
 * Created by rmateus on 01-07-2014.
 */
@Data
public class OAuth {


    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @JsonProperty("access_token")
    public String access_token;

    public String refresh_token;


    public String error_description;


    public List<ErrorResponse> errors;


    public String status;

    public String getStatus() {
        return status;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public List<ErrorResponse> getError() {
        return errors;
    }



    public String getError_description() {
        return error_description;
    }
}
