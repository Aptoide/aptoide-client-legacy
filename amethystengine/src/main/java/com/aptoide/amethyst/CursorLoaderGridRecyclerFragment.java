package com.aptoide.amethyst;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.utils.AptoideUtils;

/**
 * Created by hsousa on 26-06-2015.
 */
public class CursorLoaderGridRecyclerFragment extends CursorLoaderRecyclerFragment {

    @Override
    public void setLayoutManager(final RecyclerView recyclerView) {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), getColumnSize() * getColumnMultiplier());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(!(recyclerView.getAdapter() instanceof SpannableRecyclerAdapter)){
                    throw new IllegalStateException("RecyclerView adapter must extend SpannableRecyclerAdapter");
                }

                return AptoideUtils.UI.getSpanSize(recyclerView, position);
            }
        });

        gridLayoutManager.setSpanCount(getColumnSize() * getColumnMultiplier());
        recyclerView.setLayoutManager(gridLayoutManager);
    }
}
