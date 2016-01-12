package com.aptoide.dataprovider.webservices.json.review;

import com.aptoide.dataprovider.webservices.models.ErrorResponse;

import java.util.List;

/**
 * Created by rmateus on 23-02-2015.
 */
public class ReviewJson {

    public String status;
    public Review review;
    public List<ErrorResponse> errors;

    public Review getReview() {
        return review;
    }
}
