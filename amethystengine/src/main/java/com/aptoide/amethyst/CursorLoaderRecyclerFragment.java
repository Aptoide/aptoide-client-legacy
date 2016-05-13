package com.aptoide.amethyst;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hsousa on 26-06-2015.
 */
public abstract class CursorLoaderRecyclerFragment extends AptoideRecyclerFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * the initLoader instruction is needed because the Loader
     * isn't automatically initialized in a Fragment
     * @param view
     * @param savedInstanceState
     */
    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restartLoader();
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(0);
        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Re-starts the loader.
     */
    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
