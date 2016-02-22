package com.aptoide.amethyst;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.utils.AptoideUtils;

/**
 * Created by rmateus on 05/06/15.
 */
public abstract class GridRecyclerFragment extends AptoideRecyclerFragment {


    @Override
    public void setLayoutManager(final RecyclerView recyclerView) {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), getColumnSize() * 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(!(recyclerView.getAdapter() instanceof SpannableRecyclerAdapter)){
                    throw new IllegalStateException("RecyclerView adapter must implement SpannableRecyclerAdapter");
                }

                return AptoideUtils.UI.getSpanSize(recyclerView, position);
            }
        });

        // we need to force the spanCount, or it will crash.
        // https://code.google.com/p/android/issues/detail?id=182400
        gridLayoutManager.setSpanCount(getColumnSize()* 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}
