package com.aptoide.amethyst.viewholders.main;

import android.text.Html;
import android.view.View;

import com.aptoide.amethyst.downloadmanager.adapter.NotOngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.downloadmanager.state.EnumState;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.models.displayables.Displayable;
import com.bumptech.glide.Glide;

/**
 * Created by hsousa on 13/07/15.
 */
public class NotOnGoingDownloadViewHolder extends DownloadViewHolder {

    public NotOnGoingDownloadViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
//        NotOnGoingDownloadViewHolder notOnGoingHolder = (NotOnGoingDownloadViewHolder) holder;
        final Download download = ((NotOngoingDownloadRow) displayable).download;

        appName.setText(download.getName() == null ? "" : Html.fromHtml(download.getName()));
        Glide.with(appName.getContext()).load(download.getIcon()).into(appIcon);
        downloadingProgress.setVisibility(View.GONE);
        manageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download.getParent().remove(false);
            }
        });

        EnumState downloadState = download.getDownloadState();
        switch(downloadState){
            case ERROR:
                appError.setText(download.getParent().getFailReason().toString(appError.getContext()));
                appError.setVisibility(View.VISIBLE);
                break;
            case COMPLETE:
                appError.setVisibility(View.GONE);
                break;
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new OttoEvents.InstallAppFromManager(download.getId()));
            }
        });
    }
}