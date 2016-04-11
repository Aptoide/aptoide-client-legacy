package com.aptoide.amethyst.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.SpiceManager;


import com.aptoide.amethyst.SearchActivity;

/**
 * Created by hsousa on 05-10-2015.
 */
public abstract class MoreActivity extends AptoideBaseActivity {

    protected Toolbar mToolbar;

    SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    String eventActionUrl;
    String eventName;
    String eventType;
    String label;
    private EnumStoreTheme storeTheme;
    String packageName;
    String localyticstag;
    private boolean restored;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        Bundle args;
        if (savedInstanceState != null) {
            // Fragment is being recreated by itself on Rotation, so we shouldn't add the fragment again.
            args = savedInstanceState;
            restored = true;
        } else {
            args = getIntent().getExtras();
        }

        mToolbar.setCollapsible(false);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (args.getString(SearchActivity.SEARCH_SOURCE) != null) {
                mToolbar.setLogo(R.drawable.ic_store);
            } else {
                mToolbar.setLogo(R.drawable.ic_aptoide_toolbar);
            }
        }

        if (args == null) { // no Bundle, no gain
            Toast.makeText(this, R.string.error_occured, Toast.LENGTH_LONG).show();
            finish();
        } else {
            eventActionUrl = args.getString(Constants.EVENT_ACTION_URL);
            eventName = args.getString(Constants.EVENT_NAME);
            eventType = args.getString(Constants.EVENT_TYPE);
            label = args.getString(Constants.EVENT_LABEL);
            localyticstag = args.getString(Constants.LOCALYTICS_TAG);
            storeTheme = EnumStoreTheme.values()[args.getInt(Constants.THEME_KEY, 0)];
            packageName = args.getString(Constants.PACKAGENAME_KEY);
            getSupportActionBar().setTitle(label);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(storeTheme.getColor700tint()));
            }
            mToolbar.setBackgroundColor(getResources().getColor(storeTheme.getStoreHeader()));

            if (!restored) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, getFragment(args)).commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EVENT_ACTION_URL, eventActionUrl);
        outState.putString(Constants.EVENT_NAME, eventName);
        outState.putString(Constants.EVENT_TYPE, eventType);
        outState.putString(Constants.EVENT_LABEL, label);
        outState.putInt(Constants.THEME_KEY, storeTheme == null ? 0 : storeTheme.ordinal());
        outState.putString(Constants.PACKAGENAME_KEY, packageName);
    }

    public EnumStoreTheme getStoreTheme() {
        return storeTheme;
    }

    protected abstract Fragment getFragment(Bundle args);

    @Override
    protected String getScreenName() {
        if (localyticstag == null) {
            return null;
        }
        return AptoideUtils.StringUtils.parseLocalyticsTag(localyticstag);
    }

    @Override
    public void onDestroy() {
        // Temporary workaround from memory issues
        AptoideUtils.UI.unbindDrawables(findViewById(R.id.fragment_layout));
        super.onDestroy();
    }

    protected int getContentView() {
        return R.layout.activity_fragment_layout;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}
