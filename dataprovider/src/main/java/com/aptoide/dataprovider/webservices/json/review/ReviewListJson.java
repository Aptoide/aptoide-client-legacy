package com.aptoide.dataprovider.webservices.json.review;

import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmateus on 23-02-2015.
 */
public class ReviewListJson {

    public String status;
    public List<Review> reviews = new ArrayList<>();
    @JsonProperty("paging_details")
    public PagingDetails pagingDetails;
    public List<ErrorResponse> errors;

    public static class PagingDetails {

        public Integer total;
        public Integer offset;
        public Integer limit;
    }
}
