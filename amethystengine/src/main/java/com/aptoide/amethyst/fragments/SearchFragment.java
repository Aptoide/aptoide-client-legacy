package com.aptoide.amethyst.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.aptoide.amethyst.models.search.SearchItemSubscriptionFilter;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.dataprovider.webservices.models.v7.ListSearchApps;
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

    private static final long SEARCH_CACHE_DURATION = DurationInMillis.ONE_HOUR * 6;
    private static final String QUERY = "search";
    private static final String TRUSTED = "trusted";

    private SwipeRefreshLayout swipeContainer;
    private ScrollView noSearchResultLayout;
    private ImageView searchButton;
    private EditText searchQuery;
    private ProgressBar progressBar;
    private ScrollView layoutNoNetwork;
    private ScrollView layoutError;
    private TextView retryError;
    private TextView retryNoNetwork;

    private ArrayList<Displayable> displayables;
    private String query;
    private SearchAdapter adapter;
    private int suggestetedAppsOffset;
    private int unsubscribedAppsOffset;
	private int subscribedAppsOffset;
    private int searchOffset;
    private boolean loading = false;
    private boolean trusted;
    private SearchItemSubscriptionFilter searchItemFilter;
    private SearchApkConverter searchApkConverter;
    private AptoideDatabase database;

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
        database = new AptoideDatabase(Aptoide.getDb());
        searchItemFilter = new SearchItemSubscriptionFilter(database);
        query = getArguments().getString(QUERY);
        trusted = getArguments().getBoolean(TRUSTED);
        displayables = new ArrayList<>();
        adapter = new SearchAdapter(displayables);
        adapter.setQuery(query);
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
        searchForSubscribedApps();
    }

    protected void handleSuccessCondition() {
        progressBar.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        swipeContainer.setVisibility(View.VISIBLE);
    }

    private void startSearch() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_QUERY, searchQuery.getText().toString());
        getContext().startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchApkConverter = new SearchApkConverter(BUCKET_SIZE);
        bindViews(getView());
        getRecyclerView().setAdapter(adapter);

        searchForSuggestedApp();
        searchForSubscribedApps();

        getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
            @Override
            public void onLoadMore() {
                loading = true;
                displayables.add(new ProgressBarRow(BUCKET_SIZE));
                adapter.notifyItemInserted(adapter.getItemCount());
                searchForMoreUnsubscribedApps(searchOffset);
            }

            @Override
            public int getOffset() {

                int totalOffset = suggestetedAppsOffset;

                if (subscribedAppsOffset > 0) {
                    // Sum header and footer
                    totalOffset += 1 + subscribedAppsOffset + 1;
                }

                if (unsubscribedAppsOffset > 0) {
                    // Sum header
                    totalOffset += 1 + unsubscribedAppsOffset;
                }

                return totalOffset;
            }

            @Override
            public boolean isLoading() {
                return loading;
            }
        });
    }

    private void searchForSuggestedApp() {
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
                SuggestedAppDisplayable suggestedAppDisplayable = new SuggestedAppDisplayable(apkSuggestionJson);
                displayables.add(1, suggestedAppDisplayable);
                suggestetedAppsOffset = 2;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void searchForSubscribedApps() {
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                SearchActivity.SEARCH_LIMIT, 0, database.getSubscribedStores()),
                SearchActivity.CONTEXT+query, SEARCH_CACHE_DURATION, new RequestListener<ListSearchApps>() {

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        handleRequestError(spiceException);
                    }

                    @Override
                    public void onRequestSuccess(ListSearchApps listSearchApps) {
                        handleSuccessCondition();
                        updateList(listSearchApps);

                        swipeContainer.setEnabled(false);
                        progressBar.setVisibility(View.GONE);
                        loading = false;
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
                });
    }

    private void searchForMoreUnsubscribedApps(final int offset) {
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchAppsRequest(query, trusted,
                SearchActivity.SEARCH_LIMIT, offset), SearchActivity.CONTEXT + query +
                offset, SEARCH_CACHE_DURATION, new RequestListener<ListSearchApps>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ListSearchApps listSearchApps) {
                searchOffset = listSearchApps.datalist.next.intValue();
                removeLoadingListItem();
                updateList(listSearchApps);
                loading = false;
            }
        });
    }

    private void removeLoadingListItem() {
        if (loading && !displayables.isEmpty()) {
			displayables.remove(displayables.size() - 1);
			adapter.notifyItemRemoved(displayables.size());
		}
    }

    private void updateList(ListSearchApps listSearchApps) {
		final List<SearchApk> subscribedApps = searchApkConverter.convert(searchItemFilter.filterSubscribed(listSearchApps.datalist.list), subscribedAppsOffset, true);
		final List<SearchApk> unsubscribedApps = searchApkConverter.convert(searchItemFilter.filterUnsubscribed(listSearchApps.datalist.list), unsubscribedAppsOffset, false);

        if (!subscribedApps.isEmpty()) {
            if (subscribedAppsOffset == 0) { // Only display search results for subscribed apps once.
                displayables.add(0, new HeaderRow(getString(R.string.results_subscribed), false, BUCKET_SIZE));
                displayables.addAll(1, subscribedApps);
                subscribedAppsOffset += subscribedApps.size();
                // Sum suggested app header, suggested app, subscribed apps header and subscribed apps
                displayables.add(suggestetedAppsOffset + 1 + subscribedAppsOffset, new SearchMoreHeader(BUCKET_SIZE));
            }
        }

        if (!unsubscribedApps.isEmpty()) {
            if (unsubscribedAppsOffset == 0) {
                if (subscribedAppsOffset == 0) {
                    // Sum suggested app header and suggested app
                    displayables.add(suggestetedAppsOffset, new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                    displayables.addAll(1, unsubscribedApps);
                } else {
                    // Sum suggested app header, suggested app, subscribed apps header, subscribed apps and subscribed apps footer
                    displayables.add(suggestetedAppsOffset + 1 + subscribedAppsOffset + 1, new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                    // Sum suggested app header, suggested app, subscribed apps header, subscribed apps, subscribed apps footer and unsubscribed apps header
                    displayables.addAll(suggestetedAppsOffset + 1 + subscribedAppsOffset + 1 + 1, unsubscribedApps);
                }

            } else {
                if (subscribedAppsOffset == 0) {
                    // Sum suggested app header, suggested app and unsubscribed apps header
                    displayables.addAll(suggestetedAppsOffset + 1 + unsubscribedAppsOffset, unsubscribedApps);
                } else {
                    // Sum suggested app header, suggested app, subscribed apps header, subscribed apps, subscribed apps footer, unsubscribed apps header and unsubscribed apps
                    displayables.addAll(suggestetedAppsOffset + 1 + subscribedAppsOffset + 1 + 1 + unsubscribedAppsOffset, unsubscribedApps);
                }
            }
            unsubscribedAppsOffset += unsubscribedApps.size();
        }
        adapter.notifyDataSetChanged();
    }
}