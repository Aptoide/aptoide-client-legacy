package com.aptoide.amethyst.fragments.store;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.aptoide.dataprovider.webservices.models.Constants;

import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;

/**
 * Created by rmateus on 23/06/15.
 * Refactored by hsousa
 */
public class HomeStoreFragment extends BaseWebserviceFragment {

    public static Fragment newInstance(Bundle args) {
        HomeStoreFragment fragment = new HomeStoreFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected String getBaseContext() {
        return Constants.STORE_CONTEXT;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new HomeTabAdapter(displayableList, getStoreTheme(), subscribed, getStoreName());
    }

    @Override
    protected boolean isStorePage() {
        return true;
    }

}
