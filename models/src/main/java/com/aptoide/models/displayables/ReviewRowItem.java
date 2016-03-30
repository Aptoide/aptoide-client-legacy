package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 24/09/15.
 */
public class ReviewRowItem extends Displayable {
    public String appIcon;
    public String appName;
    public String description;
    public float rating;
    public String reviewer;
    public String avatar;
    public Integer reviewId;

    public ReviewRowItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }
}
