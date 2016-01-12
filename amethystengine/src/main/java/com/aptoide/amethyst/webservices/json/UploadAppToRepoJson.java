package com.aptoide.amethyst.webservices.json;

import com.aptoide.dataprovider.webservices.models.ErrorResponse;

import java.util.List;

import lombok.Data;

/**
 * Created by fabio on 22-10-2015.
 */
public class UploadAppToRepoJson {
    public String status;
    public Info info;
    public List<ErrorResponse> errors;

    @Data
    public static class Info{
        private String iconUrl;
        private String utr;
    }
}
