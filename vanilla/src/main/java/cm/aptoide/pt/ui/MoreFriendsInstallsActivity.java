package cm.aptoide.pt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.FeedBackActivity;
import com.aptoide.amethyst.webservices.ListApksInstallsRequest;
import com.aptoide.amethyst.webservices.json.TimelineListAPKsJson;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.hometab.TimelineRow;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;
import cm.aptoide.pt.adapter.main.HomeTabAdapter;
import cm.aptoide.pt.fragments.store.BaseWebserviceFragment;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_SendFeedBack) {
            FeedBackActivity.screenshot(this);
            startActivity(new Intent(this, FeedBackActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_feedback, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected String getScreenName() {
        return "More Friends Installs";
    }
}
