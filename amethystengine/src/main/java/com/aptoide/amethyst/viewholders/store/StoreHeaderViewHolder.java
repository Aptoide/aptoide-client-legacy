package com.aptoide.amethyst.viewholders.store;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.StoreHeaderRow;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.displayables.Displayable;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


import com.aptoide.amethyst.adapter.main.StoresTabAdapter;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class StoreHeaderViewHolder extends BaseViewHolder {

    public ImageView avatar;
    public ImageView ivSubscribe;
    public TextView name;
    public TextView description;
    public TextView subscribed;
    public LinearLayout subscribeButtonLayout;
    public TextView subscribersCount;
    public TextView appsCount;
    public TextView downloadsCount;

    private final EnumStoreTheme theme;
    private boolean subscribedBool;

    public StoreHeaderViewHolder(View itemView, int viewType, boolean subscribed, EnumStoreTheme theme) {
        super(itemView, viewType);
        this.subscribedBool = subscribed;
        this.theme = theme;
    }

    @Override
    public void populateView(Displayable displayable) {
//        final StoreHeaderViewHolder holder = (StoreHeaderViewHolder) viewHolder;
        final StoreHeaderRow row = (StoreHeaderRow) displayable;

        final Context context = itemView.getContext();
        if (row.id == -1 || TextUtils.isEmpty(row.avatar)) {
            Glide.with(context).fromResource().load(R.drawable.ic_avatar_apps)
                    .transform(new CircleTransform(context)).into(avatar);
        } else {
            Glide.with(context).load(row.avatar).transform(new CircleTransform(context)).into(avatar);
        }

        @ColorInt int color = context.getResources().getColor(theme.getStoreHeader());
        name.setText(row.name);
        name.setTextColor(color);
        description.setText(row.description);
        appsCount.setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(row.apps));
        downloadsCount.setText(AptoideUtils.StringUtils.withDecimalSuffix(row.downloads));
        subscribersCount.setText(AptoideUtils.StringUtils.withDecimalSuffix(row.subscribers));
        subscribeButtonLayout.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        if (subscribedBool) {

            ivSubscribe.setImageResource(R.drawable.ic_check_white);
            subscribed.setText(context.getString(R.string.appview_subscribed_store_button_text));
            subscribeButtonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscribedBool = false;
                    Toast.makeText(itemView.getContext(), AptoideUtils.StringUtils.getFormattedString(itemView.getContext(), R.string.subscribing_store_message,row.name), Toast.LENGTH_SHORT).show();
                    ArrayList<Long> sotoreIds = new ArrayList<>();
                    sotoreIds.add(row.id);
                    StoresTabAdapter.removeStores(sotoreIds);
                    subscribeButtonLayout.setEnabled(false);
                }
            });
        } else {
            ivSubscribe.setImageResource(R.drawable.ic_plus_white);
            subscribed.setText(context.getString(R.string.appview_subscribe_store_button_text));
            subscribed.setCompoundDrawables(null, null, null, null);
            /*Drawable drawableLeft = itemView.getContext().getResources().getDrawable(R.drawable.ic_action_cancel_small_dark);
            if (drawableLeft != null) {
                drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
                subscribed.setCompoundDrawables(drawableLeft, null, null, null);
            }*/
            subscribeButtonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!subscribedBool) {
                        subscribedBool = true;
                        BusProvider.getInstance().post(new OttoEvents.RepoSubscribeEvent(row.name));
                        subscribeButtonLayout.setEnabled(false);
                    }
                }
            });
        }

    }

    @Override
    protected void bindViews(View itemView) {
        avatar = (ImageView )itemView.findViewById(R.id.store_avatar_storehome);
        ivSubscribe = (ImageView )itemView.findViewById(R.id.iv_subscribed_icon);
        name = (TextView )itemView.findViewById(R.id.store_name_home_row);
        description = (TextView )itemView.findViewById(R.id.store_description_storehome);
        subscribed = (TextView )itemView.findViewById(R.id.store_subscribed_storehome);
        subscribeButtonLayout = (LinearLayout )itemView.findViewById(R.id.subscribe_button_layout);
        subscribersCount = (TextView )itemView.findViewById(R.id.store_subscribers_count);
        appsCount = (TextView )itemView.findViewById(R.id.store_apps_count);
        downloadsCount = (TextView )itemView.findViewById(R.id.store_downloads_count);
    }
}