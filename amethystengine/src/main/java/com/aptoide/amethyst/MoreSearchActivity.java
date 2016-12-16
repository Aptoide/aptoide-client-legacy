package com.aptoide.amethyst;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aptoide.amethyst.adapter.SearchAdapter;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.models.search.SearchAppConverter;
import com.aptoide.amethyst.ui.MoreActivity;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.utils.CacheKeyFactory;
import com.aptoide.dataprovider.webservices.models.v7.ListSearchApps;
import com.aptoide.dataprovider.webservices.models.v7.SearchItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SearchApp;
import com.aptoide.models.stores.Store;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fabio on 20-11-2015.
 */
public class MoreSearchActivity extends MoreActivity {

    protected static EnumStoreTheme storeTheme = null;
    private AppBarLayout mAppBar;

    @Override
    protected Fragment getFragment(Bundle args) {
        MoreSearchFragment fragment = new MoreSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setLogo(null);
            mToolbar.setTitle(AptoideUtils.StringUtils.getFormattedString(this, R.string.search_activity_title, getIntent().getExtras().getString(SearchActivity.SEARCH_QUERY)));
            try {
                storeTheme = (EnumStoreTheme) getIntent().getExtras().get(SearchActivity.SEARCH_STORE_THEME);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            setupStoreTheme(storeTheme);
        }
    }

