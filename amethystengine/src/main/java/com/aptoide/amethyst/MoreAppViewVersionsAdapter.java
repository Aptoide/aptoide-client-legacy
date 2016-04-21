package com.aptoide.amethyst;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.MoreVersionsAppViewItem;
import com.bumptech.glide.Glide;

import java.util.List;

import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.appview.MoreAppViewVersionsHolder;

/**
 * Created by hsousa on 03/12/15.
 *
 */
public class MoreAppViewVersionsAdapter extends RecyclerView.Adapter<MoreAppViewVersionsHolder> implements SpannableRecyclerAdapter {

    public List<MoreVersionsAppViewItem> list;

    public MoreAppViewVersionsAdapter(List<MoreVersionsAppViewItem> list) {
        this.list = list;
    }

    @Override
    public MoreAppViewVersionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.this_app_in_other_stores_layout, parent, false);

        return new MoreAppViewVersionsHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final MoreAppViewVersionsHolder holder, int position) {
        final MoreVersionsAppViewItem item = list.get(position);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(holder.itemView.getContext().getResources().getColor(EnumStoreTheme.get(item.storeTheme).getStoreHeader()));
        gd.setCornerRadius(5);
        holder.mContent.setBackgroundDrawable(gd);

//        holder.mAppName.setText(item.appName);
//        holder.mAppVersion.setText(AptoideUtils.StringUtils.getFormattedString(holder.itemView.getContext() ,R.string.version_placeholder, item.versionName));
        holder.mAppVersion.setText(item.versionName);
        holder.mStoreName.setText(item.storeName);
        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(), AppViewActivity.class);
                i.putExtra(Constants.FROM_RELATED_KEY, true);
                i.putExtra(Constants.APP_ID_KEY, item.id);
                i.putExtra(Constants.APPNAME_KEY, item.appName);
                i.putExtra(Constants.STOREID_KEY, item.storeId);
                i.putExtra(Constants.STORENAME_KEY, item.storeName);
                i.putExtra(Constants.STOREAVATAR_KEY, item.storeAvatar);
                i.putExtra(Constants.PACKAGENAME_KEY, item.packageName);
                i.putExtra(Constants.DOWNLOAD_FROM_KEY, "app_view_more_multiversion");
                holder.itemView.getContext().startActivity(i);
            }
        });

        if (TextUtils.isEmpty(item.icon)) {
            Glide.with(holder.itemView.getContext()).fromResource().load(R.drawable.ic_avatar_apps).transform(new CircleTransform(holder.itemView.getContext())).into(holder.mAvatarApp);
        } else {
            Glide.with(holder.itemView.getContext()).load(item.icon).transform(new CircleTransform(holder.itemView.getContext())).into(holder.mAvatarApp);
        }


        if (TextUtils.isEmpty(item.storeAvatar)) {
            Glide.with(holder.itemView.getContext()).fromResource().load(R.drawable.ic_avatar_apps).transform(new CircleTransform(holder.itemView.getContext())).into(holder.mAvatarStore);
        } else {
            Glide.with(holder.itemView.getContext()).load(item.storeAvatar).transform(new CircleTransform(holder.itemView.getContext())).into(holder.mAvatarStore);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getSpanSize(int position) {
        return list.get(position).getSpanSize();
    }
}
