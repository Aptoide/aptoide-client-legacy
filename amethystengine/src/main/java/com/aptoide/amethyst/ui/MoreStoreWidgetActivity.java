package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.persistence.DurationInMillis;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

public class MoreStoreWidgetActivity extends MoreActivity {


    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = MoreStoreWidgetFragment.newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    public static class MoreStoreWidgetFragment extends BaseWebserviceFragment {

        private String eventActionUrl;
        private long storeId;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            eventActionUrl = args.getString(Constants.EVENT_ACTION_URL);
            storeId = args.getLong(Constants.STOREID_KEY, -1);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        public static Fragment newInstance() {
            return new MoreStoreWidgetFragment();
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {

            this.useCache = useCache;

            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildStoreWidgetRequest(storeId, eventActionUrl),
                    getBaseContext() + parseActionUrlIntoCacheKey(eventActionUrl) + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                    cacheExpiryDuration,
                    listener);
        }

        @Override
        protected BaseAdapter getAdapter() {
            return new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme(),getStoreName());
        }

        @Override
        protected String getBaseContext() {
            return "GetMoreStoreWidgets";
        }

    }
}
