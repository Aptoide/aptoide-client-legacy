package com.aptoide.models.displayables;

import com.aptoide.models.displayables.AppItem;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 03-11-2015.
 */
public class MoreVersionsItem extends AppItem {
    public MoreVersionsItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }
}
