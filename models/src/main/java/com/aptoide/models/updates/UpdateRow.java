package com.aptoide.models.updates;

import com.aptoide.models.AppItem;

/**
 * Created by rmateus on 17/06/15.
 */
public class UpdateRow extends AppItem {

    public String packageName;
    public String versionName;
    public String versionNameInstalled;
    public int versionCode;

    public UpdateRow(int bucketSize) {
        super(bucketSize);
    }
}
