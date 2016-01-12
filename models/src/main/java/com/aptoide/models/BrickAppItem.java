package com.aptoide.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Editor's choice displayable.
 *
 * Created by rmateus on 04/06/15.
 */
public class BrickAppItem extends AppItem {

    public BrickAppItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

}