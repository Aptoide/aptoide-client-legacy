package com.aptoide.models.displayables;

import com.aptoide.download_manager.model.Download;

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