    private void setupStoreTheme(EnumStoreTheme storeTheme) {
        if (storeTheme != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(storeTheme.getColor700tint()));
            }
            mAppBar.setBackgroundColor(getResources().getColor(storeTheme.getStoreHeader()));
            mToolbar.setBackgroundColor(getResources().getColor(storeTheme.getStoreHeader()));
        }
    }

    static public class MoreSearchFragment extends LinearRecyclerFragment {

        private static final long SEARCH_CACHE_DURATION = DurationInMillis.ONE_HOUR * 6;
        private static final String LIST_STATE_KEY = "LIST_STATE";
        private static final String SEARCH_OFFSET_KEY = "SEARCH_OFFSET";
        private static final String APPS_OFFSET_KEY = "APPS_OFFSET";
        private static final String DISPLAYABLES_KEY = "DISPLAYABLES";
        private static final String LOADING_KEY = "LOADING_KEY";
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

        protected SearchAdapter adapter;
        protected String storeName;
        private SearchAppConverter searchAppConverter;

        private String query;
        private boolean trusted;
        private boolean loading;
        private List<Store> stores;
        private Parcelable listState;
        private int searchOffset;
        private List<Displayable> displayables;
        private boolean generalError;
        private boolean networkError;
        private boolean emptyResult;
        private int appsOffset;
        private CacheKeyFactory cacheKeyFactory;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            storeName = getArguments().getString(SearchActivity.SEARCH_STORE_NAME);
            query = getArguments().getString(SearchActivity.SEARCH_QUERY);
            trusted = getArguments().getBoolean(SearchActivity.SEARCH_ONLY_TRUSTED_APPS);
            cacheKeyFactory = new CacheKeyFactory();

            searchAppConverter = new SearchAppConverter(BUCKET_SIZE);

            if (savedInstanceState != null) {
                listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
                searchOffset = savedInstanceState.getInt(SEARCH_OFFSET_KEY);
                appsOffset = savedInstanceState.getInt(APPS_OFFSET_KEY);
                displayables = savedInstanceState.getParcelableArrayList(DISPLAYABLES_KEY);
                loading = savedInstanceState.getBoolean(LOADING_KEY);
                generalError = savedInstanceState.getBoolean(GENERAL_ERROR_KEY);
                networkError = savedInstanceState.getBoolean(NETWORK_ERROR_KEY);
                emptyResult = savedInstanceState.getBoolean(EMPTY_RESULT_KEY);
                stores = savedInstanceState.getParcelableArrayList(STORE_LIST_KEY);
            } else {
                displayables = new ArrayList<>();
                stores = formatStore(storeName, new AptoideDatabase(Aptoide.getDb()));
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
            searchForApps(stores);
        }

        @Override
        public void onResume() {
            super.onResume();
            getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
                @Override
                public void onLoadMore() {
                    searchForApps(searchOffset, stores);
                }

                @Override
                public int getOffset() {
                    return appsOffset;
                }

                @Override
                public boolean isLoading() {
                    return loading;
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
            retryNoNetwork.setOnClickListener(null);
            retryError.setOnClickListener(null);
            searchButton.setOnClickListener(null);
            searchQuery.setOnEditorActionListener(null);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelable(LIST_STATE_KEY, getRecyclerView().getLayoutManager().onSaveInstanceState());
            outState.putParcelableArrayList(DISPLAYABLES_KEY, new ArrayList<Parcelable>(displayables));
            outState.putInt(SEARCH_OFFSET_KEY, searchOffset);
            outState.putInt(APPS_OFFSET_KEY, appsOffset);
            outState.putBoolean(LOADING_KEY, loading);
            outState.putBoolean(EMPTY_RESULT_KEY, emptyResult);
            outState.putBoolean(NETWORK_ERROR_KEY, networkError);
            outState.putBoolean(GENERAL_ERROR_KEY, generalError);
            outState.putParcelableArrayList(STORE_LIST_KEY, new ArrayList<Parcelable>(stores));
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbindViews();
        }

        private void retrySearchForApps() {
            removeErrorViews();
            searchForApps(stores);
        }

        protected void startSearch() {
            Intent intent = new Intent(getActivity(), MoreSearchActivity.class);
            intent.putExtra(SearchActivity.SEARCH_QUERY, query);
            intent.putExtra(SearchActivity.SEARCH_STORE_NAME, storeName);
            intent.putExtra(SearchActivity.SEARCH_ONLY_TRUSTED_APPS, trusted);
            startActivity(intent);
        }

        private void searchForApps(List<Store> stores) {
            if (!isErrorViewShown()
                    && appsOffset == 0) {
                searchForApps(0, stores);
            }
        }

        private void searchForApps(final int offset, List<Store> stores) {
            setLoading(true);
            String key = SearchActivity.CONTEXT + query + trusted + SearchActivity.SEARCH_LIMIT + offset + getStoreListCacheKey(stores);
            spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                    SearchActivity.SEARCH_LIMIT, offset, stores),
                    cacheKeyFactory.create(key), SEARCH_CACHE_DURATION,
                    new RequestListener<ListSearchApps>() {

                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            showErrorView(spiceException);
                        }

                        @Override
                        public void onRequestSuccess(ListSearchApps listSearchApps) {
                            if (!isErrorViewShown()) {
                                updateSearchOffset(listSearchApps);
                                addApps(displayables, searchAppConverter.convert(
                                        getSearchItemList(listSearchApps), appsOffset, false));
                                setLoading(false);
                                treatEmptyList();
                            }
                        }
                    });
        }

        private void treatEmptyList() {
            if (isListEmpty()) {
                showEmptyResultView();
                Analytics.Search.noSearchResultEvent(query);
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

        private void addApps(List<Displayable> displayables, List<SearchApp> apps) {
            if (!apps.isEmpty()) {

                int notifyPositionStart;
                int notifyItemCount = 0;
                if (appsOffset == 0) {
                    if (isStoreSearch() || !Aptoide.getConfiguration().isMultipleStores()) {
                        displayables.add(0, new HeaderRow(AptoideUtils.StringUtils.getFormattedString(getContext(), R.string.results_in_store, Aptoide.getConfiguration().getMarketName().replaceFirst(" Store","")), false, BUCKET_SIZE));
                    } else {
                        displayables.add(0, new HeaderRow(getString(R.string.results_subscribed), false, BUCKET_SIZE));
                    }
                    displayables.addAll(1, apps);
                    notifyPositionStart = 1;
                    notifyItemCount = 1;
                } else {
                    // Sum apps header
                    displayables.addAll(1 + appsOffset, apps);
                    notifyPositionStart = 1 + appsOffset;
                }
                notifyItemCount += apps.size();
                appsOffset += apps.size();
                adapter.notifyItemRangeInserted(notifyPositionStart, notifyItemCount);
            }
        }

        private void updateSearchOffset(ListSearchApps listSearchApps) {
            if (listSearchApps != null
                    && listSearchApps.datalist != null
                    && listSearchApps.datalist.next != null) {
                searchOffset = listSearchApps.datalist.next.intValue();
            }
        }

        private void restoreState() {
            getRecyclerView().getLayoutManager().onRestoreInstanceState(listState);

            if (emptyResult) {
                showEmptyResultView();
            }

            if (generalError) {
                showGeneralErrorView();
            }

            if (networkError) {
                showNoNetworkErrorView();
            }

            if (loading && searchOffset > 0) {
                searchForApps(searchOffset, stores);
            }
        }

        protected void bindViews(View view) {
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

        protected void unbindViews() {
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

        private void showEmptyResultView() {
            getRecyclerView().setVisibility(View.GONE);
            noSearchResultLayout.setVisibility(View.VISIBLE);
            searchQuery.setText(query);
            emptyResult = true;
        }

        private boolean isListEmpty() {
            return searchOffset == 0;
        }

        private void showErrorView(Exception exception) {
            setLoading(false);
            Logger.printException(exception);
            if (isListEmpty() && !isErrorViewShown()) {
                spiceManager.cancelAllRequests();
                if (exception instanceof NoNetworkException) {
                    showNoNetworkErrorView();
                } else {
                    showGeneralErrorView();
                }
            }
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

        private boolean isStoreSearch() {
            return storeName != null && !TextUtils.isEmpty(storeName);
        }

        private List<Store> formatStore(String storeName, AptoideDatabase database) {
            if (isStoreSearch()) {
                final Store store = database.getSubscribedStore(storeName);
                if (store != null) {
                    return Collections.singletonList(store);
                }
            }
            return database.getSubscribedStores();
        }

        private String getStoreListCacheKey(List<Store> stores) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (Store store : stores) {
                stringBuilder.append(store.getName());
            }
            return stringBuilder.toString();
        }

        public void setLoading(boolean loading) {
            if (loading) {
                if (!this.loading) {
                    showLoading();
                }
            } else {
                if (this.loading) {
                    removeLoading();
                }
            }
            this.loading = loading;
        }

        private void removeLoading() {
            if (!displayables.isEmpty()) {
                displayables.remove(displayables.size() - 1);
                adapter.notifyItemRemoved(displayables.size());
            }
        }

        private void showLoading() {
            displayables.add(new ProgressBarRow(BUCKET_SIZE));
            adapter.notifyItemInserted(displayables.size());
        }
    }
}
