package com.aptoide.amethyst;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.utils.AptoideUtils;

/**
 * Created by rmateus on 01/06/15.
 */
public abstract class AptoideRecyclerFragment extends AptoideSpicedBaseFragment {

    @Nullable
    private int recyclerViewOffset;

    private RecyclerView recyclerView;
    protected int BUCKET_SIZE;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerViewOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getActivity().getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        this.recyclerView = recycler;
        BUCKET_SIZE = AptoideUtils.UI.getBucketSize();

        setLayoutManager(recycler);

        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

                outRect.set(recyclerViewOffset, recyclerViewOffset, recyclerViewOffset, recyclerViewOffset);

            }
        });
    }

    @Override
    public void onDestroyView() {
        recyclerView = null;
        super.onDestroyView();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public abstract void setLayoutManager(RecyclerView recyclerView);

    public void setRecyclerAdapter(RecyclerView.Adapter adapter) {
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    protected int getColumnSize() {
        return BUCKET_SIZE;
    }

    protected int getColumnMultiplier() {
        return 2;
    }

}
