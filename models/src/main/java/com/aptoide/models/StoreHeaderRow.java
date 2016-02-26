package com.aptoide.models;

import com.aptoide.models.Displayable;

/**
 * Created by hsousa on 13/08/15.
 */
public class StoreHeaderRow extends Displayable {

    public long id;
    public String name;
    public String avatar;
    public String description;
    public long apps;
    public long subscribers;
    public long downloads;

    public StoreHeaderRow(int bucketSize) {
        super(bucketSize);
    }

    public int getSpanSize() {
        return FULL_ROW;
    }

}
