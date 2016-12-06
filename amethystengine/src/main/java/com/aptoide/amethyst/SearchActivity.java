package com.aptoide.amethyst;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.ui.SearchManager;
import com.aptoide.amethyst.utils.AptoideUtils;

import com.aptoide.amethyst.fragments.SearchFragment;
import com.aptoide.amethyst.utils.SearchUtils;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchActivity extends AptoideBaseActivity {

    public static final String CONTEXT = "Search";
    public static final String SEARCH_QUERY = "query";
    public static final String SEARCH_STORE_NAME = "search_source";
    public static final String SEARCH_STORE_THEME = "search_theme";
    public static final String SEARCH_ONLY_TRUSTED_APPS = "trusted";
    public static final int SEARCH_LIMIT = 7;
    private static final String ARG_SECONDARY = "arg_secondary";

    /**
     * Indicates whether this search activity is secondary. If it is, it should be replaced with
     * the next search takes place.
     */
    private boolean secondary;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        bindViews();
        EnumStoreTheme storeTheme = null;
        try {
            storeTheme = (EnumStoreTheme) getIntent().getExtras().get(SEARCH_STORE_THEME);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        String storeName = getIntent().getStringExtra(SEARCH_STORE_NAME);
		String query = getIntent().getExtras().getString(SearchActivity.SEARCH_QUERY);
        boolean trustedAppsOnly = getIntent().getExtras().getBoolean(SEARCH_ONLY_TRUSTED_APPS, false);

		Analytics.Search.searchTerm(query, storeName);

		if (storeName != null && !TextUtils.isEmpty(storeName)) {
            Intent intent = new Intent(SearchActivity.this, Aptoide.getConfiguration().getMoreSearchActivity());
			intent.putExtra(SEARCH_QUERY, query);
			intent.putExtra(SEARCH_STORE_NAME, storeName);
			intent.putExtra(SEARCH_STORE_THEME, storeTheme);
            intent.putExtra(SEARCH_ONLY_TRUSTED_APPS, trustedAppsOnly);
			startActivity(intent);
			finish();
			return;
		}
        setSupportActionBar(mToolbar);
        ActionBar supportActBar = getSupportActionBar();
        if (supportActBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (Aptoide.getConfiguration().getDefaultStore().contains("apps")) {
                //getSupportActionBar().setLogo(R.drawable.ic_aptoide_toolbar);
            }
        }

        getSupportActionBar().setTitle(AptoideUtils.StringUtils.getFormattedString(this, R.string.search_activity_title, query));

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, SearchFragment
                    .newInstance(getIntent().getStringExtra(SearchActivity.SEARCH_QUERY), trustedAppsOnly), "").commit();
        }

		secondary = getIntent().getBooleanExtra(ARG_SECONDARY, false);
	}

    protected int getContentView() {
        return R.layout.activity_fragment_layout;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected String getScreenName() {
        return null;
    }

    @Override
    public void startActivity(final Intent intent) {
        final ComponentName name = intent.getComponent();
        if (name != null && SearchActivity.class.getName().equals(name.getClassName())) {
            intent.putExtra(ARG_SECONDARY, true);
            if (secondary) {
                finish();
            }
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_search_menu, menu);
        SearchManager.setupSearch(menu, this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home || item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SEARCH_STORE_THEME {
    }
}
