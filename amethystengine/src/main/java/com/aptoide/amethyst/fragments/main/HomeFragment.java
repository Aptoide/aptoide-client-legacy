package com.aptoide.amethyst.fragments.main;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.squareup.otto.Subscribe;

import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

/**
 * Created by rmateus on 02/06/15.
 * Refactored by hsousa
 */
public class HomeFragment extends BaseWebserviceFragment {

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    /**
     * Besides setting the cacheName, in HomeFragment it is used as a Webservice variable to control
     * wether we're in Home or in Store
     * @return
     */
    @Override
    protected String getBaseContext() {
        return Constants.HOME_CONTEXT;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme(),getStoreName(),getStoreId());
    }

    @Override
    protected boolean isHomePage() {
        return true;
    }

    @Override
    protected long getStoreId() {
        return Defaults.DEFAULT_STORE_ID;
    }

    @Override
    public void onDetach() {
        BusProvider.getInstance().unregister(this);
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        BusProvider.getInstance().register(this);
        super.onAttach(context);
    }

    @Subscribe
    public void refreshAdultItem(OttoEvents.RepoCompleteEvent event) {
        executeSpiceRequest(false);
    }

}
