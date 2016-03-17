package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.DisplayableList;
import com.aptoide.models.displayables.ProgressBarRow;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

/**
 * Created by fabio on 28-10-2015.
 */
public class MoreVersionsActivity extends MoreActivity {

    @Override
    protected Fragment getFragment(Bundle args) {
        return MoreAppVersionsFragment.newInstance(args);
    }

    public static class MoreAppVersionsFragment extends BaseWebserviceFragment {

        public static final int MORE_APP_LIMIT = 9;
        boolean mLoading;
        int limit = MORE_APP_LIMIT;

        protected RequestListener<DisplayableList> listener = new RequestListener<DisplayableList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                handleErrorCondition(spiceException);
            }

            @Override
            public void onRequestSuccess(DisplayableList displayables) {

                if (displayables.size() > 0) {
                    handleSuccessCondition();
                    offset += displayables.size();
                    adapter = getAdapter();
                    setRecyclerAdapter(adapter);

                    displayableList.clear();
                    displayableList.addAll(displayables);
                } else {

                    handleNoItemsCondition();
                    TextView tvEmptyMsg = (TextView) getActivity().findViewById(R.id.tv_empty_msg);
                    if (tvEmptyMsg != null) {
                        tvEmptyMsg.setVisibility(View.VISIBLE);
                        tvEmptyMsg.setText(getString(R.string.no_apps_found));
                    }
                }
            }
        };

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
                @Override
                public void onLoadMore() {
                    if (displayableList.size() >= limit) {
                        mLoading = true;
                        displayableList.add(new ProgressBarRow(BUCKET_SIZE));
                        adapter.notifyItemInserted(adapter.getItemCount());
                        executeEndlessSpiceRequest();
                    }
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

        public static MoreAppVersionsFragment newInstance(Bundle args) {
            MoreAppVersionsFragment fragment = new MoreAppVersionsFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {
            this.useCache = useCache;
            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

            // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
            spiceManager.execute(
                    AptoideUtils.RepoUtils.GetMoreAppVersionsRequest(getArguments().getString(Constants.PACKAGENAME_KEY), limit, offset),
                    getBaseContext() + "-packageName-" + getArguments().getString(Constants.PACKAGENAME_KEY) + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false) + offset,
                    cacheExpiryDuration,
                    listener);
        }

        private void executeEndlessSpiceRequest() {
            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.GetMoreAppVersionsRequest(getArguments().getString(Constants.PACKAGENAME_KEY), limit, offset),
                    getBaseContext() + "-packageName-" + getArguments().getString(Constants.PACKAGENAME_KEY) + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false) + offset,
                    cacheExpiryDuration, new RequestListener<DisplayableList>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            if (mLoading && !displayableList.isEmpty()) {
                                displayableList.remove(displayableList.size() - 1);
                                adapter.notifyItemRemoved(displayableList.size());
                            }
                        }

                        @Override
                        public void onRequestSuccess(DisplayableList displayables) {

                            if (mLoading && !displayableList.isEmpty()) {
                                displayableList.remove(displayableList.size() - 1);
                                adapter.notifyItemRemoved(displayableList.size());
                            }


                            int index = displayableList.size();
                            displayableList.addAll(displayables);
                            adapter.notifyItemRangeInserted(index, displayables.size());

                            offset += displayables.size();
                            mLoading = false;
                        }
                    });

        }

        @Override
        protected BaseAdapter getAdapter() {
            if (adapter == null) {
                return new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme());
            } else {
                return adapter;
            }
        }

        @Override
        protected String getBaseContext() {
            return "search";
        }

    }
}
