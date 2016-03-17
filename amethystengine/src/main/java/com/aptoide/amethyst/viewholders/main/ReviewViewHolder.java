package com.aptoide.amethyst.viewholders.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.ReviewRowItem;
import com.bumptech.glide.Glide;


import com.aptoide.amethyst.ui.ReviewActivity;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 24/09/15.
 */
public class ReviewViewHolder extends BaseViewHolder {

    private final EnumStoreTheme theme;
    public ImageView appIcon;
    public TextView rating;
    public TextView appName;
    public ImageView avatar;
    public TextView reviewer;
    public TextView description;
    public FrameLayout score;

    public ReviewViewHolder(View itemView, int viewType, EnumStoreTheme theme) {
        super(itemView, viewType);
        this.theme = theme;
    }

    @Override
    public void populateView(Displayable displayable) {
//        ReviewViewHolder holder = (ReviewViewHolder) viewHolder;
        final ReviewRowItem appItem = (ReviewRowItem) displayable;
        final Context context = itemView.getContext();

        appName.setText(appItem.appName);
        description.setText(appItem.description);
        reviewer.setText(AptoideUtils.StringUtils.getFormattedString(context, R.string.reviewed_by, appItem.reviewer));
        rating.setText(AptoideUtils.StringUtils.getRoundedValueFromDouble(appItem.rating));
        Glide.with(context).load(appItem.appIcon).into(appIcon);
        Glide.with(context).load(appItem.avatar).transform(new CircleTransform(context)).into(avatar);

        if(theme != null) {
            @ColorInt int color = context.getResources().getColor(theme.getStoreHeader());
            score.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("review_id", appItem.reviewId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    protected void bindViews(View itemView) {
        appIcon = (ImageView )itemView.findViewById(R.id.app_icon);
        rating = (TextView )itemView.findViewById(R.id.rating);
        appName = (TextView )itemView.findViewById(R.id.app_name);
        avatar = (ImageView )itemView.findViewById(R.id.avatar);
        reviewer = (TextView )itemView.findViewById(R.id.reviewer);
        description = (TextView )itemView.findViewById(R.id.description);
        score = (FrameLayout )itemView.findViewById(R.id.score);
    }
}
