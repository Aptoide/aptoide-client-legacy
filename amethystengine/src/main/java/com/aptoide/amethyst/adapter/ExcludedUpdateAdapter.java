package com.aptoide.amethyst.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.models.displayables.ExcludedUpdate;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.ExcludedUpdateViewHolder;

/**
 * Created by hsousa on 26/06/15.
 */
public class ExcludedUpdateAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final ArrayList<ExcludedUpdate> list;
    private String sizeString;

    public ExcludedUpdateAdapter(ArrayList<ExcludedUpdate> list) {
        this.list = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        sizeString = IconSizeUtils.generateSizeString(parent.getContext());
        return new ExcludedUpdateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_excluded_update, null), viewType);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder viewHolder, int position) {

        final ExcludedUpdateViewHolder item = (ExcludedUpdateViewHolder) viewHolder;
        final ExcludedUpdate appItem = list.get(position);

        item.cb_exclude.setChecked(appItem.isChecked());
        item.cb_exclude.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                appItem.setChecked(item.cb_exclude.isChecked());
            }
        });

        String iconString = appItem.getIcon();
        if (iconString.contains("_icon")) {
            String[] splittedUrl = iconString.split("\\.(?=[^\\.]+$)");
            iconString = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
        }

        Glide.with(viewHolder.itemView.getContext()).load(iconString).into(item.app_icon);
        item.tv_name.setText(appItem.getName());
        item.tv_vercode.setText(appItem.getVersionName());
        item.tv_apkid.setText(appItem.getApkid());

        item.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.cb_exclude.setChecked(!appItem.isChecked());
                appItem.setChecked(!appItem.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected void setAllChecked(final boolean checked) {
        for (ExcludedUpdate excludedUpdate : list) {
            excludedUpdate.setChecked(checked);
        }

        notifyDataSetChanged();
    }

    public void selectAll() {
        setAllChecked(true);
    }

    public void selectNone() {
        setAllChecked(false);
    }
}