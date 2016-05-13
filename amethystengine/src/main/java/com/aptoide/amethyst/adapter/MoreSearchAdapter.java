package com.aptoide.amethyst.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.SearchApk;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SuggestedAppDisplayable;

import java.util.List;


import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.ProgressBarRowViewHolder;
import com.aptoide.amethyst.viewholders.SearchAppViewHolder;
import com.aptoide.amethyst.viewholders.SuggestedAppViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;

/**
 * Created by fabio on 20-11-2015.
 */
public class MoreSearchAdapter extends BaseAdapter implements SpannableRecyclerAdapter {
    public MoreSearchAdapter(List<Displayable> displayableList) {
        super(displayableList);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (viewType == R.layout.search_app_row) {
            return new SearchAppViewHolder(view, viewType);
        } else if (viewType == R.layout.layout_header) {
            return new HeaderViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else if (viewType == R.layout.suggested_app_search) {
            return new SuggestedAppViewHolder(view, viewType);
        } else if (viewType == R.layout.row_progress_bar) {
            return new ProgressBarRowViewHolder(view, viewType);
        } else {
            throw new IllegalStateException(("This adapter doesn't know how to show viewtype " + viewType));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (displayableList.get(position) instanceof HeaderRow) {
            return R.layout.layout_header;
        } else if (displayableList.get(position) instanceof SearchApk) {
            return R.layout.search_app_row;
        } else if (displayableList.get(position) instanceof SuggestedAppDisplayable) {
            return R.layout.suggested_app_search;
        } else if (displayableList.get(position) instanceof ProgressBarRow) {
            return R.layout.row_progress_bar;
        } else {
            throw new IllegalStateException("This adapter doesn't know how to show " + displayableList.get(position).getClass().getName());
        }
    }

    @Override
    public int getSpanSize(int position) {
        return displayableList.get(position).getSpanSize();
    }
    @Override
    public int getItemCount() {
        return displayableList.size();
    }
}
