package com.aptoide.models.updates;

import com.aptoide.models.AppItem;

/**
 * Created by rmateus on 17/06/15.
 */
public class InstallRow extends AppItem {

    public String versionName;
    public long firstInstallTime;

    public InstallRow(int bucketSize) {
        super(bucketSize);
    }
}
