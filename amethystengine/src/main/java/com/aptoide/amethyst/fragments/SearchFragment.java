package com.aptoide.amethyst.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.SearchRequest;
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

    SwipeRefreshLayout swipeContainer;
    ScrollView noSearchResultLayout;
    ImageView searchButton;
    EditText searchQuery;

    ProgressBar progressBar;
    ScrollView layoutNoNetwork;
    ScrollView layoutError;
    TextView retryError;
    TextView retryNoNetwork;

    private ArrayList<Displayable> displayables = new ArrayList<>();
    private String query;
    private SearchAdapter adapter = new SearchAdapter(displayables);
    private int displayUOffset = 0;
	private int displayOffset;
    private int searchOffset;
    boolean mLoading = false;
    private final static String TAG = SearchFragment.class.getSimpleName();
    private boolean trusted;
    private AptoideDatabase database;
    private SearchApkConverter searchApkConverter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AptoideDatabase(Aptoide.getDb());
        query = getArguments().getString("search");
        trusted = getArguments().getBoolean("trusted");
        if (adapter != null) {
            adapter.setQuery(query);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout )view.findViewById(R.id.swipe_container);
        noSearchResultLayout = (ScrollView )view.findViewById(R.id.no_search_results_layout);
        searchButton = (ImageView )view.findViewById(R.id.ic_search_button);
        searchQuery = (EditText )view.findViewById(R.id.search_text);
        progressBar = (ProgressBar )view.findViewById(R.id.progress_bar);
        layoutNoNetwork = (ScrollView )view.findViewById(R.id.no_network_connection);
        layoutError = (ScrollView )view.findViewById(R.id.error);
        retryError = (TextView )view.findViewById(R.id.retry_error);
        retryNoNetwork = (TextView )view.findViewById(R.id.retry_no_network);
    }

    RequestListener<ListSearchApps> listener = new RequestListener<ListSearchApps>() {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleRequestError(spiceException);
        }

        @Override
        public void onRequestSuccess(ListSearchApps listSearchApps) {
            handleSuccessCondition();
            updateList(listSearchApps);

            searchOffset += listSearchApps.datalist.limit.intValue();
            swipeContainer.setEnabled(false);
            progressBar.setVisibility(View.GONE);
            mLoading = false;
            if (displayables.size() <= 0) {
                getRecyclerView().setVisibility(View.GONE);
                noSearchResultLayout.setVisibility(View.VISIBLE);
                searchQuery.setText(query);
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
                Analytics.Search.noSearchResultEvent(query);
            }
        }
    };

    private void handleRequestError(Exception e) {
        Logger.printException(e);
        progressBar.setVisibility(View.GONE);
        swipeContainer.setVisibility(View.GONE);

        if (e instanceof NoNetworkException) {
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

    private void retry() {
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        swipeContainer.setVisibility(View.VISIBLE);
        executeSpiceManager();
    }

    protected void handleSuccessCondition() {
        progressBar.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        swipeContainer.setVisibility(View.VISIBLE);
    }

    private void startSearch() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra(android.app.SearchManager.QUERY, searchQuery.getText().toString());
        getContext().startActivity(intent);
    }

    RequestListener<ApkSuggestionJson> getAdsListener = new RequestListener<ApkSuggestionJson>() {

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {

            if (apkSuggestionJson.getAds().size() == 0) {
                return;
            }
            if (apkSuggestionJson.getAds().get(0).getPartner() == null) {
                return;
            }
            if (apkSuggestionJson.getAds().get(0).getPartner().getPartnerData() == null) {
                return;
            }

            String name = apkSuggestionJson.getAds().get(0).getData().name;
            float size = apkSuggestionJson.getAds().get(0).getData().size.floatValue() / 1024 / 1024;
            String description = apkSuggestionJson.getAds().get(0).getData().description;
            float rating = apkSuggestionJson.getAds().get(0).getData().stars.floatValue();
            String iconPath = apkSuggestionJson.getAds().get(0).getData().icon;

//            SuggestedAppDisplayable suggestedAppDisplayable = new SuggestedAppDisplayable(3, name, size, description, rating, iconPath);
            HeaderRow suggestedAppHeader = new HeaderRow("Suggested App", false, BUCKET_SIZE);
            displayables.add(0, suggestedAppHeader);
            SuggestedAppDisplayable suggestedAppDisplayable = new SuggestedAppDisplayable(apkSuggestionJson);
            displayables.add(1, suggestedAppDisplayable);

            adapter.notifyDataSetChanged();
//            swipeContainer.setEnabled(false);
//            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchApkConverter = new SearchApkConverter(BUCKET_SIZE);
        bindViews(getView());
        getRecyclerView().setAdapter(adapter);

        executeSpiceManager();
        GetAdsRequest getAdsRequest = new GetAdsRequest();

        getAdsRequest.setLocation("search");
        getAdsRequest.setKeyword(query);
        getAdsRequest.setLimit(1);
        spiceManager.execute(getAdsRequest, getAdsListener);

        getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
            @Override
            public void onLoadMore() {
                mLoading = true;
                displayables.add(new ProgressBarRow(BUCKET_SIZE));
                adapter.notifyItemInserted(adapter.getItemCount());
                executeEndlessSpiceRequest();
            }

            @Override
            public int getOffset() {
                return displayOffset + displayUOffset;
            }

            @Override
            public boolean isLoading() {
                return mLoading;
            }
        });
    }

    public static SearchFragment newInstance(String query, boolean trusted) {
        SearchFragment searchFragment = new SearchFragment();

        Bundle args = new Bundle();

        args.putString("search", query);
        args.putBoolean("trusted", trusted);

        searchFragment.setArguments(args);

        return searchFragment;
    }

    private void executeSpiceManager() {
        long cacheDuration = DurationInMillis.ONE_HOUR * 6;
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                SearchRequest.SEARCH_LIMIT, 0),
                SearchActivity.CONTEXT+query, cacheDuration, listener);
    }

    private void executeEndlessSpiceRequest() {
        long cacheDuration = DurationInMillis.ONE_HOUR * 6;
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                SearchRequest.SEARCH_LIMIT, searchOffset), SearchActivity.CONTEXT + query + searchOffset, cacheDuration, new RequestListener<ListSearchApps>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ListSearchApps listSearchApps) {
                searchOffset += listSearchApps.datalist.limit.intValue();
                removeLoadingListItem();
                updateList(listSearchApps);
                mLoading = false;
            }
        });
    }

    private void removeLoadingListItem() {
        if (mLoading && !displayables.isEmpty()) {
			displayables.remove(displayables.size() - 1);
			adapter.notifyItemRemoved(displayables.size());
		}
    }

    private void updateList(ListSearchApps listSearchApps) {
		final List<SearchApk> subscribedApps = getSubscribedApps(listSearchApps.datalist.list);
		final List<SearchApk> unsubscribedApps = getUnsubscribedApps(listSearchApps.datalist.list);

        for (SearchApk searchApk: unsubscribedApps) {
            for (Displayable displayable: displayables) {
                if (displayable instanceof SearchApk
                        && ((SearchApk) displayable).name.equals(searchApk.name)) {
                    Log.d("Marcelo", "REPEATED " + searchApk.name);
                }
            }
        }

        if (!subscribedApps.isEmpty()) {
            if (displayOffset == 0) {
                displayables.add(0, new HeaderRow(getString(R.string.results_subscribed), false, BUCKET_SIZE));
                displayables.addAll(1, subscribedApps);
                displayOffset += subscribedApps.size();
                // Consider subscribed header and subscribed apps
                displayables.add(1 + displayOffset, new SearchMoreHeader(BUCKET_SIZE));
            }
        }

        if (!unsubscribedApps.isEmpty()) {
            if (displayUOffset == 0) {
                if (displayOffset == 0) {
                    displayables.add(0, new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                    displayables.addAll(1, unsubscribedApps);
                } else {
                    // Consider subscribed header, subscribed apps and subscribed footer
                    displayables.add(1 + displayOffset + 1, new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                    // Consider subscribed header, subscribed apps, subscribed footer and unsubscribed header
                    displayables.addAll(1 + displayOffset + 1 + 1, unsubscribedApps);
                }

            } else {
                if (displayOffset == 0) {
                    // Consider unsubscribed header
                    displayables.addAll(1 + displayUOffset, unsubscribedApps);
                } else {
                    // Consider subscribed header, subscribed apps, subscribed footer, unsubscribed header and unsubscribed apps
                    displayables.addAll(1 + displayOffset + 1 + 1 + displayUOffset, unsubscribedApps);
                }
            }
            displayUOffset += unsubscribedApps.size();
        }
        adapter.notifyDataSetChanged();
    }

    private List<SearchApk> getUnsubscribedApps(List<SearchItem> allSearchItems) {
        final List<SearchItem> unsubscribedSearchItems = new ArrayList<>(allSearchItems);
        unsubscribedSearchItems.removeAll(getSubscribedSearchItems(allSearchItems));
        return searchApkConverter.convert(unsubscribedSearchItems, false);
    }

    private List<SearchApk> getSubscribedApps(List<SearchItem> allSearchItems) {
        return searchApkConverter.convert(getSubscribedSearchItems(allSearchItems), true);
    }

    private List<SearchItem> getSubscribedSearchItems(List<SearchItem> allSearchItems) {
        final List<String> subscribedStoresNames = database.getSubscribedStoreNames();
        final List<SearchItem> subscribedSearchItems = new ArrayList<>();

        for (String storeName: subscribedStoresNames) {
            for (SearchItem app: allSearchItems) {
                if (app.store.name.equals(storeName)) {
                    subscribedSearchItems.add(app);
                }
            }
        }
        return subscribedSearchItems;
    }
}
