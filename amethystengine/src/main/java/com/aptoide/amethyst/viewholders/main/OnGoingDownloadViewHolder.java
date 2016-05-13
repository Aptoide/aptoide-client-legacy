package com.aptoide.amethyst.viewholders.main;

import android.text.Html;
import android.view.View;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.downloadmanager.adapter.OngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.displayables.Displayable;
import com.bumptech.glide.Glide;



/**
 * Created by hsousa on 13/07/15.
 */
public class OnGoingDownloadViewHolder extends DownloadViewHolder {

    public OnGoingDownloadViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
//        OnGoingDownloadViewHolder onGoingHolder = (OnGoingDownloadViewHolder) holder;
        final Download download = ((OngoingDownloadRow) displayable).download;

        appName.setText(download.getName() == null ? "" : Html.fromHtml(download.getName()));
        Glide.with(appName.getContext()).load(download.getIcon()).into(appIcon);
        downloadingProgress.setIndeterminate(false);
        downloadingProgress.setProgress(download.getProgress());
        downloadDetails.setVisibility(View.VISIBLE);
        speed.setText(AptoideUtils.StringUtils.formatBits((long) download.getSpeed()));
        eta.setText(AptoideUtils.StringUtils.formatEta(download.getTimeLeft(), appName.getContext().getString(R.string.remaining_time)));
        progress.setText(download.getProgress() + "%");
        manageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download.getParent().remove(false);
            }
        });

    }
}