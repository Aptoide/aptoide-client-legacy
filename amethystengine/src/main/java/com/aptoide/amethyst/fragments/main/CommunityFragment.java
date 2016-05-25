package com.aptoide.amethyst.fragments.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.dialogs.AdultHiddenDialog;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.AllCommentsRequest;
import com.aptoide.dataprovider.webservices.GetReviews;
import com.aptoide.dataprovider.webservices.json.review.Review;
import com.aptoide.dataprovider.webservices.json.review.ReviewListJson;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.dataprovider.webservices.models.v2.Comment;
import com.aptoide.dataprovider.webservices.models.v2.GetComments;
import com.aptoide.models.displayables.AdultItem;
import com.aptoide.models.displayables.CommentItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ReviewRowItem;
import com.aptoide.models.displayables.CommentPlaceHolderRow;
import com.aptoide.models.displayables.ReviewPlaceHolderRow;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.CommunityTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.COMMENTS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.REVIEWS_TYPE;

/**
 * Created by rmateus on 02/06/15.
 */
public class CommunityFragment extends BaseWebserviceFragment {

    protected RequestListener<StoreHomeTab> listener = new RequestListener<StoreHomeTab>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleErrorCondition(spiceException);
        }

        @Override
        public void onRequestSuccess(StoreHomeTab tab) {
            handleSuccessCondition();

            adapter = getAdapter();
            setRecyclerAdapter(adapter);

            displayableList.clear();
            if (isStorePage()) {
                displayableList.add(getStoreHeaderRow(tab));
            }

            displayableList.addAll(tab.list);

            if (isHomePage()) {
                displayableList.add(new AdultItem(BUCKET_SIZE));
            }
            for (Displayable row : tab.list) {

                if (row instanceof CommentPlaceHolderRow) {
                    executeCommentsRequest();
                } else if (row instanceof ReviewPlaceHolderRow) {
                    executeReviewsSpiceRequest();
                }
            }

            // check for hidden items
            if (tab.hidden > 0 && AptoideUtils.getSharedPreferences().getBoolean(Constants.SHOW_ADULT_HIDDEN, true) && getFragmentManager().findFragmentByTag(Constants.HIDDEN_ADULT_DIALOG) == null) {
                new AdultHiddenDialog().show(getFragmentManager(), Constants.HIDDEN_ADULT_DIALOG);
            }
        }
    };

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new CommunityTabAdapter(displayableList, getActivity(), -1);
        }
        return adapter;
    }

    @Override
    protected String getBaseContext() {
        return "community";
    }

    public static Fragment newInstance() {
        return new CommunityFragment();
    }

    @Override
    public void setLayoutManager(final RecyclerView recyclerView) {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), AptoideUtils.UI.getEditorChoiceBucketSize());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(!(recyclerView.getAdapter() instanceof SpannableRecyclerAdapter)){
                    throw new IllegalStateException("RecyclerView adapter must extend SpannableRecyclerAdapter");
                }

                int spanSize = ((SpannableRecyclerAdapter) recyclerView.getAdapter()).getSpanSize(position);
                if (spanSize >= ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount()) {
                    return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                } else {
                    return spanSize;
                }
            }
        });

        // we need to force the spanCount, or it will crash.
        // https://code.google.com/p/android/issues/detail?id=182400
        gridLayoutManager.setSpanCount(AptoideUtils.UI.getEditorChoiceBucketSize());
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void executeSpiceRequest(boolean useCache) {
        this.useCache = useCache;
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

        // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
        spiceManager.execute(
                //AptoideUtils.RepoUtils.buildStoreRequest(getStoreId(), getFakeString(), getFakeString(), getBaseContext()),
                AptoideUtils.RepoUtils.buildStoreRequest(getStoreId(), getBaseContext(), AptoideUtils.UI.getEditorChoiceBucketSize()),
                getBaseContext() + "-" + getStoreId() + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                cacheExpiryDuration,
                listener);
    }

    private void executeReviewsSpiceRequest() {

        GetReviews.GetReviewList reviewRequest = new GetReviews.GetReviewList();

        reviewRequest.setOrderBy("id");
        reviewRequest.homePage = isHomePage();
        reviewRequest.limit = AptoideUtils.UI.getEditorChoiceBucketSize() * 4;

        spiceManager.execute(reviewRequest, "review-community-store-"+BUCKET_SIZE, useCache ? DurationInMillis.ONE_HOUR : DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ReviewListJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                handleErrorCondition(spiceException);
            }

            @Override
            public void onRequestSuccess(ReviewListJson reviewListJson) {
                if ("OK".equals(reviewListJson.status) && reviewListJson.reviews != null && reviewListJson.reviews.size() > 0) {

                    // range is the size of the list. Because the HeaderRow replaces the placeholder, it's not considered an insertion
                    // why is this important? because of notifyItemRangeInserted
                    int range = reviewListJson.reviews.size();
                    int index = 0, originalIndex = 0;

                    boolean reviewPlaceHolderFound = false;
                    for (Displayable display : displayableList) {
                        if (display instanceof ReviewPlaceHolderRow) {
                            reviewPlaceHolderFound = true;
                            originalIndex = index = displayableList.indexOf(display);
                            break;
                        }
                    }

                    // prevent multiple requests adding to the beginning of the list
                    if (!reviewPlaceHolderFound)
                        return;

                    HeaderRow header = new HeaderRow(getString(R.string.reviews), true, REVIEWS_TYPE, BUCKET_SIZE, isHomePage(), Constants.GLOBAL_STORE);
                    header.FULL_ROW = AptoideUtils.UI.getEditorChoiceBucketSize();
                    displayableList.set(index++, header);

                    for (Review review : reviewListJson.reviews) {

                        ReviewRowItem reviewRowItem = getReviewRow(review);
                        reviewRowItem.FULL_ROW = 1;
                        displayableList.add(index++, reviewRowItem);
                    }

                    getAdapter().notifyItemRangeInserted(originalIndex + 1, range);
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BUCKET_SIZE = AptoideUtils.UI.getEditorChoiceBucketSize();
    }

    private void executeCommentsRequest() {
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

        AllCommentsRequest request = new AllCommentsRequest();
        request.storeName = getStoreName();
        request.filters = Aptoide.filters;
        request.lang = AptoideUtils.StringUtils.getMyCountryCode(getContext());
        request.limit = AptoideUtils.UI.getEditorChoiceBucketSize() * 4;

        spiceManager.execute(request, getBaseContext() + getStoreId()+BUCKET_SIZE, cacheExpiryDuration, new RequestListener<GetComments>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Logger.printException(spiceException);
                    }

                    @Override
                    public void onRequestSuccess(GetComments get) {
                        if ("OK".equals(get.status) && get.list != null && get.list.size() > 0) {


                            // range is the size of the list. Because the HeaderRow replaces the placeholder, it's not considered an insertion
                            // why is this important? because of notifyItemRangeInserted
                            int range = get.list.size();
                            int index = 0, originalIndex = 0;

                            boolean placeHolderFound = false;
                            for (Displayable display : displayableList) {
                                if (display instanceof CommentPlaceHolderRow) {
                                    placeHolderFound = true;
                                    originalIndex = index = displayableList.indexOf(display);
                                    break;
                                }
                            }

                            // prevent multiple requests adding to beginning of the list
                            if (!placeHolderFound) {
                                return;
                            }

                            HeaderRow header = new HeaderRow(getString(R.string.comments), true, COMMENTS_TYPE, BUCKET_SIZE, isHomePage(), getStoreId());
                            header.FULL_ROW = AptoideUtils.UI.getEditorChoiceBucketSize();
                            displayableList.set(index++, header);

                            for (int i = 0; i < get.list.size(); i++) {
//                            for (int i = 0; i < 7; i++) {
                                Comment comment = get.list.get(i);

                                CommentItem commentItem = getCommentRow(comment);
                                displayableList.add(index++, commentItem);
                            }

                            getAdapter().notifyItemRangeInserted(originalIndex + 1, range);
                        }
                    }
                }
        );
    }

    private CommentItem getCommentRow(Comment comment) {
        CommentItem item = new CommentItem(BUCKET_SIZE);
        item.setSpanSize(1);
        item.appname = comment.getAppname();
        item.id = comment.getId();
        item.lang = comment.getLang();
        item.text = comment.getText();
        item.timestamp = comment.getTimestamp();
        item.useravatar = comment.getUseravatar();
        item.appid = comment.getAppid();
        item.username = comment.getUsername();
        return item;
    }

    @Override
    protected long getStoreId() {
        return Defaults.DEFAULT_STORE_ID;
    }

    @Override
    public String getStoreName() {
        return Defaults.DEFAULT_STORE_NAME;
    }

}
