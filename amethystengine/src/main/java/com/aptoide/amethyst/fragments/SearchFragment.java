package com.aptoide.amethyst.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.LinearRecyclerFragment;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.adapter.SearchAdapter;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.models.search.SearchApkConverter;
import com.aptoide.amethyst.models.search.SearchItemSubscriptionFilter;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.dataprovider.webservices.models.v7.ListSearchApps;
import com.aptoide.dataprovider.webservices.models.v7.SearchItem;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SearchApk;
import com.aptoide.models.displayables.SearchMoreHeader;
import com.aptoide.models.displayables.SuggestedAppDisplayable;
import com.aptoide.models.stores.Store;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchFragment extends LinearRecyclerFragment {

    private static final long SEARCH_CACHE_DURATION = DurationInMillis.ONE_HOUR * 6;
    private static final String QUERY = "search";
    private static final String TRUSTED = "trusted";

    private static final String USER_TOUCHED_LIST_KEY = "USER_TOUCHED_LIST";
    private static final String LIST_STATE_KEY = "LIST_STATE";
    private static final String UNSUBSCRIBED_OFFSET_KEY = "UNSUBSCRIBED_OFFSET";
    private static final String SEARCH_OFFSET_KEY = "SEARCH_OFFSET";
    private static final String SUBSCRIBED_OFFSET_KEY = "SUBSCRIBED_OFFSET";
    private static final String SUGGESTED_OFFSET_KEY = "SUGGESTED_OFFSET";
    private static final String DISPLAYABLES_KEY = "DISPLAYABLES";
    private static final String UNSUBSCRIBED_LOADING_KEY = "UNSUBSCRIBED_LOADING_KEY";
    private static final String SUBSCRIBED_LOADING_KEY = "SUBSCRIBED_LOADING_KEY";
    private static final String EMPTY_RESULT_KEY = "EMPTY_RESULT";
    private static final String NETWORK_ERROR_KEY = "NETWORK_ERROR";
    private static final String GENERAL_ERROR_KEY = "GENERAL_ERROR";
    private static final String STORE_LIST_KEY = "STORE_LIST";

    private SwipeRefreshLayout swipeContainer;
    private ScrollView noSearchResultLayout;
    private ImageView searchButton;
    private EditText searchQuery;
    private ProgressBar progressBar;
    private ScrollView layoutNoNetwork;
    private ScrollView layoutError;
    private TextView retryError;
    private TextView retryNoNetwork;

    private String query;
    private SearchAdapter adapter;
    private boolean trusted;
    private SearchItemSubscriptionFilter searchItemFilter;
    private SearchApkConverter searchApkConverter;
    private RecyclerView.OnItemTouchListener userTouchListener;

    private boolean userTouchedList;
    private ArrayList<Displayable> displayables;
    private int suggestetedAppsOffset;
    private int unsubscribedAppsOffset;
    private int subscribedAppsOffset;
    private int searchOffset;
    private Parcelable listState;
    private boolean generalError;
    private boolean networkError;
    private boolean emptyResult;
    private boolean subscribedLoading;
    private boolean unsubscribedLoading;
    private List<Store> subscribedStores;

    public static SearchFragment newInstance(String query, boolean trusted) {
        final SearchFragment searchFragment = new SearchFragment();

        final Bundle args = new Bundle();
        args.putString(QUERY, query);
        args.putBoolean(TRUSTED, trusted);

        searchFragment.setArguments(args);

        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
        searchItemFilter = new SearchItemSubscriptionFilter(database);
        searchApkConverter = new SearchApkConverter(BUCKET_SIZE);
        query = getArguments().getString(QUERY);
        trusted = getArguments().getBoolean(TRUSTED);

        if (savedInstanceState != null) {
            userTouchedList = savedInstanceState.getBoolean(USER_TOUCHED_LIST_KEY);
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            subscribedAppsOffset = savedInstanceState.getInt(SUBSCRIBED_OFFSET_KEY);
            unsubscribedAppsOffset = savedInstanceState.getInt(UNSUBSCRIBED_OFFSET_KEY);
            searchOffset = savedInstanceState.getInt(SEARCH_OFFSET_KEY);
            suggestetedAppsOffset = savedInstanceState.getInt(SUGGESTED_OFFSET_KEY);
            displayables = savedInstanceState.getParcelableArrayList(DISPLAYABLES_KEY);
            unsubscribedLoading = savedInstanceState.getBoolean(UNSUBSCRIBED_LOADING_KEY);
            subscribedLoading = savedInstanceState.getBoolean(SUBSCRIBED_LOADING_KEY);
            generalError = savedInstanceState.getBoolean(GENERAL_ERROR_KEY);
            networkError = savedInstanceState.getBoolean(NETWORK_ERROR_KEY);
            emptyResult = savedInstanceState.getBoolean(EMPTY_RESULT_KEY);
            subscribedStores = savedInstanceState.getParcelableArrayList(STORE_LIST_KEY);
        } else {
            displayables = new ArrayList<>();
            subscribedStores = database.getSubscribedStores();
        }

        adapter = new SearchAdapter(displayables);
        adapter.setQuery(query);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(getView());
        removeDefaultLoadingViews();
        getRecyclerView().setAdapter(adapter);

        restoreState();
        searchForApps();
    }

    @Override
    public void onResume() {
        super.onResume();
        userTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                userTouchedList = true;
                getRecyclerView().removeOnItemTouchListener(this);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        getRecyclerView().addOnItemTouchListener(userTouchListener);
        getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
            @Override
            public void
            onLoadMore() {
                searchForSubscribedApps(subscribedStores);
                searchForUnsubscribedApps(searchOffset);
            }

            @Override
            public int getOffset() {
                return unsubscribedAppsOffset;
            }

            @Override
            public boolean isLoading() {
                return unsubscribedLoading;
            }
        });
        retryNoNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrySearchForApps();
            }
        });
        retryError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrySearchForApps();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getRecyclerView().clearOnScrollListeners();
        getRecyclerView().removeOnItemTouchListener(userTouchListener);
        retryNoNetwork.setOnClickListener(null);
        retryError.setOnClickListener(null);
        searchButton.setOnClickListener(null);
        searchQuery.setOnEditorActionListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(USER_TOUCHED_LIST_KEY, userTouchedList);
        outState.putParcelable(LIST_STATE_KEY, getRecyclerView().getLayoutManager().onSaveInstanceState());
        outState.putInt(SUBSCRIBED_OFFSET_KEY, subscribedAppsOffset);
        outState.putInt(UNSUBSCRIBED_OFFSET_KEY, unsubscribedAppsOffset);
        outState.putParcelableArrayList(DISPLAYABLES_KEY, displayables);
        outState.putInt(SEARCH_OFFSET_KEY, searchOffset);
        outState.putInt(SUGGESTED_OFFSET_KEY, suggestetedAppsOffset);
        outState.putBoolean(UNSUBSCRIBED_LOADING_KEY, unsubscribedLoading);
        outState.putBoolean(SUBSCRIBED_LOADING_KEY, subscribedLoading);
        outState.putBoolean(EMPTY_RESULT_KEY, emptyResult);
        outState.putBoolean(NETWORK_ERROR_KEY, networkError);
        outState.putBoolean(GENERAL_ERROR_KEY, generalError);
        outState.putParcelableArrayList(STORE_LIST_KEY, new ArrayList<Parcelable>(subscribedStores));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindViews();
    }

    private void restoreState() {
        getRecyclerView().getLayoutManager().onRestoreInstanceState(listState);

        if (isLoading()) {
            setLoading(subscribedLoading, unsubscribedLoading);
        }

        if (emptyResult) {
            showEmptyResultView();
        }

        if (generalError) {
            showGeneralErrorView();
        }

        if (networkError) {
            showNoNetworkErrorView();
        }

        if (unsubscribedLoading
                && searchOffset > 0) {
            searchForUnsubscribedApps(searchOffset);
        }
    }

    private void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        noSearchResultLayout = (ScrollView) view.findViewById(R.id.no_search_results_layout);
        searchButton = (ImageView) view.findViewById(R.id.ic_search_button);
        searchQuery = (EditText) view.findViewById(R.id.search_text);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        layoutNoNetwork = (ScrollView) view.findViewById(R.id.no_network_connection);
        layoutError = (ScrollView) view.findViewById(R.id.error);
        retryError = (TextView) view.findViewById(R.id.retry_error);
        retryNoNetwork = (TextView) view.findViewById(R.id.retry_no_network);
    }

    private void unbindViews() {
        swipeContainer = null;
        noSearchResultLayout = null;
        searchButton = null;
        searchQuery = null;
        progressBar = null;
        layoutNoNetwork = null;
        layoutError = null;
        retryError = null;
        retryNoNetwork = null;
    }

    private void retrySearchForApps() {
        removeErrorViews();
        searchForApps();
    }

    private void searchForApps() {
        if (!isErrorViewShown()) {
            searchForSuggestedApp();
            searchForSubscribedApps(subscribedStores);
            searchForUnsubscribedApps();
        }
    }

    private void searchForSuggestedApp() {
        if (suggestetedAppsOffset == 0) {
            final GetAdsRequest getAdsRequest = new GetAdsRequest();
            getAdsRequest.setLocation(QUERY);
            getAdsRequest.setKeyword(query);
            getAdsRequest.setLimit(1);
            spiceManager.execute(getAdsRequest, new RequestListener<ApkSuggestionJson>() {

                @Override
                public void onRequestFailure(SpiceException spiceException) {

                }

                @Override
                public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {
                    if (!isErrorViewShown()) {
                        addSuggestedApp(displayables, apkSuggestionJson);
                    }
                }
            });
        }
    }

    private void searchForSubscribedApps(List<Store> subscribedStores) {
        if (subscribedAppsOffset == 0) {
            setLoading(true, unsubscribedLoading);
            spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                    SearchActivity.SEARCH_LIMIT, 0, subscribedStores),
                    SearchActivity.CONTEXT + query + getStoreListCacheKey(subscribedStores), SEARCH_CACHE_DURATION, new RequestListener<ListSearchApps>() {

                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            showErrorView(spiceException);
                        }

                        @Override
                        public void onRequestSuccess(ListSearchApps listSearchApps) {
                            if (!isErrorViewShown()) {
                                setLoading(false, unsubscribedLoading);
                                addSubsribedApps(displayables, searchApkConverter.convert(getSearchItemList(listSearchApps), subscribedAppsOffset, true));
                                treatEmptyList();
                            }
                        }
                    });
        }
    }

    private List<SearchItem> getSearchItemList(ListSearchApps listSearchApps) {
        if (listSearchApps != null
                && listSearchApps.datalist != null
                && listSearchApps.datalist.list != null) {
            return listSearchApps.datalist.list;
        }
        return new ArrayList<>();
    }

    private String getStoreListCacheKey(List<Store> stores) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Store store : stores) {
            stringBuilder.append(store.getName());
        }
        return stringBuilder.toString();
    }

    private void searchForUnsubscribedApps() {
        if (unsubscribedAppsOffset == 0) {
            searchForUnsubscribedApps(0);
        }
    }

    private void searchForUnsubscribedApps(final int offset) {
        setLoading(subscribedLoading, true);
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                SearchActivity.SEARCH_LIMIT, offset), SearchActivity.CONTEXT + query +
                offset, SEARCH_CACHE_DURATION, new RequestListener<ListSearchApps>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (unsubscribedAppsOffset == 0) {
                    showErrorView(spiceException);
                }
            }

            @Override
            public void onRequestSuccess(ListSearchApps listSearchApps) {
                if (!isErrorViewShown()) {
                    updateSearchOffset(listSearchApps);
                    addUnsubscribedApps(displayables, searchApkConverter.convert(
                            searchItemFilter.filterUnsubscribed(getSearchItemList(listSearchApps)),
                            unsubscribedAppsOffset, false));
                    setLoading(subscribedLoading, false);
                    treatEmptyList();
                }
            }
        });
    }

    private void updateSearchOffset(ListSearchApps listSearchApps) {
        if (listSearchApps != null
                && listSearchApps.datalist != null
                && listSearchApps.datalist.next != null) {
            searchOffset = listSearchApps.datalist.next.intValue();
        }
    }

    private void treatEmptyList() {
        if (!isLoading() && isListEmpty()) {
            showEmptyResultView();
            Analytics.Search.noSearchResultEvent(query);
        }
    }

    private boolean isListEmpty() {
        // Do not take suggested app into account
        return unsubscribedAppsOffset == 0 && subscribedAppsOffset == 0;
    }

    private void showErrorView(Exception exception) {
        setLoading(false, false);
        Logger.printException(exception);
        if (!isErrorViewShown()) {
            spiceManager.cancelAllRequests();
            if (exception instanceof NoNetworkException) {
                showNoNetworkErrorView();
            } else {
                showGeneralErrorView();
            }
        }
    }

    private void showEmptyResultView() {
        getRecyclerView().setVisibility(View.GONE);
        noSearchResultLayout.setVisibility(View.VISIBLE);
        searchQuery.setText(query);
        emptyResult = true;
    }

    private boolean isErrorViewShown() {
        return generalError || networkError;
    }

    private void showGeneralErrorView() {
        swipeContainer.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        generalError = true;
        networkError = false;
    }

    private void showNoNetworkErrorView() {
        swipeContainer.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.VISIBLE);
        networkError = true;
        generalError = false;
    }

    private void removeErrorViews() {
        swipeContainer.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        networkError = false;
        generalError = false;
    }

    private void removeDefaultLoadingViews() {
        swipeContainer.setEnabled(false);
        progressBar.setVisibility(View.GONE);
    }

    private void startSearch() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_QUERY, searchQuery.getText().toString());
        getContext().startActivity(intent);
    }

    private boolean isLoading() {
        return unsubscribedLoading || subscribedLoading;
    }

    private void setLoading(boolean subscribedLoading, boolean unsubscribedLoading) {
        final boolean enableLoading = subscribedLoading || unsubscribedLoading;

        if (enableLoading) {
            if (!isLoading()) {
                displayables.add(new ProgressBarRow(BUCKET_SIZE));
                adapter.notifyItemInserted(displayables.size());
            }
        } else {
            if (isLoading()) {
                if (!displayables.isEmpty()) {
                    displayables.remove(displayables.size() - 1);
                    adapter.notifyItemRemoved(displayables.size());
                }
            }
        }
        this.subscribedLoading = subscribedLoading;
        this.unsubscribedLoading = unsubscribedLoading;
    }

    private void addSuggestedApp(List<Displayable> displayables, ApkSuggestionJson apkSuggestionJson) {

        if (apkSuggestionJson.getAds().size() == 0) {
            return;
        }
        if (apkSuggestionJson.getAds().get(0).getPartner() == null) {
            return;
        }
        if (apkSuggestionJson.getAds().get(0).getPartner().getPartnerData() == null) {
            return;
        }


        final HeaderRow suggestedAppHeader = new HeaderRow("Suggested App", false, BUCKET_SIZE);
        displayables.add(0, suggestedAppHeader);
        displayables.add(1, new SuggestedAppDisplayable(apkSuggestionJson));
        suggestetedAppsOffset = 2;
        adapter.notifyItemRangeInserted(0, 2);
        final boolean itemsBellow = subscribedAppsOffset + unsubscribedAppsOffset > 0;
        updateScrollPosition(2, itemsBellow, userTouchedList);
    }

    private void addSubsribedApps(List<Displayable> displayables, List<SearchApk> subscribedApps) {
        if (!subscribedApps.isEmpty()) {
            displayables.add(suggestetedAppsOffset, new HeaderRow(getString(R.string.results_subscribed), false, BUCKET_SIZE));
            displayables.addAll(suggestetedAppsOffset + 1, subscribedApps);
            int notifyItemCount = subscribedApps.size();
            subscribedAppsOffset += notifyItemCount;
            // Sum suggested app header, suggested app, subscribed apps header and subscribed apps
            displayables.add(suggestetedAppsOffset + 1 + subscribedAppsOffset, new SearchMoreHeader(BUCKET_SIZE));

            adapter.notifyItemRangeInserted(suggestetedAppsOffset, 1 + notifyItemCount + 1);
            final boolean itemsBellow = unsubscribedAppsOffset > 0;
            updateScrollPosition(notifyItemCount, itemsBellow, userTouchedList);
        }
    }

    private void addUnsubscribedApps(List<Displayable> displayables, List<SearchApk> unsubscribedApps) {
        if (!unsubscribedApps.isEmpty()) {

            int notifyPositionStart;
            int notifyItemCount = 0;
            if (unsubscribedAppsOffset == 0) {
                if (subscribedAppsOffset == 0) {
                    // Sum suggested app header and suggested app
                    displayables.add(suggestetedAppsOffset, new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                    displayables.addAll(suggestetedAppsOffset + 1, unsubscribedApps);
                    notifyPositionStart = suggestetedAppsOffset;
                } else {
                    // Sum suggested app header, suggested app, subscribed apps header, subscribed apps and subscribed apps footer
                    displayables.add(suggestetedAppsOffset + 1 + subscribedAppsOffset + 1, new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                    // Sum suggested app header, suggested app, subscribed apps header, subscribed apps, subscribed apps footer and unsubscribed apps header
                    displayables.addAll(suggestetedAppsOffset + 1 + subscribedAppsOffset + 1 + 1, unsubscribedApps);
                    notifyPositionStart = suggestetedAppsOffset + 1 + subscribedAppsOffset + 1;
                }
                notifyItemCount = 1;
            } else {
                if (subscribedAppsOffset == 0) {
                    // Sum suggested app header, suggested app and unsubscribed apps header
                    displayables.addAll(suggestetedAppsOffset + 1 + unsubscribedAppsOffset, unsubscribedApps);
                    notifyPositionStart = suggestetedAppsOffset + 1 + unsubscribedAppsOffset;
                } else {
                    // Sum suggested app header, suggested app, subscribed apps header, subscribed apps, subscribed apps footer, unsubscribed apps header and unsubscribed apps
                    displayables.addAll(suggestetedAppsOffset + 1 + subscribedAppsOffset + 1 + 1 + unsubscribedAppsOffset, unsubscribedApps);
                    notifyPositionStart = suggestetedAppsOffset + 1 + subscribedAppsOffset + 1 + 1 + unsubscribedAppsOffset;
                }
            }
            notifyItemCount += unsubscribedApps.size();
            unsubscribedAppsOffset += unsubscribedApps.size();

            adapter.notifyItemRangeInserted(notifyPositionStart, notifyItemCount);
            updateScrollPosition(notifyItemCount, false, userTouchedList);
        }
    }

    private void updateScrollPosition(int updateSize, boolean itemsBellow, boolean userTouchedList) {
        if (!userTouchedList) {
            getRecyclerView().scrollToPosition(0);
        } else if (itemsBellow) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
            linearLayoutManager.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + updateSize);
        }
    }
}