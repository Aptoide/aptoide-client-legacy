package com.aptoide.amethyst.fragments.store;

import android.os.Bundle;

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.persistence.DurationInMillis;

import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;

import static com.aptoide.amethyst.utils.AptoideUtils.RepoUtils.buildStoreWidgetRequest;

/**
 * Json example for this kind of tab:
 * "type": "API",
 * "name": "getStoreWidgets",
 * "label": "Aptoide Publishers",
 * "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_3239
 * <p/>
 * Created by rmateus on 23/06/15.
 */
public class CategoryFragment extends BaseWebserviceFragment {


    public static CategoryFragment newInstance(Bundle args, String actionUrl) {
        CategoryFragment categoryFragment = new CategoryFragment();
        args.putString("actionUrl", actionUrl);
        categoryFragment.setArguments(args);

        return categoryFragment;
    }

    String actionUrl, cacheKey;
    long storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionUrl = getArguments().getString("actionUrl");
        storeId = getArguments().getLong(Constants.STOREID_KEY, -1);
        if (actionUrl == null) {
            actionUrl = "";
        }

        try {
            cacheKey = actionUrl.substring(actionUrl.lastIndexOf("%") + 3, actionUrl.length());
            Logger.i(this, "cacheKey: " + cacheKey);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Logger.printException(e);
        }

    }

    @Override
    protected void executeSpiceRequest(boolean useCache) {

        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
        spiceManager.execute(
                buildStoreWidgetRequest(storeId, actionUrl),
                cacheKey + "-categoryStore-" + getStoreId() + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                cacheExpiryDuration,
                listener);
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new HomeTabAdapter(displayableList, null, getStoreTheme(),getStoreName());
        }
        return adapter;
    }

    @Override
    protected String getBaseContext() {
        return cacheKey;
    }

}
