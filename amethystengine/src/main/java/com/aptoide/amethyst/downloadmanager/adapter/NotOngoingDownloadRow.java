package com.aptoide.amethyst.downloadmanager.adapter;

import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.models.displayables.DownloadRow;

/**
 * In this row there can be ERROR and COMPLETE status.
 * Created by hsousa on 13-07-2015.
 */
public class NotOngoingDownloadRow extends DownloadRow {

    public NotOngoingDownloadRow(Download download, int bucketSize) {
        super(download, bucketSize);
    }

}