package com.aptoide.dataprovider.webservices.models.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 21/09/15.
 */
public class GetComments {

    public String status;
    public int offset, total;

    @JsonProperty("listing")
    public List<Comment> list = new ArrayList<>();
}
