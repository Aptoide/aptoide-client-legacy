package com.aptoide.amethyst.viewholders.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HomeStoreItem;
import com.bumptech.glide.Glide;


import com.aptoide.amethyst.StoresActivity;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 22-06-2015.
 */
public class StoreItemRowViewHolder extends BaseViewHolder {

    public ImageView storeAvatar;
    public TextView storeName;
    public TextView storeUnsubscribe;
    public LinearLayout storeLayout;
    public TextView storeSubscribers;
    public TextView storeDownloads;
    public LinearLayout infoLayout;

    public StoreItemRowViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
        final HomeStoreItem storeItem = (HomeStoreItem) displayable;
//        final StoreItemRowViewHolder holder = (StoreItemRowViewHolder) viewHolder;

        final EnumStoreTheme themeIz = EnumStoreTheme.get(storeItem.theme);

        storeName.setText(storeItem.repoName);
        storeDownloads.setText(AptoideUtils.StringUtils.withSuffix(storeItem.storeDwnNumber));
        storeSubscribers.setText(AptoideUtils.StringUtils.withSuffix(storeItem.storeSubscribers));

        // in order to re-use the row_store_item layout, we hide the unsubscribe button and increase the padding
        storeUnsubscribe.setVisibility(View.GONE);

        final Context context = itemView.getContext();
        @ColorInt int color = context.getResources().getColor(themeIz.getStoreHeader());
        storeLayout.setBackgroundColor(color);
        storeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), StoresActivity.class);
                intent.putExtra(Constants.STOREID_KEY, storeItem.id);
                intent.putExtra(Constants.STORENAME_KEY, storeItem.repoName);
                intent.putExtra(Constants.STOREAVATAR_KEY, storeItem.avatar);
                intent.putExtra(Constants.THEME_KEY, themeIz.ordinal());
                intent.putExtra(Constants.DOWNLOAD_FROM_KEY, "store");
                boolean subscribed = new AptoideDatabase(Aptoide.getDb()).existsStore(storeItem.id);
                intent.putExtra(Constants.STORE_SUBSCRIBED_KEY, subscribed);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                view.getContext().startActivity(intent);
            }
        });

        if (storeItem.id == -1 || TextUtils.isEmpty(storeItem.avatar)) {
            Glide.with(context).fromResource().load(R.drawable.ic_avatar_apps)
                    .transform(new CircleTransform(context)).into(storeAvatar);
        } else {
            Glide.with(context).load(storeItem.avatar).transform(new CircleTransform(context))
                    .into(storeAvatar);
        }

    }

    @Override
    protected void bindViews(View itemView) {
        storeAvatar = (ImageView )itemView.findViewById(R.id.store_avatar_row);
        storeName = (TextView )itemView.findViewById(R.id.store_name_row);
        storeUnsubscribe = (TextView )itemView.findViewById(R.id.store_unsubscribe_row);
        storeLayout = (LinearLayout )itemView.findViewById(R.id.store_main_layout_row);
        storeSubscribers = (TextView )itemView.findViewById(R.id.store_subscribers);
        storeDownloads = (TextView )itemView.findViewById(R.id.store_downloads);
        infoLayout = (LinearLayout )itemView.findViewById(R.id.store_layout_subscribers);
    }
}