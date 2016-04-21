package com.aptoide.amethyst.viewholders.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class HeaderViewHolder extends BaseViewHolder {

    private final EnumStoreTheme theme;
    private final String storeName;
    private final long storeId;

    public TextView title;
    public Button more;
    public RelativeLayout moreLayout;

    public HeaderViewHolder(View itemView, int viewType, EnumStoreTheme theme) {
        super(itemView, viewType);
        this.theme = theme;
        storeName = null;
        storeId = 0;
    }

    public HeaderViewHolder(View itemView, int viewType, EnumStoreTheme theme, String storeName, long storeId) {
        super(itemView, viewType);
        this.theme = theme;
        this.storeName = storeName;
        this.storeId = storeId;
    }

    @Override
    public void populateView(Displayable displayable) {
        HeaderRow row = (HeaderRow) displayable;
        title.setText(row.getLabel());
        if (row.isHasMore()) {
            more.setVisibility(View.VISIBLE);
            if (storeName == null || TextUtils.isEmpty(storeName)) {
                if (storeId == 0) {
                    more.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme));
                    moreLayout.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme));
                } else {
                    more.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme,storeId));
                    moreLayout.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme,storeId));
                }
            } else {
                if (storeId == 0) {
                    more.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme, storeName));
                    moreLayout.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme, storeName));
                } else {
                    more.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme, storeName,storeId));
                    moreLayout.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, theme, storeName,storeId));
                }
            }
        } else {
            more.setVisibility(View.GONE);
            moreLayout.setClickable(false);
            moreLayout.setFocusable(false);
        }

        if (theme != null) {
            @ColorInt int color = itemView.getContext().getResources().getColor(theme.getStoreHeader());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                more.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else {

                // put this in Utils when you need to tint a Button background
                Drawable wrapDrawable = DrawableCompat.wrap(more.getBackground());
                DrawableCompat.setTint(wrapDrawable, itemView.getContext().getResources().getColor(theme.getStoreHeader()));
                more.setBackgroundDrawable(DrawableCompat.unwrap(wrapDrawable));
            }
        }
    }

    @Override
    protected void bindViews(View itemView) {
        title = (TextView )itemView.findViewById(R.id.title);
        more = (Button )itemView.findViewById(R.id.more);
        moreLayout = (RelativeLayout )itemView.findViewById(R.id.more_layout);
    }
}