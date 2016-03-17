package com.aptoide.amethyst.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.CursorLoaderLinearRecyclerFragment;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.database.schema.Schema;
import com.aptoide.models.displayables.ExcludedUpdate;

import java.util.ArrayList;
import java.util.List;


import com.aptoide.amethyst.adapter.ExcludedUpdateAdapter;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;

public class ExcludedUpdatesFragment extends CursorLoaderLinearRecyclerFragment {

    SwipeRefreshLayout swipeContainer;
    ProgressBar progressBar;

    private static final int MSG_NO_EXCLUDED_UPDATES = 1;
    public ArrayList<ExcludedUpdate> excludedUpdates = new ArrayList<>();
    @Nullable
    private ExcludedUpdateAdapter adapter;
    private AptoideDatabase db = new AptoideDatabase(Aptoide.getDb());

    protected void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout )view.findViewById(R.id.swipe_container);
        progressBar = (ProgressBar )view.findViewById(R.id.progress_bar);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(getView());
        adapter = new ExcludedUpdateAdapter(excludedUpdates);
        getRecyclerView().setAdapter(adapter);
        setHasOptionsMenu(true);

        getActivity().findViewById(R.id.swipe_container).setEnabled(false);

        startLoading(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_excluded_updates, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i == android.R.id.home || i == R.id.home) {
            getActivity().finish();
        } else if (i == R.id.menu_remove) {
            if (isAnyChecked(excludedUpdates)) {

                Analytics.ExcludedUpdates.restoreUpdates();

                final List<String[]> excludedStrings = new ArrayList<>();
                for (ExcludedUpdate excludedUpdate : excludedUpdates) {
                    if (excludedUpdate.isChecked()) {

                        excludedStrings.add(new String[]{excludedUpdate.getApkid(), String.valueOf(excludedUpdate.getVercode())});

//                            new AptoideDatabase(Aptoide.getDb()).deleteFromExcludeUpdate(excludedUpdate.getApkid(), excludedUpdate.getVercode());
//                        FlurryAgent.logEvent("Excluded_Updates_Removed_Update_From_List");
                    }
                }

                // Deletes the excluded updates from DB in a background thread, and then
                // restarts the loader on the UiThread.
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        new AptoideDatabase(Aptoide.getDb()).deleteFromExcludeUpdate(excludedStrings);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                restartLoader();
                            }
                        });
                    }
                }).start();

                BusProvider.getInstance().post(new OttoEvents.ExcludedUpdateRemovedEvent());
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.no_excluded_updates_selected, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (i == R.id.menu_select_all) {
            adapter.selectAll();
        } else if (i == R.id.menu_select_none) {
            adapter.selectNone();
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isAnyChecked(ArrayList<ExcludedUpdate> excludedUpdates){
        if(!excludedUpdates.isEmpty()) {
            for (ExcludedUpdate excludedUpdate : excludedUpdates) {
                if (excludedUpdate.isChecked()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ExcludedUpdatesFragment newInstance() {
        return new ExcludedUpdatesFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                return db.getExcludedApks();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        excludedUpdates.clear();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ExcludedUpdate excludedUpdate = new ExcludedUpdate(
                    c.getString(c.getColumnIndex(Schema.Excluded.COLUMN_NAME)),
                    c.getString(c.getColumnIndex(Schema.Excluded.COLUMN_PACKAGE_NAME)),
                    c.getString(c.getColumnIndex(Schema.Excluded.COLUMN_ICONPATH)),
                    c.getInt(c.getColumnIndex(Schema.Excluded.COLUMN_VERCODE)),
                    c.getString(c.getColumnIndex(Schema.Excluded.COLUMN_VERNAME)),
                    BUCKET_SIZE);
            excludedUpdates.add(excludedUpdate);
        }
        c.close();

        if (excludedUpdates.isEmpty()) {

            setUiNoUpdates(getView());

        } else {
            progressBar.setVisibility(View.GONE);
            swipeContainer.setEnabled(false);
        }

        adapter.notifyDataSetChanged();
    }


    private void startLoading(View view){
        progressBar.setVisibility(View.VISIBLE);
//        view.findViewById(R.id.list).setVisibility(View.GONE);
        view.findViewById(R.id.error).setVisibility(View.GONE);
    }

    private void setUiNoUpdates(View view) {
        TextView tv = (TextView)view.findViewById(R.id.tv_empty_msg);
        tv.setVisibility(View.VISIBLE);
        tv.setText(getString(R.string.noIgnoreUpdateText));
        progressBar.setVisibility(View.GONE);
        swipeContainer.setEnabled(false);
        view.findViewById(R.id.error).setVisibility(View.GONE);
    }

}