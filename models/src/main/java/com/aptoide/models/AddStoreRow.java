package com.aptoide.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 23-06-2015.
 */
public class AddStoreRow extends Displayable {

    public AddStoreRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    @Override
    public int getSpanSize() {
        return BUCKETSIZE;
    }
}
