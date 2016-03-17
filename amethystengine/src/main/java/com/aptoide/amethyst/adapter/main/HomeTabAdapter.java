package com.aptoide.amethyst.adapter.main;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.Displayable;

import java.util.List;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.HomeBrickItemViewHolder;
import com.aptoide.amethyst.viewholders.HomeGridItemViewHolder;
import com.aptoide.amethyst.viewholders.ProgressBarRowViewHolder;
import com.aptoide.amethyst.viewholders.main.AdultRowViewHolder;
import com.aptoide.amethyst.viewholders.main.EditorsChoiceViewHolder;
import com.aptoide.amethyst.viewholders.main.EmptyViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.amethyst.viewholders.main.HomeCategoryViewHolder;
import com.aptoide.amethyst.viewholders.main.ReviewViewHolder;
import com.aptoide.amethyst.viewholders.main.StoreItemRowViewHolder;
import com.aptoide.amethyst.viewholders.main.TimelineViewHolder;
import com.aptoide.amethyst.viewholders.store.StoreHeaderViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class HomeTabAdapter extends BaseAdapter implements SpannableRecyclerAdapter {

    /**
     * flag used to change the Subscribe button. since that info comes from SQLite
     */
    private boolean subscribed;
    private String storeName;
    private long storeId;

    /**
     * Necessary to show the AdultDialog
     */
    private FragmentManager fragmentManager;

    /**
     * Used on the Store's fragments. We'll also use to tint the review rating button
     */
    private EnumStoreTheme theme;


    public HomeTabAdapter(List<Displayable> displayableList, EnumStoreTheme theme, boolean subscribed,String storeName) {
        super(displayableList);
        this.theme = theme;
        this.subscribed = subscribed;
        this.storeName = storeName;
    }

    public HomeTabAdapter(List<Displayable> displayableList, FragmentManager fragmentManager, EnumStoreTheme theme) {
        super(displayableList);
        this.fragmentManager = fragmentManager;
        this.theme = theme;
    }

    public HomeTabAdapter(List<Displayable> displayableList, FragmentManager fragmentManager, EnumStoreTheme theme, String storeName) {
        super(displayableList);
        this.fragmentManager = fragmentManager;
        this.theme = theme;
        this.storeName = storeName;
    }

    public HomeTabAdapter(List<Displayable> displayableList, FragmentManager fragmentManager, EnumStoreTheme theme, String storeName, long storeId) {
        super(displayableList);
        this.fragmentManager = fragmentManager;
        this.theme = theme;
        this.storeName = storeName;
        this.storeId = storeId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);

        BaseViewHolder holder;
        if (viewType == R.layout.layout_header) {
            holder = new HeaderViewHolder(view, viewType, theme, storeName, storeId);
        } else if (viewType == R.layout.grid_item) {
            holder = new HomeGridItemViewHolder(view, viewType);
        } else if (viewType == R.layout.row_store_header) {
            holder = new StoreHeaderViewHolder(view, viewType, subscribed, theme);
        } else if (viewType == R.layout.editors_choice_row) {
            holder = new EditorsChoiceViewHolder(view, viewType);
        } else if (viewType == R.layout.row_review) {
            holder = new ReviewViewHolder(view, viewType, theme);
        } else if (viewType == R.layout.row_empty) {
            holder = new EmptyViewHolder(view, viewType);
        } else if (viewType == R.layout.row_category_home_item) {
            holder = new HomeCategoryViewHolder(view, viewType, theme);
        } else if (viewType == R.layout.timeline_item) {
            holder = new TimelineViewHolder(view, viewType);
        } else if (viewType == R.layout.row_store_item) {
            holder = new StoreItemRowViewHolder(view, viewType);
        } else if (viewType == R.layout.row_adult_switch) {
            holder = new AdultRowViewHolder(view, viewType, fragmentManager);
        } else if (viewType == R.layout.brick_app_item) {
            holder = new HomeBrickItemViewHolder(view, viewType);
        } else if (viewType == R.layout.row_progress_bar) {
            holder = new ProgressBarRowViewHolder(view, viewType);
        } else {
            throw new IllegalStateException("HomeTabAdapter with unknown viewtype");
        }

        return holder;
    }

    @Override
    public int getSpanSize(int position) {
        if (position >= displayableList.size() || position < 0) {
            return 1;
        } else {
            return displayableList.get(position).getSpanSize();
        }
    }
}
