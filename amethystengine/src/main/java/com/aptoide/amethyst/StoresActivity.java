package com.aptoide.amethyst;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.amethyst.webservices.v2.AlmostGenericResponseV2RequestListener;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.astuetz.PagerSlidingTabStrip;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Subscribe;

import java.net.HttpURLConnection;

import com.aptoide.amethyst.adapter.StorePagerAdapter;
import com.aptoide.amethyst.callbacks.AddCommentVoteCallback;
import com.aptoide.amethyst.ui.SearchManager;
import com.aptoide.amethyst.ui.dialogs.PasswordDialog;
import retrofit.RetrofitError;

/**
 * This class should always be called with a Bundle with valid args.
 * <p/>
 * It has some duplicate functionality with the HomeStoreFragment due to the
 * Software Requirement that the tabs must be created according to a webservice.
 */
public class StoresActivity extends AptoideBaseActivity implements AddCommentVoteCallback {

    public static Intent newIntent(@NonNull final Context context,
                                   final long storeId,
                                   @NonNull final String storeName,
                                   @NonNull final String storeAvatar,
                                   final int storeTheme) {
        final Intent intent = new Intent(context, StoresActivity.class);
        intent.putExtra(Constants.STOREID_KEY, storeId);
        intent.putExtra(Constants.STORENAME_KEY, storeName);
        intent.putExtra(Constants.STOREAVATAR_KEY, storeAvatar);
        intent.putExtra(Constants.THEME_KEY, storeTheme);
        intent.putExtra(Constants.DOWNLOAD_FROM_KEY, "store");
        boolean subscribed = new AptoideDatabase(Aptoide.getDb()).existsStore(storeId);
        intent.putExtra(Constants.STORE_SUBSCRIBED_KEY, subscribed);
        return intent;
    }


    PagerSlidingTabStrip mPagerSlidingTabStrip;
    ViewPager mViewPager;
    Toolbar mToolbar;
    AppBarLayout mAppBar;

    ProgressBar progressBar;
    ScrollView layoutNoNetwork;
    ScrollView layoutError;
    TextView retryError;
    TextView retryNoNetwork;


    private Bundle args;
    private String storeName;
    private long storeId;
    private boolean subscribed;
    private EnumStoreTheme storeTheme;
    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    protected int BUCKET_SIZE;

