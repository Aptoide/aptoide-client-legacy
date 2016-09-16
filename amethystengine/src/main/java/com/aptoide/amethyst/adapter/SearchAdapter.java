package com.aptoide.amethyst.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.MoreSearchActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.DummyBaseViewHolder;
import com.aptoide.amethyst.viewholders.ProgressBarRowViewHolder;
import com.aptoide.amethyst.viewholders.SearchAppViewHolder;
import com.aptoide.amethyst.viewholders.SponsoredAppViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SearchApp;
import com.aptoide.models.displayables.SearchMoreHeader;
import com.aptoide.models.displayables.SponsoredSearchApp;

import java.util.List;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<Displayable> list;
    private String query;

    public SearchAdapter(List<Displayable> list) {
        this.list = list;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (viewType == R.layout.search_app_row) {
            return new SearchAppViewHolder(view, viewType);
        } else if (viewType == R.layout.search_ad) {
            return new AdsViewHolder(view, viewType);
        } else if (viewType == R.layout.layout_header) {
            return new HeaderViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else if (viewType == R.layout.search_more_results) {
            return new DummyBaseViewHolder(view, viewType);
        } else if (viewType == R.layout.suggested_app_search) {
            return new SponsoredAppViewHolder(view, viewType);
        } else if (viewType == R.layout.row_progress_bar) {
            return new ProgressBarRowViewHolder(view, viewType);
        } else {
            throw new IllegalStateException(("This adapter doesn't know how to show viewtype " + viewType));
        }

    }

    @Override
    public void onBindViewHolder(final BaseViewHolder viewHolder, int position) {

        if (viewHolder.viewType == R.layout.layout_header) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            HeaderRow row = (HeaderRow) list.get(position);

            headerViewHolder.title.setText(row.getLabel());

            if (!row.isHasMore()) {
                headerViewHolder.more.setVisibility(View.GONE);
            }
        } else if (viewHolder.viewType == R.layout.search_app_row) {
            viewHolder.populateView(list.get(position));
        } else if (viewHolder.viewType == R.layout.search_more_results) {
            viewHolder.itemView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(viewHolder.itemView.getContext(), Aptoide.getConfiguration().getMoreSearchActivity());
                    i.putExtra(SearchActivity.SEARCH_QUERY, query);
                    viewHolder.itemView.getContext().startActivity(i);
                }
            });
        } else if (viewHolder.viewType == R.layout.suggested_app_search) {
            viewHolder.populateView(list.get(position));
        } else if (viewHolder.viewType == R.layout.row_progress_bar) {
            viewHolder.populateView(list.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof HeaderRow) {
            return R.layout.layout_header;
        } else if (list.get(position) instanceof SearchApp) {
            return R.layout.search_app_row;
        } else if (list.get(position) instanceof SearchMoreHeader) {
            return R.layout.search_more_results;
        } else if (list.get(position) instanceof SponsoredSearchApp) {
            return R.layout.suggested_app_search;
        } else if (list.get(position) instanceof ProgressBarRow) {
            return R.layout.row_progress_bar;
        } else if (list.get(position) instanceof DummyDisplayable) {
            return R.layout.search_ad;
        } else {
            throw new IllegalStateException("This adapter doesn't know how to show " + list.get(position).getClass().getName());
        }
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
