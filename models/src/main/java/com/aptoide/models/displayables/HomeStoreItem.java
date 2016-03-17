package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 08-10-2015.
 */
public class HomeStoreItem extends Displayable {

    public long id;
    public String repoName;
    public String avatar;
    public String added;
    public String modified;

    public String description;
    public String theme;
    public String view;

    public long storeApps;
    public long storeDwnNumber;
    public long storeSubscribers;

    public HomeStoreItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    @Override
    public int getSpanSize() {
        return FULL_ROW / 2;
    }
}
