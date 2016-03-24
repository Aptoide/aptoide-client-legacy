package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by gmartinsribeiro on 01/12/15.
 */
public class ScreenshotsViewHolder extends BaseViewHolder{

    public ImageView screenshot;
    public ImageView play_button;
    public FrameLayout media_layout;

    public ScreenshotsViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        screenshot = (ImageView )itemView.findViewById(R.id.screenshot_image_item);
        play_button = (ImageView )itemView.findViewById(R.id.play_button);
        media_layout = (FrameLayout )itemView.findViewById(R.id.media_layout);
    }
}
