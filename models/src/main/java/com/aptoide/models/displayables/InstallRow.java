package com.aptoide.models.displayables;

import com.aptoide.models.displayables.AppItem;

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
