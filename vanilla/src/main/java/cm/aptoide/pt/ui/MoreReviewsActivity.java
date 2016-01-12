package cm.aptoide.pt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.aptoide.amethyst.FeedBackActivity;
import com.aptoide.dataprovider.webservices.GetReviews;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.persistence.DurationInMillis;

import cm.aptoide.pt.R;
import cm.aptoide.pt.fragments.ReviewsFragment;

public class MoreReviewsActivity extends MoreActivity {

    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = new CommunityReviewsFragment();
        fragment.setArguments(args);
        return fragment;
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
        return "More Reviews";
    }

    public static class CommunityReviewsFragment extends ReviewsFragment {

        private boolean homepage;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            homepage = isHomePage();
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {
            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

            GetReviews.GetReviewList request = new GetReviews.GetReviewList();

            if (homepage) {
                request.setOrderBy("rand");
            }
            request.store_id = storeId;
            request.homePage = homepage;
            request.limit = REVIEWS_LIMIT;

            // in order to present the right info on screen after a screen rotation, always pass the bucketsize as cachekey
            spiceManager.execute(request, getBaseContext() + storeId + BUCKET_SIZE, cacheExpiryDuration,listener);
        }

        protected void executeEndlessSpiceRequest() {
            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

            GetReviews.GetReviewList request = new GetReviews.GetReviewList();

            if (homepage) {
                request.setOrderBy("rand");
            }
            request.store_id = storeId;
            request.homePage = homepage;
            request.limit = REVIEWS_LIMIT;
            request.offset = offset;

            spiceManager.execute(request, getBaseContext() + storeId + BUCKET_SIZE + offset, cacheExpiryDuration, endlessListener);
        }
    }
}