    RequestListener<StoreHomeTab> listener = new RequestListener<StoreHomeTab>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            final Throwable cause = spiceException.getCause();
            if (cause instanceof RetrofitError) {
                final RetrofitError retrofitError = (RetrofitError) cause;
                if (retrofitError.getResponse() != null && retrofitError.getResponse().getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    handleUnauthorized();
                }
            }
            handleErrorCondition(spiceException);
        }

        @Override
        public void onRequestSuccess(StoreHomeTab homeTab) {
            try {
                storeId = homeTab.store.nodes.meta.data.id.longValue();
                storeName = homeTab.store.nodes.meta.data.name;
                storeTheme = EnumStoreTheme.get(homeTab.store.nodes.meta.data.appearance.theme);
                args.putLong(Constants.STOREID_KEY, storeId);
                mViewPager.setAdapter(new StorePagerAdapter(getSupportFragmentManager(), args, homeTab.store.nodes.tabs.tabList));
                mPagerSlidingTabStrip.setViewPager(mViewPager);
                setupStoreTheme(storeTheme);

                handleSuccessCondition();
            } catch (Exception e) {
                handleErrorCondition(e);
            }
        }
    };

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        if (savedInstanceState != null) {
            args = savedInstanceState;
        } else {
            args = getIntent().getExtras();
            if (args == null) { // no Bundle, no gain
                Toast.makeText(this, R.string.error_occured, Toast.LENGTH_LONG).show();
                finish();
            }
        }
        BUCKET_SIZE = AptoideUtils.UI.getBucketSize();

        storeId = args.getLong(Constants.STOREID_KEY, 0);
        storeName = args.getString(Constants.STORENAME_KEY);
        storeTheme = EnumStoreTheme.values()[args.getInt(Constants.THEME_KEY, 0)];
        subscribed = args.getBoolean(Constants.STORE_SUBSCRIBED_KEY, false);

        mToolbar.setLogo(R.drawable.ic_store);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(storeName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        executeSpiceRequest(true);

        Analytics.Stores.enter(storeName);
    }

    protected int getContentView() {
        return R.layout.stores_main;
    }

    protected void bindViews() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip )findViewById(R.id.tabs);
        mViewPager = (ViewPager )findViewById(R.id.pager);
        mToolbar = (Toolbar )findViewById(R.id.toolbar);
        mAppBar = (AppBarLayout )findViewById(R.id.appbar);
        progressBar = (ProgressBar )findViewById(R.id.progress_bar);
        layoutNoNetwork = (ScrollView )findViewById(R.id.no_network_connection);
        layoutError = (ScrollView )findViewById(R.id.error);
        retryError = (TextView )findViewById(R.id.retry_error);
        retryNoNetwork = (TextView )findViewById(R.id.retry_no_network);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    private void setupStoreTheme(EnumStoreTheme storeTheme) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(storeTheme.getColor700tint()));
        }

        mAppBar.setBackgroundColor(getResources().getColor(storeTheme.getStoreHeader()));
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(storeTheme.getStoreHeader()), PorterDuff.Mode.SRC_IN);
        retryNoNetwork.getBackground().setColorFilter(getResources().getColor(storeTheme.getStoreHeader()), PorterDuff.Mode.SRC_IN);
        retryError.getBackground().setColorFilter(getResources().getColor(storeTheme.getStoreHeader()), PorterDuff.Mode.SRC_IN);
    }

    private void executeSpiceRequest(boolean useCache) {
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
        if (storeId <= 0) {
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildStoreRequest(storeName, Constants.STORE_CONTEXT),
                    Constants.STORE_CONTEXT + "-" + storeName + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                    cacheExpiryDuration,
                    listener);
        } else {
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildStoreRequest(storeId, Constants.STORE_CONTEXT),
                    Constants.STORE_CONTEXT + "-" + storeId + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                    cacheExpiryDuration,
                    listener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home || item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.STOREID_KEY, storeId);
        outState.putString(Constants.STORENAME_KEY, storeName);
        outState.putInt(Constants.THEME_KEY, storeTheme.ordinal());
        outState.putBoolean(Constants.STORE_SUBSCRIBED_KEY, subscribed);
    }


    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Temporary workaround from memory issues
        AptoideUtils.UI.unbindDrawables(findViewById(R.id.store_layout));
    }

    @Override
    public void voteComment(int commentId, AddApkCommentVoteRequest.CommentVote vote) {

        RequestListener<GenericResponseV2> commentRequestListener = new AlmostGenericResponseV2RequestListener() {
            @Override
            public void CaseOK() {
                Toast.makeText(StoresActivity.this, getString(R.string.vote_submitted), Toast.LENGTH_LONG).show();
            }
        };

        AptoideUtils.VoteUtils.voteComment(
                spiceManager,
                commentId,
                storeName,
                SecurePreferences.getInstance().getString("token", "empty"),
                commentRequestListener,
                vote);
    }

    @Subscribe
    public void onStoreCompleted(OttoEvents.RepoCompleteEvent event) {
        if (event.getRepoId() == storeId) {
            executeSpiceRequest(false);
        }
    }

    @Subscribe
    public void onStoreCompleted(OttoEvents.RepoSubscribeEvent event) {
        if (event.getStoreName().equals(storeName)) {
            args.putBoolean(Constants.STORE_SUBSCRIBED_KEY, subscribed = true);
            executeSpiceRequest(false);
        }
    }

    @Subscribe
    public void onStoreAuthorization(OttoEvents.StoreAuthorizationEvent event) {
        if (event.getId() == storeId) {
            final Login login = event.getLogin();
            new AptoideDatabase(Aptoide.getDb()).updateStoreLogin(storeId, login);
            retry();
        }
    }

    @Subscribe
    public void refreshStoresEvent(OttoEvents.RepoDeletedEvent event) {
        Logger.d("AptoideStoreActivity", "OnEvent " + event.getClass().getSimpleName());
        args.putBoolean(Constants.STORE_SUBSCRIBED_KEY, subscribed = false);
        if (event.stores != null && !event.stores.isEmpty()) {
            for (Store store : event.stores) {
                AptoideUtils.RepoUtils.removeStoreOnCloud(store, StoresActivity.this, spiceManager);
                if (store.getName()!=null && storeName != null && store.getName().equals(storeName)) {
                    executeSpiceRequest(false);
                }
            }
        }
    }

    @Subscribe
    public void subscribeRepo(OttoEvents.RepoSubscribeEvent event) {
        AptoideUtils.RepoUtils.startParse(event.getStoreName(), StoresActivity.this, spiceManager);
    }

    private void handleUnauthorized() {
        final DialogFragment dialogFragment = PasswordDialog.newInstance(storeId);
        dialogFragment.show(getSupportFragmentManager(), PasswordDialog.FRAGMENT_TAG);
    }

    private void handleErrorCondition(Exception e) {
        Logger.printException(e);
        progressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mAppBar.setVisibility(View.GONE);

        if (e instanceof NoNetworkException) {
            layoutError.setVisibility(View.GONE);
            layoutNoNetwork.setVisibility(View.VISIBLE);
            retryNoNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });

        } else {
            layoutNoNetwork.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
            retryError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });
        }
    }

    private void retry() {
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        executeSpiceRequest(false);
    }

    protected void handleSuccessCondition() {
        progressBar.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mAppBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String getScreenName() {
        return "More Top Stores";
    }
}
