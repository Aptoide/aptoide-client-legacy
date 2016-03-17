package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.webservices.ListApksInstallsRequest;
import com.aptoide.amethyst.webservices.json.TimelineListAPKsJson;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.TimelineRow;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;

public class MoreFriendsInstallsActivity extends MoreActivity {


    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = MoreFriendsInstallsFragment.newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    public static class MoreFriendsInstallsFragment extends BaseWebserviceFragment {

        public static Fragment newInstance() {
            return new MoreFriendsInstallsFragment();
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {

            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

            ListApksInstallsRequest request = new ListApksInstallsRequest();

            // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
            spiceManager.execute(
                    request,
                    getBaseContext() + BUCKET_SIZE + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                    cacheExpiryDuration,
                    new RequestListener<TimelineListAPKsJson>() {

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    handleErrorCondition(spiceException);
                }

                @Override
                public void onRequestSuccess(TimelineListAPKsJson timelineListAPKsJson) {
                    handleSuccessCondition();
                    displayableList.clear();
                    setRecyclerAdapter(getAdapter());

                    if (timelineListAPKsJson != null && timelineListAPKsJson.usersapks != null && timelineListAPKsJson.usersapks.size() > 0) {

                        for (TimelineListAPKsJson.UserApk userApk : timelineListAPKsJson.usersapks) {
                            TimelineRow timeline = getTimelineRow(userApk);
                            displayableList.add(timeline);
                        }

                        getAdapter().notifyDataSetChanged();
                    }
                }
            });

        }

        @Override
        protected BaseAdapter getAdapter() {
            if (adapter == null) {
                adapter = new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme(),getStoreName());
            }
            return adapter;
        }

        @Override
        protected String getBaseContext() {
            return "GetMoreFriendsInstall";
        }
    }

    @Override
    protected String getScreenName() {
        return "More Friends Installs";
    }
}
