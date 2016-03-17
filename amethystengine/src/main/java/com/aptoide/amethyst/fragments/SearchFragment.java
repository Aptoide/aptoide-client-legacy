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

import com.aptoide.amethyst.LinearRecyclerFragment;
import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.SearchApk;
import com.aptoide.amethyst.models.search.SearchResults;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.SearchRequest;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SuggestedAppDisplayable;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;


import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.adapter.SearchAdapter;
import com.aptoide.models.displayables.SearchMoreHeader;

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
    protected int offset = 0, u_offset = 0;
    boolean mLoading = false;
    private final static String TAG = SearchFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString("search");
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

    RequestListener<SearchResults> listener = new RequestListener<SearchResults>() {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleRequestError(spiceException);
        }

        @Override
        public void onRequestSuccess(SearchResults searchResults) {
            handleSuccessCondition();
            List<SearchApk> apkList = searchResults.apkList;
            List<SearchApk> uApkList = searchResults.uApkList;
//            displayables.clear();
            if (!apkList.isEmpty()) {
                HeaderRow results = new HeaderRow(getString(R.string.results_subscribed), false, BUCKET_SIZE);
                displayables.add(results);
                displayables.addAll(apkList);
                displayables.add(new SearchMoreHeader(BUCKET_SIZE));
            }

            if (!uApkList.isEmpty()) {
                displayables.add(new HeaderRow(getString(R.string.other_stores), false, BUCKET_SIZE));
                displayables.addAll(uApkList);
            } else {
                displayables.addAll(uApkList);
            }
            u_offset += uApkList.size();
            adapter.notifyDataSetChanged();
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
                return u_offset;
            }

            @Override
            public boolean isLoading() {
                return mLoading;
            }
        });
    }

    public static SearchFragment newInstance(String query) {
        SearchFragment searchFragment = new SearchFragment();

        Bundle args = new Bundle();

        args.putString("search", query);

        searchFragment.setArguments(args);

        return searchFragment;
    }

    private void executeSpiceManager() {
        long cacheDuration = DurationInMillis.ONE_HOUR * 6;
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchRequest(query, SearchRequest.SEARCH_LIMIT, SearchRequest.OTHER_REPOS_SEARCH_LIMIT, offset, u_offset),
                SearchActivity.CONTEXT+query, cacheDuration, listener);
    }

    private void executeEndlessSpiceRequest() {
        long cacheDuration = DurationInMillis.ONE_HOUR * 6;
        spiceManager.execute(AptoideUtils.RepoUtils.buildSearchRequest(query, SearchRequest.SEARCH_LIMIT, SearchRequest.OTHER_REPOS_SEARCH_LIMIT, offset, u_offset),
                SearchActivity.CONTEXT+query+u_offset,cacheDuration,new RequestListener<SearchResults>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(SearchResults searchResults) {

                if (mLoading && !displayables.isEmpty()) {
                    displayables.remove(displayables.size() - 1);
                    adapter.notifyItemRemoved(displayables.size());
                }

                List<SearchApk> uApkList = searchResults.uApkList;
                if (!uApkList.isEmpty()) {
                    displayables.addAll(uApkList);
                }
                u_offset += uApkList.size();
                adapter.notifyDataSetChanged();
                mLoading = false;
            }
        });
    }
}
