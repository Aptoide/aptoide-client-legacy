package cm.aptoide.pt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.aptoide.amethyst.FeedBackActivity;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.models.AdItem;
import com.aptoide.models.ApkSuggestionJson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;
import cm.aptoide.pt.adapter.main.HomeTabAdapter;
import cm.aptoide.pt.fragments.store.BaseWebserviceFragment;

public class MoreHighlightedActivity extends MoreActivity {


    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = MoreHighlightedFragment.newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Analytics.Home.clickOnHighlighted();
    }

    public static class MoreHighlightedFragment extends BaseWebserviceFragment {

        // on v6, 50 was the limit
        private static final int ADS_LIMIT = 50;
        private BaseAdapter adapter;

        public static Fragment newInstance() {
            return new MoreHighlightedFragment();
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {

            final GetAdsRequest request = new GetAdsRequest();
            request.setLimit(ADS_LIMIT);
            request.setLocation("homepage");
            request.setKeyword("__NULL__");


            // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
            spiceManager.execute(request, new RequestListener<ApkSuggestionJson>() {

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    handleErrorCondition(spiceException);
                }

                @Override
                public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {
                    handleSuccessCondition();
                    displayableList.clear();

                    if (apkSuggestionJson != null && apkSuggestionJson.ads != null && apkSuggestionJson.ads.size() > 0) {

                        for (ApkSuggestionJson.Ads ad : apkSuggestionJson.ads) {
                            AdItem adItem = getAdItem(ad);
                            displayableList.add(adItem);
                        }

//                        getAdapter().notifyDataSetChanged();
                    }

                    setRecyclerAdapter(getAdapter());
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
            return "GetMoreHighlighted";
        }
    }

    @Override
    protected String getScreenName() {
        return "More Highlighted";
    }
}
