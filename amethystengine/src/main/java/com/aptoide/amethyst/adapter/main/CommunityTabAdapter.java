package com.aptoide.amethyst.adapter.main;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.AppItem;
import com.aptoide.models.displayables.CommentItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ReviewRowItem;
import com.aptoide.models.displayables.CommentPlaceHolderRow;
import com.aptoide.models.displayables.ReviewPlaceHolderRow;

import java.util.List;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.main.EmptyViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.amethyst.viewholders.main.ReviewViewHolder;
import com.aptoide.amethyst.viewholders.main.TopAppViewHolder;
import com.aptoide.amethyst.viewholders.store.CommentViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class CommunityTabAdapter extends BaseAdapter implements SpannableRecyclerAdapter {
    private Activity activity;
    @ColorInt private int colorResId;

    /**
     * Community's adapter
     *
     * @param displayableList Displayable list to show
     * @param activity
     * @param colorResId      if colorResId < 0, the default color will be used
     */
    public CommunityTabAdapter(List<Displayable> displayableList, Activity activity, int colorResId) {
        super(displayableList);
        this.activity = activity;
        if (colorResId < 0 && activity != null) {
            this.colorResId = activity.getResources().getColor(R.color.default_color);
        } else {
            this.colorResId = colorResId;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BaseViewHolder holder;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);

        if (viewType == R.layout.layout_header) {
            holder = new HeaderViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else if (viewType == R.layout.top_app_row) {
            holder = new TopAppViewHolder(view, viewType);
        } else if (viewType == R.layout.comment_row) {
            holder = new CommentViewHolder(view, viewType, activity, colorResId);
        } else if (viewType == R.layout.row_review) {
            holder = new ReviewViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else if (viewType == R.layout.row_empty) {
            holder = new EmptyViewHolder(view, viewType);
        } else {
            throw new IllegalStateException(CommunityTabAdapter.class.getSimpleName() + " with unknown viewtype");
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {

        Displayable displayable = displayableList.get(position);
        if (displayable instanceof HeaderRow) {
            return R.layout.layout_header;
        } else if (displayable instanceof AppItem) {
            return R.layout.top_app_row;
        } else if (displayable instanceof CommentItem) {
            return R.layout.comment_row;
        } else if (displayable instanceof ReviewRowItem) {
            return R.layout.row_review;
        } else if (displayableList.get(position) instanceof ReviewPlaceHolderRow) {
            return R.layout.row_empty;
        } else if (displayableList.get(position) instanceof CommentPlaceHolderRow) {
            return R.layout.row_empty;
        } else {
            throw new IllegalStateException("This adapter doesn't know how to show " + displayableList.get(position).getClass().getName());
        }
    }

    @Override
    public int getSpanSize(int position) {
        return displayableList.get(position).getSpanSize();
    }
}
