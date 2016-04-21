package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 03-11-2015.
 */
public class MoreVersionsAppViewItem extends Displayable {

    public long id;
    public String appName;
    public String packageName;
    public String versionName;
    public String icon;
    public String storeName;
    public String storeAvatar;
    public String storeTheme;
    public long storeId;
    public int versionCode;


    public MoreVersionsAppViewItem(@JsonProperty("BUCKETSIZE") int numColumns) {
        super(numColumns);
        FULL_ROW = numColumns;
        setSpanSize(1);
    }

    @Override
    public int getSpanSize() {
        return 1;
    }
}
