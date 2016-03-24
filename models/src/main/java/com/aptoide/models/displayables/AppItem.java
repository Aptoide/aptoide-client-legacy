package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class to hold data used to display an AppItem
 *
 * Created by rmateus on 04/06/15.
 */
public class AppItem extends Displayable {

    public long id;
    public String appName;
    public String packageName;
    public long fileSize;
    public String icon;
    public String featuredGraphic;
    public String uptype;
    public String path;
    public String path_alt;
    public long storeId;
    public String storeName;
    public String versionName;
    public String versionCode;
    public String md5sum;
    public long downloads;
    public float rating;
    public String modified;
    public String updated;

    public AppItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

}