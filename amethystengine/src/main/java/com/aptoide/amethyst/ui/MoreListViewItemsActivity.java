/*******************************************************************************
 * Copyright (c) 2015 hsousa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.dialogs.AdultHiddenDialog;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.models.displayables.ProgressBarRow;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

/**
 * This Activity shows Apps and Stores
 */
public class MoreListViewItemsActivity extends MoreActivity {
    private String storeName;
    private EnumStoreTheme storeTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeTheme = (EnumStoreTheme) getIntent().getExtras().get(SearchActivity.SEARCH_THEME);
        storeName = getIntent().getStringExtra(SearchActivity.SEARCH_SOURCE);
    }

    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = MoreListViewItemsFragment.newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchActivity.SEARCH_SOURCE, storeName);
            intent.putExtra(SearchActivity.SEARCH_THEME, storeTheme);
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager.setupSearch(menu, this);
        return super.onCreateOptionsMenu(menu);
    }

    public static class MoreListViewItemsFragment extends BaseWebserviceFragment {

        String eventActionUrl;
        String storeName;
        boolean mLoading = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            eventActionUrl = args.getString("eventActionUrl");
            storeName = args.getString(SearchActivity.SEARCH_SOURCE);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
                @Override
                public int getOffset() {
                    return offset;
                }

                @Override
                public boolean isLoading() {
                    return mLoading;
                }

                @Override
                public void onLoadMore() {
                    if (offset < total) {
                        mLoading = true;
                        displayableList.add(new ProgressBarRow(BUCKET_SIZE));
                        adapter.notifyItemInserted(adapter.getItemCount());

                        executeEndlessSpiceRequest();
                    }
                }
            });
        }

        public static Fragment newInstance() {
            return new MoreListViewItemsFragment();
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {

            this.useCache = useCache;
            this.offset = useCache ? offset : 0;

            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildViewItemsRequest(storeName, eventActionUrl, getLayoutMode(), offset),
                    getBaseContext() + parseActionUrlIntoCacheKey(eventActionUrl) + getStoreId() + BUCKET_SIZE + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                    cacheExpiryDuration,
                    listener);
        }

        protected void executeEndlessSpiceRequest() {
            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildViewItemsRequest(storeName, eventActionUrl, getLayoutMode(), offset),
                    getBaseContext() + parseActionUrlIntoCacheKey(eventActionUrl) + getStoreId() + BUCKET_SIZE + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false) + offset,
                    cacheExpiryDuration,
                    new RequestListener<StoreHomeTab>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            handleErrorCondition(spiceException);
                        }

                        @Override
                        public void onRequestSuccess(StoreHomeTab tab) {
                            if (getView() == null) {
                                return;
                            }

                            if (mLoading && !displayableList.isEmpty()) {
                                displayableList.remove(displayableList.size() - 1);
                                adapter.notifyItemRemoved(displayableList.size());
                            }

                            int index = displayableList.size();
                            displayableList.addAll(tab.list);
                            adapter.notifyItemRangeInserted(index, tab.list.size());


                            // total and new offset is red here
                            offset = tab.offset;
                            total = tab.total;

                            mLoading = false;

                            // check for hidden items
                            if (tab.hidden > 0 && AptoideUtils.getSharedPreferences().getBoolean(Constants.SHOW_ADULT_HIDDEN, true) && getFragmentManager().findFragmentByTag(Constants.HIDDEN_ADULT_DIALOG) == null) {
                                AdultHiddenDialog dialog = new AdultHiddenDialog();
                                AptoideDialog.showDialogAllowingStateLoss(dialog, getFragmentManager(), Constants.HIDDEN_ADULT_DIALOG);
                            }
                        }
                    }
            );
        }

        @Override
        protected BaseAdapter getAdapter() {
            return new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme(),getStoreName());
        }

        @Override
        protected String getBaseContext() {
            return "GetMoreListApps";
        }

        protected String getLayoutMode() {
            return Constants.LAYOUT_GRID;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }
    }


}
