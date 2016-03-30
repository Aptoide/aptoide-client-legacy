package com.aptoide.amethyst.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.dataprovider.webservices.json.review.Review;
import com.aptoide.dataprovider.webservices.json.review.ReviewListJson;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.ReviewRowItem;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

public abstract class ReviewsFragment extends BaseWebserviceFragment {

    // on v6, 50 was the limit
    protected static final int REVIEWS_LIMIT = 25;

    String eventActionUrl;
    protected boolean mLoading = false;
    protected int offset = 0, limit = 9;
    protected long storeId;

    protected final RequestListener<ReviewListJson> listener = new RequestListener<ReviewListJson>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleErrorCondition(spiceException);
        }

        @Override
        public void onRequestSuccess(ReviewListJson reviewListJson) {
            handleSuccessCondition();
            displayableList.clear();
            final BaseAdapter adapter = getAdapter();
            setRecyclerAdapter(adapter);

            if ("OK".equals(reviewListJson.status) && reviewListJson.reviews != null && reviewListJson.reviews.size() > 0) {

                for (Review review : reviewListJson.reviews) {
                    ReviewRowItem reviewRowItem = getReviewRow(review);
                    displayableList.add(reviewRowItem);
                }

                adapter.notifyDataSetChanged();
            }

            offset = displayableList.size();
        }
    };

    protected final RequestListener<ReviewListJson> endlessListener = new RequestListener<ReviewListJson>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleErrorCondition(spiceException);
        }

        @Override
        public void onRequestSuccess(ReviewListJson reviewListJson) {
            if (adapter == null) {
                return;
            }
            handleSuccessCondition();

            if (mLoading && !displayableList.isEmpty()) {
                displayableList.remove(displayableList.size() - 1);
                adapter.notifyItemRemoved(displayableList.size());
            }

            if ("OK".equals(reviewListJson.status) && reviewListJson.reviews != null && reviewListJson.reviews.size() > 0) {

                for (Review review : reviewListJson.reviews) {
                    ReviewRowItem reviewRowItem = getReviewRow(review);
                    displayableList.add(reviewRowItem);
                }
                adapter.notifyItemRangeInserted(offset, displayableList.size());
                offset += displayableList.size();
                mLoading = false;
            }
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeId = getArguments().getLong(Constants.STOREID_KEY);
    }

    @Override
    protected boolean isHomePage() {
        return storeId == Defaults.DEFAULT_STORE_ID;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = getRecyclerView();
        recyclerView.addOnScrollListener(
                new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
                    @Override
                    public void onLoadMore() {
                        mLoading = true;
                        displayableList.add(new ProgressBarRow(BUCKET_SIZE));
                        adapter.notifyItemInserted(adapter.getItemCount());
                        executeEndlessSpiceRequest();
                    }

                    @Override
                    public int getOffset() {
                        return offset;
                    }

                    @Override
                    public boolean isLoading() {
                        return mLoading;
                    }
                });
    }

    protected abstract void executeEndlessSpiceRequest();

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme());
        }
        return adapter;
    }

    @Override
    protected String getBaseContext() {
        return "GetMoreReviews";
    }
}
