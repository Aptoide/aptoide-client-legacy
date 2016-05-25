package com.aptoide.amethyst.fragments.store;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aptoide.amethyst.GridRecyclerFragment;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.dialogs.AdultHiddenDialog;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.StoreHeaderRow;
import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.ListApksInstallsRequest;
import com.aptoide.amethyst.webservices.json.TimelineListAPKsJson;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.dataprovider.webservices.GetReviews;
import com.aptoide.dataprovider.webservices.json.review.Review;
import com.aptoide.dataprovider.webservices.json.review.ReviewListJson;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.models.displayables.AdItem;
import com.aptoide.models.displayables.AdultItem;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ReviewRowItem;
import com.aptoide.models.displayables.TimelineRow;
import com.aptoide.models.displayables.AdPlaceHolderRow;
import com.aptoide.models.displayables.ReviewPlaceHolderRow;
import com.aptoide.models.displayables.TimeLinePlaceHolderRow;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.aptoide.amethyst.adapter.BaseAdapter;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.ADS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.REVIEWS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.TIMELINE_TYPE;

/**
 * Json example for this kind of tab:
 *  "type": "API",
 *  "name": "getStoreWidgets",
 *  "label": "Aptoide Publishers",
 *  "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_3239
 *
 * Created by rmateus on 23/06/15.
 */
public abstract class BaseWebserviceFragment extends GridRecyclerFragment {

    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBar;
    protected ScrollView layoutNoNetwork;
    protected ScrollView layoutError;
    protected TextView retryError;
    protected TextView retryNoNetwork;

    @Nullable
    protected BaseAdapter adapter;

    // flag to feed the storeHeader button
    protected boolean subscribed;
    protected boolean useCache = true;
    private long storeId;
    protected String storeName;
    protected String versionName;
    protected String packageName;
    private EnumStoreTheme storeTheme;
    protected Bundle args;
    protected List<Displayable> displayableList = new ArrayList<>();
    protected String sponsoredCache;  //used only on HomeFragment
    protected int offset, total;

