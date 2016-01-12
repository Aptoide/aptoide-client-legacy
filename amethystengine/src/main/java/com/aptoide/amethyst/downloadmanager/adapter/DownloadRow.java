package com.aptoide.amethyst.downloadmanager.adapter;

import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.models.AppItem;

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