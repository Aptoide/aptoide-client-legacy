package com.aptoide.download_manager.adapter;

import com.aptoide.download_manager.model.Download;
import com.aptoide.models.displayables.DownloadRow;

/**
 * Created by hsousa on 13-07-2015.
 */
public class OngoingDownloadRow extends DownloadRow {

    public OngoingDownloadRow(Download download, int bucketSize) {
        super(download, bucketSize);
    }

}