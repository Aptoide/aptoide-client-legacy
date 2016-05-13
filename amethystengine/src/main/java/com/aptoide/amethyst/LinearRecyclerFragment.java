package com.aptoide.amethyst;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by rmateus on 05/06/15.
 */
public class LinearRecyclerFragment extends AptoideRecyclerFragment {

    @Override
    public void setLayoutManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }
}