    protected RequestListener<StoreHomeTab> listener = new RequestListener<StoreHomeTab>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleErrorCondition(spiceException);
        }

        @Override
        public void onRequestSuccess(StoreHomeTab tab) {
            if (getView() == null) {
                return;
            }

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

                if (row instanceof ReviewPlaceHolderRow) {
                    executeReviewsSpiceRequest();
                } else if (row instanceof AdPlaceHolderRow) {
                    executeAdsSpiceRequest();
                } else if (row instanceof TimeLinePlaceHolderRow) {
                    executeTimelineRequest();
                }
            }

            // total and new offset is red here
            offset = tab.offset;
            total = tab.total;

            // check for hidden items
            if (tab.hidden > 0 && AptoideUtils.getSharedPreferences().getBoolean(Constants.SHOW_ADULT_HIDDEN, true) && getFragmentManager().findFragmentByTag(Constants.HIDDEN_ADULT_DIALOG) == null) {
                new AdultHiddenDialog().show(getFragmentManager(), Constants.HIDDEN_ADULT_DIALOG);
            }
        }
    };

    protected void handleErrorCondition(SpiceException spiceException) {
        Logger.printException(spiceException);

        progressBar.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
        swipeContainer.setEnabled(false);
        getRecyclerView().setVisibility(View.GONE);

        if (spiceException instanceof NoNetworkException) {
            layoutError.setVisibility(View.GONE);
            layoutNoNetwork.setVisibility(View.VISIBLE);
            retryNoNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });

        } else {
            layoutNoNetwork.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
            retryError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });
        }
    }

    protected void handleNoItemsCondition() {

        layoutNoNetwork.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
        swipeContainer.setEnabled(false);
        getRecyclerView().setVisibility(View.GONE);
    }

    private void retry() {
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        executeSpiceRequest(false);
    }

    protected void handleSuccessCondition() {
        // TODO find memory leak
        if (getView() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
        swipeContainer.setEnabled(true);
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

        getRecyclerView().setVisibility(View.VISIBLE);
    }

    protected void executeSpiceRequest(boolean useCache) {
        this.swipeContainer.setEnabled(false);
        this.useCache = useCache;
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

        // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
        spiceManager.execute(
                AptoideUtils.RepoUtils.buildStoreRequest(getStoreId(), getBaseContext()),
                getBaseContext() + "-" + getStoreId() + "-" + BUCKET_SIZE + "--" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                cacheExpiryDuration,
                listener);
    }

    private void executeTimelineRequest() {

        if (AptoideUtils.AccountUtils.isLoggedIn(getActivity()) && AptoideUtils.getSharedPreferences().getBoolean(Preferences.TIMELINE_ACEPTED_BOOL, false)) {

            ListApksInstallsRequest listRelatedApkRequest = new ListApksInstallsRequest();
            listRelatedApkRequest.setLimit(String.valueOf(BUCKET_SIZE));

            // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
            spiceManager.execute(listRelatedApkRequest, "MoreFriendsInstalls--" + BUCKET_SIZE, useCache ? DurationInMillis.ONE_HOUR : DurationInMillis.ALWAYS_EXPIRED, new RequestListener<TimelineListAPKsJson>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Logger.printException(spiceException);
                }

                @Override
                public void onRequestSuccess(TimelineListAPKsJson timelineListAPKsJson) {
                    if (timelineListAPKsJson != null && timelineListAPKsJson.usersapks != null && timelineListAPKsJson.usersapks.size() > 0) {

                        // range is the size of the list. Because the HeaderRow replaces the placeholder, it's not considered an insertion
                        // why is this important? because of notifyItemRangeInserted
                        int range = timelineListAPKsJson.usersapks.size();
                        int index = 0, originalIndex = 0;

                        boolean placeHolderFound = false;
                        for (Displayable display : displayableList) {
                            if (display instanceof TimeLinePlaceHolderRow) {
                                placeHolderFound = true;
                                originalIndex = index = displayableList.indexOf(display);
                                break;
                            }
                        }

                        // prevent multiple requests adding to beginning of the list
                        if (!placeHolderFound) {
                            return;
                        }


                        HeaderRow header = new HeaderRow(getString(R.string.friends_installs), true, TIMELINE_TYPE, BUCKET_SIZE, isHomePage(), getStoreId());
                        displayableList.set(index++, header);

                        for (int i = 0; i < timelineListAPKsJson.usersapks.size(); i++) {
                            TimelineListAPKsJson.UserApk userApk = timelineListAPKsJson.usersapks.get(i);

                            TimelineRow timeline = getTimelineRow(userApk);

                            displayableList.add(index++, timeline);
                        }

                        adapter.notifyItemRangeInserted(originalIndex + 1, range);
                    }
                }


            });
        }
    }

    private void executeAdsSpiceRequest() {

        if (sponsoredCache == null) {
            if (args != null && args.getString("sponsoredCache") != null) {
                sponsoredCache = args.getString("sponsoredCache");
            } else {
                sponsoredCache = UUID.randomUUID().toString();
            }
        }

        final GetAdsRequest request = new GetAdsRequest();
        request.setLimit(BUCKET_SIZE);
        request.setLocation("homepage");
        request.setKeyword("__NULL__");


        // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
        spiceManager.execute(request, sponsoredCache + "--" + BUCKET_SIZE, useCache ? DurationInMillis.ONE_HOUR : DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ApkSuggestionJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
            }

            @Override
            public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {
                if (apkSuggestionJson != null && apkSuggestionJson.ads != null && apkSuggestionJson.ads.size() > 0) {

                    // range is the size of the list. Because the HeaderRow replaces the placeholder, it's not considered an insertion
                    // why is this important? because of notifyItemRangeInserted
                    int range = apkSuggestionJson.ads.size();
                    int index = 0, originalIndex = 0;

                    boolean adPlaceHolderFound = false;
                    for (Displayable display : displayableList) {
                        if (display instanceof AdPlaceHolderRow) {
                            adPlaceHolderFound = true;
                            originalIndex = index = displayableList.indexOf(display);
                            break;
                        }
                    }

                    // prevent multiple requests adding to beginning of the list
                    if (!adPlaceHolderFound)
                        return;


                    HeaderRow header = new HeaderRow(getString(R.string.highlighted_apps), true, ADS_TYPE, BUCKET_SIZE, isHomePage(), getStoreId());
                    displayableList.set(index++, header);

                    for (int i = 0; i < apkSuggestionJson.ads.size(); i++) {
                        ApkSuggestionJson.Ads ad = apkSuggestionJson.ads.get(i);

                        AdItem adItem = getAdItem(ad, "Highlighted");

                        displayableList.add(index++, adItem);
                    }

                    adapter.notifyItemRangeInserted(originalIndex + 1, range);
                }
            }
        });
    }

    private void executeReviewsSpiceRequest() {

        GetReviews.GetReviewList reviewRequest = new GetReviews.GetReviewList();

        reviewRequest.setOrderBy("rand");
        reviewRequest.store_id = getStoreId();
        reviewRequest.homePage = isHomePage();
        reviewRequest.limit = 1;

        // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
        spiceManager.execute(reviewRequest, "review-store-" + getStoreId() + "--" + BUCKET_SIZE, useCache ? DurationInMillis.ONE_HOUR : DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ReviewListJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
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


                    HeaderRow header = new HeaderRow(getString(R.string.more_reviews), true, REVIEWS_TYPE, BUCKET_SIZE, isHomePage(), getStoreId());
                    displayableList.set(index++, header);

                    for (Review review : reviewListJson.reviews) {

                        ReviewRowItem reviewRowItem = getReviewRow(review);
                        displayableList.add(index++, reviewRowItem);
                    }

                    adapter.notifyItemRangeInserted(originalIndex + 1, range);
                }
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(getView());
        if (savedInstanceState != null) {
            args = savedInstanceState;
        } else {
            args = getArguments();
        }

        // args is null on Fragment's first entry
        if (args != null) {
            storeId = args.getLong(Constants.STOREID_KEY, 0);
            storeName = args.getString(Constants.STORENAME_KEY);
            versionName = args.getString(Constants.VERSIONNAME_KEY);
            packageName = args.getString(Constants.PACKAGENAME_KEY);
            storeTheme = EnumStoreTheme.values()[args.getInt(Constants.THEME_KEY, 0)];
            subscribed = args.getBoolean(Constants.STORE_SUBSCRIBED_KEY, false);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(storeTheme.getStoreHeader()), PorterDuff.Mode.SRC_IN);
            retryNoNetwork.getBackground().setColorFilter(getResources().getColor(storeTheme.getStoreHeader()), PorterDuff.Mode.SRC_IN);
            retryError.getBackground().setColorFilter(getResources().getColor(storeTheme.getStoreHeader()), PorterDuff.Mode.SRC_IN);
        }

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                executeSpiceRequest(false);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.default_progress_bar_color, R.color.default_color, R.color.default_progress_bar_color, R.color
                .default_color);
        executeSpiceRequest(true);
    }

    @Override
    public void onDestroyView() {
        adapter = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (args != null) {
            outState.putLong(Constants.STOREID_KEY, storeId);
            outState.putString(Constants.STORENAME_KEY, storeName);
            outState.putInt(Constants.THEME_KEY, storeTheme.ordinal());
            outState.putBoolean(Constants.STORE_SUBSCRIBED_KEY, subscribed);
            outState.putString("sponsoredCache", sponsoredCache);
            outState.putString(Constants.VERSIONNAME_KEY, versionName);
            outState.putString(Constants.PACKAGENAME_KEY, packageName);
        }
    }

    @NonNull
    protected StoreHeaderRow getStoreHeaderRow(StoreHomeTab tab) {
        StoreHeaderRow storeHeaderRow = new StoreHeaderRow(BUCKET_SIZE);
        storeHeaderRow.id = tab.store.nodes.meta.data.id.longValue();
        storeHeaderRow.name = tab.store.nodes.meta.data.name;
        storeHeaderRow.avatar = tab.store.nodes.meta.data.avatar;
        storeHeaderRow.description = tab.store.nodes.meta.data.appearance.description;
        storeHeaderRow.apps = tab.store.nodes.meta.data.stats.apps.longValue();
        storeHeaderRow.subscribers = tab.store.nodes.meta.data.stats.subscribers.longValue();
        storeHeaderRow.downloads = tab.store.nodes.meta.data.stats.downloads.longValue();
        return storeHeaderRow;
    }



    @NonNull
    protected ReviewRowItem getReviewRow(Review review) {
        ReviewRowItem reviewRowItem = new ReviewRowItem(BUCKET_SIZE);
        reviewRowItem.appIcon = review.apk.icon;
        reviewRowItem.appName = review.apk.title;
        reviewRowItem.avatar = review.user.avatar;
        reviewRowItem.reviewer = review.user.name;
        reviewRowItem.description = review.finalVerdict;
        reviewRowItem.rating = review.average.floatValue();
        reviewRowItem.reviewId = review.id;
        return reviewRowItem;
    }

    @NonNull
    protected AdItem getAdItem(ApkSuggestionJson.Ads ad, String category) {
        AdItem adItem = new AdItem(BUCKET_SIZE);
        adItem.setSpanSize(2);
        adItem.appName = ad.data.name;
        adItem.icon = ad.data.icon;
        adItem.packageName = ad.data.packageName;
        adItem.storeName = ad.data.repo;
        adItem.id = ad.data.id.longValue();
        adItem.adId = ad.info.ad_id;
        adItem.cpcUrl = ad.info.cpc_url;
        adItem.cpiUrl = ad.info.cpi_url;
        adItem.cpdUrl = ad.info.cpd_url;
        adItem.partnerName = ad.partner != null ? ad.partner.partnerInfo.name : null;
        adItem.partnerClickUrl = ad.partner != null ? ad.partner.partnerData.click_url : null;
        adItem.category = category;
        return adItem;
    }

    @NonNull
    protected TimelineRow getTimelineRow(TimelineListAPKsJson.UserApk userApk) {
        TimelineRow timeline = new TimelineRow(BUCKET_SIZE);
        timeline.setSpanSize(2);

        timeline.appName = userApk.apk.name;
        timeline.appIcon = parseIcon(userApk.apk);
        timeline.appFriend = AptoideUtils.StringUtils.getFormattedString(getContext(), R.string.installed_this, userApk.info.username);
        timeline.userAvatar = userApk.info.avatar;

        timeline.repoName = userApk.apk.repo;
        timeline.md5sum = userApk.apk.md5sum;
        return timeline;
    }

    @NonNull
    private String parseIcon(TimelineListAPKsJson.UserApk.APK apk) {

        String icon = apk.icon_hd;
        if (icon == null) {
            icon = apk.icon;
        }
        if (icon.contains("_icon")) {
            String[] splittedUrl = icon.split("\\.(?=[^\\.]+$)");
            icon = splittedUrl[0] + "_" + IconSizeUtils.generateSizeString() + "." + splittedUrl[1];
        }
        return icon;
    }

    public CharSequence parseActionUrlIntoCacheKey(String actionUrl) {
        try {
            return actionUrl.replace(":", "").replace("/", "");
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    protected long getStoreId() {
        return storeId;
    }

    public EnumStoreTheme getStoreTheme() {
        return storeTheme;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    // by default, no fragments are either HomePage nor StorePages. Override the methods in their subclasses according to their needs.
    protected boolean isHomePage() {
        return false;
    }

    protected boolean isStorePage() {
        return false;
    }

    protected abstract BaseAdapter getAdapter();

    protected abstract String getBaseContext();

    protected void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout )view.findViewById(R.id.swipe_container);
        progressBar = (ProgressBar )view.findViewById(R.id.progress_bar);
        layoutNoNetwork = (ScrollView )view.findViewById(R.id.no_network_connection);
        layoutError = (ScrollView )view.findViewById(R.id.error);
        retryError = (TextView )view.findViewById(R.id.retry_error);
        retryNoNetwork = (TextView )view.findViewById(R.id.retry_no_network);
    }
}
