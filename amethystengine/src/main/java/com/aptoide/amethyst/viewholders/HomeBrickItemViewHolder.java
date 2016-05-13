package com.aptoide.amethyst.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.BrickAppItem;
import com.aptoide.models.displayables.Displayable;
import com.bumptech.glide.Glide;


import com.aptoide.amethyst.adapter.BaseAdapter;

/**
 * Created by hsousa on 20/10/15.
 */
public class HomeBrickItemViewHolder extends BaseViewHolder {

    public TextView name;
    public ImageView graphic;
    public RatingBar ratingBar;


    public HomeBrickItemViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
//        HomeBrickItemViewHolder holder = (HomeBrickItemViewHolder) viewHolder;
        BrickAppItem appItem = (BrickAppItem) displayable;

        name.setText(appItem.appName);
//                downloads.setText(withSuffix(appItem.downloads) + " downloads");
//                ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(appItem.rating);
        itemView.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
        Glide.with(itemView.getContext()).load(appItem.featuredGraphic).placeholder(R.drawable.placeholder_705x345).into(graphic);

    }

    @Override
    protected void bindViews(View itemView) {
        name = (TextView )itemView.findViewById(R.id.app_name);
        graphic = (ImageView )itemView.findViewById(R.id.featured_graphic);
        ratingBar = (RatingBar )itemView.findViewById(R.id.ratingbar);
    }
}