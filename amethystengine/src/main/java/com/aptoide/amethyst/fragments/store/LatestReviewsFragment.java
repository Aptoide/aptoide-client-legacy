package com.aptoide.amethyst.fragments.store;

import android.os.Bundle;

import com.aptoide.dataprovider.webservices.GetReviews.GetReviewList;
import com.octo.android.robospice.persistence.DurationInMillis;

import com.aptoide.amethyst.fragments.ReviewsFragment;

public class LatestReviewsFragment extends ReviewsFragment {

    public static LatestReviewsFragment newInstance(final Bundle args) {
        final LatestReviewsFragment fragment = new LatestReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void executeSpiceRequest(boolean useCache) {
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
        final GetReviewList request = buildRequest();
        spiceManager.execute(request, getBaseContext() + storeId + BUCKET_SIZE, cacheExpiryDuration, listener);
    }

    protected void executeEndlessSpiceRequest() {
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
        final GetReviewList request = buildRequest();
        request.offset = offset;
        spiceManager.execute(request, getBaseContext() + storeId + BUCKET_SIZE + offset, cacheExpiryDuration, endlessListener);
    }

    protected GetReviewList buildRequest() {
        final GetReviewList request = new GetReviewList();
        request.store_id = storeId;
        request.limit = REVIEWS_LIMIT;
        return request;
    }
}
