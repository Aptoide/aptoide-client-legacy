package com.aptoide.amethyst.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;



import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;

import com.aptoide.amethyst.websockets.WebSocketSingleton;
import com.aptoide.amethyst.services.DownloadService;


/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 04-12-2013
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public final class SearchManager {

    private SearchManager() {
        throw new IllegalStateException("No instances");
    }

    private static boolean isSocketDisconnect;

    public static void setupSearch(Menu menu, final Activity activity) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final android.app.SearchManager searchManager = (android.app.SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                MenuItemCompat.collapseActionView(searchItem);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    MenuItemCompat.collapseActionView(searchItem);
                    isSocketDisconnect = true;

                    if (Build.VERSION.SDK_INT > 7) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (isSocketDisconnect) {
                                    WebSocketSingleton.getInstance().disconnect();
                                }

                            }
                        }, 10000);
                    }
                }
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSocketDisconnect = false;

                if (Build.VERSION.SDK_INT > 7) {
                    WebSocketSingleton.getInstance().connect();
                } else {
                    activity.onSearchRequested();
                    MenuItemCompat.collapseActionView(searchItem);
                }
            }
        });


        if (Build.VERSION.SDK_INT > 7) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        }
    }

}