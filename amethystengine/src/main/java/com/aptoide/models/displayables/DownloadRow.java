package com.aptoide.models.displayables;

import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.models.displayables.AppItem;

/**
 * Created by hsousa on 13-07-2015.
 */
public class DownloadRow extends AppItem {

    public Download download;

    public DownloadRow(Download download, int bucketSize) {
        super(bucketSize);
        this.download = download;
    }

}