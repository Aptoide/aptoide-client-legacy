package com.aptoide.amethyst.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.database.SimpleCursorLoader;
import com.squareup.otto.Subscribe;


import com.aptoide.amethyst.adapter.RollBackAdapter;
import com.aptoide.amethyst.adapter.RollbackSectionListAdapter;

import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;

//TODO: strings refactor, flurry e feedbackactivity
public class RollbackActivity extends AptoideBaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Toolbar mToolbar;

    private RollBackAdapter rollBackAdapter;

    @Subscribe
    public void onInstalledApkEvent(OttoEvents.InstalledApkEvent event) {
        refreshRollbackList();
    }

    @Subscribe
    public void onUnistalledApkEvent(OttoEvents.UnInstalledApkEvent event) {
        refreshRollbackList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        bindViews();

        rollBackAdapter = new RollBackAdapter(this);

        ListView lView = (ListView) findViewById(R.id.rollback_list);
        lView.setDivider(null);

        RollbackSectionListAdapter adapter = new RollbackSectionListAdapter(getLayoutInflater(), rollBackAdapter);

        lView.setAdapter(adapter);

        mToolbar.setCollapsible(false);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.rollback));
    }

    protected int getContentView() {
        return R.layout.page_rollback;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.home) {
            finish();
        } else if (i == R.id.menu_clear_rollback) {
            Analytics.Rollback.clear();
//            FlurryAgent.logEvent("Rollback_Cleared_Rollback_List");
            new AptoideDatabase(Aptoide.getDb()).deleteRollbackItems();
            getSupportLoaderManager().restartLoader(17, null, this);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_rollback_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SimpleCursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                Cursor cursor = new AptoideDatabase(Aptoide.getDb()).getRollbackActions();
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        rollBackAdapter.swapCursor(cursor);
        if(cursor.getCount()==0){
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        rollBackAdapter.swapCursor(null);
    }

    public void refreshRollbackList() {
        getSupportLoaderManager().restartLoader(17, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        getSupportLoaderManager().restartLoader(17, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FlurryAgent.onStartSession(this, getResources().getString(R.string.FLURRY_KEY));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        FlurryAgent.onEndSession(this);
    }

    @Override
    protected String getScreenName() {
        return "Roolback";
    }

}
