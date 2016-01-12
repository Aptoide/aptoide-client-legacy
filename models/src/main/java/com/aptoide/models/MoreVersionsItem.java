package com.aptoide.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 03-11-2015.
 */
public class MoreVersionsItem extends AppItem {
    public MoreVersionsItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }
}
