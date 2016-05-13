package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 17/06/15.
 */
public class UpdateViewHolder extends BaseViewHolder {

    public TextView name;
    public ImageView icon;
    public TextView appInstalledVersion;
    public TextView appUpdateVersion;
    public LinearLayout updateButtonLayout;

    public UpdateViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        name = (TextView)itemView.findViewById(R.id.name);
        icon = (ImageView)itemView.findViewById(R.id.icon);
        appInstalledVersion = (TextView)itemView.findViewById(R.id.app_installed_version);
        appUpdateVersion = (TextView)itemView.findViewById(R.id.app_update_version);
        updateButtonLayout = (LinearLayout)itemView.findViewById(R.id.updateButtonLayout);
    }
}
