package com.aptoide.amethyst.viewholders.appview;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 03/12/15.
 */
public class MoreAppViewVersionsHolder extends BaseViewHolder {

    public RelativeLayout mContent;
    public ImageView mAvatarStore;
    public ImageView mAvatarApp;
    public TextView mStoreName;
    public TextView mAppVersion;

    public MoreAppViewVersionsHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        mContent = (RelativeLayout )itemView.findViewById(R.id.other_stores_content);
        mAvatarStore = (ImageView )itemView.findViewById(R.id.other_stores_avatar_store);
        mAvatarApp = (ImageView )itemView.findViewById(R.id.other_stores_avatar_app);
        mStoreName = (TextView )itemView.findViewById(R.id.other_stores_name);
        mAppVersion = (TextView )itemView.findViewById(R.id.other_stores_app_version);
    }
}
