package com.aptoide.amethyst.fragments.main;

import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.CursorLoaderGridRecyclerFragment;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.database.SimpleCursorLoader;
import com.aptoide.amethyst.database.schema.Schema;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.StoreItem;
import com.aptoide.amethyst.preferences.AptoidePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.models.displayables.AddStoreRow;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.DisplayableList;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


import com.aptoide.amethyst.adapter.main.StoresTabAdapter;


/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 28-10-2013
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class StoresFragment extends CursorLoaderGridRecyclerFragment {
    SwipeRefreshLayout swipeContainer;
    ProgressBar progressBar;

    private List<Displayable> displayableList = new ArrayList<>();
    private StoresTabAdapter adapter;
    private boolean isMergeStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isMergeStore = savedInstanceState.getBoolean("isMerge");
        } else {
            isMergeStore = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean("mergeStores", false);
        }

        AptoideUtils.RepoUtils.addDefaultAppsStore(getContext());
    }

    protected void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout )view.findViewById(R.id.swipe_container);
        progressBar = (ProgressBar )view.findViewById(R.id.progress_bar);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(getView());
        adapter = new StoresTabAdapter(displayableList);
        getRecyclerView().setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }

    @Override
    protected int getColumnMultiplier() {
        return 1;
    }

    @Override
    protected int getColumnSize() {
        return AptoideUtils.UI.getStoreBucketSize();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccountManager.get(getContext()).getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0 &&
                !PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(AptoidePreferences.REPOS_SYNCED, false)) {
            AptoideUtils.RepoUtils.syncRepos(getContext(), spiceManager);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SimpleCursorLoader(getContext()) {
            @Override
            public Cursor loadInBackground() {
                return new AptoideDatabase(Aptoide.getDb()).getStoresCursor();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int storeBucketSize = AptoideUtils.UI.getStoreBucketSize();

        DisplayableList stores = new DisplayableList();
        if (data != null) {

            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                String theme = data.getString(data.getColumnIndex(Schema.Repo.COLUMN_THEME));
                if (theme != null) {
                    theme = theme.toUpperCase();
                } else {
                    theme = "DEFAULT";
                }

                Login login = null;
                if (!TextUtils.isEmpty(data.getString(data.getColumnIndex("username")))) {
                    login = new Login();
                    login.setUsername(data.getString(data.getColumnIndex("username")));
                    login.setPassword(data.getString(data.getColumnIndex("password")));
                }

                StoreItem storeItem = new StoreItem(
                        data.getString(data.getColumnIndex(Schema.Repo.COLUMN_NAME)),
                        data.getString(data.getColumnIndex(Schema.Repo.COLUMN_DOWNLOADS)),
                        data.getString(data.getColumnIndex(Schema.Repo.COLUMN_AVATAR)),
                        EnumStoreTheme.get(theme).getStoreHeader(), EnumStoreTheme.get(theme).ordinal(), "grid".equals(data.getString(data.getColumnIndex(Schema.Repo
                        .COLUMN_VIEW))), data.getLong(data
                        .getColumnIndex(Schema.Repo.COLUMN_ID)), login, storeBucketSize);

                stores.add(storeItem);
            }
        }

        // Add the final row as a button
        AddStoreRow storeRow = new AddStoreRow(storeBucketSize);
        stores.add(storeRow);

        displayableList.clear();
        displayableList.addAll(stores);
        adapter.notifyDataSetChanged();
        getActivity().supportInvalidateOptionsMenu();

        swipeContainer.setEnabled(false);
        progressBar.setVisibility(View.GONE);
    }


    public static Fragment newInstance() {
        return new StoresFragment();
    }



    @Subscribe
    public void refreshStoresEvent(OttoEvents.RepoAddedEvent event) {
        Logger.d("AptoideStoreFragment", "OnEvent " + event.getClass().getSimpleName());
        refresh();
    }

    @Subscribe
    public void refreshStoresEvent(OttoEvents.RepoDeletedEvent event) {
        Logger.d("AptoideStoreFragment", "OnEvent " + event.getClass().getSimpleName());
        if (event.stores != null && !event.stores.isEmpty()) {
            for (Store store : event.stores) {
                AptoideUtils.RepoUtils.removeStoreOnCloud(store, getActivity(), spiceManager);
            }
        }
        refresh();
    }

    public void refresh() {
        if (!isMergeStore) getLoaderManager().restartLoader(0, null, this);
    }
}