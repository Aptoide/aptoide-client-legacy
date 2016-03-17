package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 13/07/15.
 */
public class DownloadViewHolder extends BaseViewHolder {

    public ImageView appIcon;
    public TextView appName;
    public ProgressBar downloadingProgress;
    public RelativeLayout downloadDetails;
    public TextView speed;
    public TextView eta;
    public TextView progress;
    public TextView appError;
    public View view;
    public ImageView manageIcon;
    public RelativeLayout layout;

    public DownloadViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        appIcon = (ImageView )itemView.findViewById(R.id.app_icon);
        appName = (TextView )itemView.findViewById(R.id.app_name);
        downloadingProgress = (ProgressBar )itemView.findViewById(R.id.downloading_progress);
        downloadDetails = (RelativeLayout )itemView.findViewById(R.id.download_details_layout);
        speed = (TextView )itemView.findViewById(R.id.speed);
        eta = (TextView )itemView.findViewById(R.id.eta);
        progress = (TextView )itemView.findViewById(R.id.progress);
        appError = (TextView )itemView.findViewById(R.id.app_error);
        view = (View )itemView.findViewById(R.id.view);
        manageIcon = (ImageView )itemView.findViewById(R.id.manage_icon);
        layout = (RelativeLayout )itemView.findViewById(R.id.row_app_download_indicator);
    }
}