package com.aptoide.amethyst.viewholders.main;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.models.displayables.AppItem;
import com.aptoide.models.displayables.Displayable;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class TopAppViewHolder extends BaseViewHolder {

    public TextView name;
    public ImageView icon;
    public TextView tvTimeSinceModified;
    public TextView tvStoreName;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public TopAppViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
//        TopAppViewHolder item = (TopAppViewHolder) holder;

        final AppItem appItem = (AppItem) displayable;
        name.setText(appItem.appName);

        Date modified = null;
        try {
            modified = dateFormatter.parse(appItem.updated);
        }
        catch(ParseException e) {
            dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            try {
                modified = dateFormatter.parse(appItem.updated);
            } catch (ParseException e1) {
                Crashlytics.log(Log.DEBUG, "ParseException", "Locale: " + Locale.getDefault() + " Error: " + e1.getMessage());
                Logger.printException(e1);
            }
        } finally {
            if (modified != null) {
                tvTimeSinceModified.setText(AptoideUtils.DateTimeUtils.getInstance(itemView.getContext()).getTimeDiffString(itemView.getContext(), modified.getTime()));
            }
        }

        name.setText(appItem.appName);
        name.setTypeface(null, Typeface.BOLD);

        tvStoreName.setText(appItem.storeName);
        tvStoreName.setTypeface(null, Typeface.BOLD);
        itemView.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
        Glide.with(itemView.getContext()).load(appItem.icon).into(icon);
    }

    @Override
    protected void bindViews(View itemView) {
        name = (TextView )itemView.findViewById(R.id.name);
        icon = (ImageView )itemView.findViewById(R.id.icon);
        tvTimeSinceModified = (TextView )itemView.findViewById(R.id.timeSinceModified);
        tvStoreName = (TextView )itemView.findViewById(R.id.storeName);
    }
}
