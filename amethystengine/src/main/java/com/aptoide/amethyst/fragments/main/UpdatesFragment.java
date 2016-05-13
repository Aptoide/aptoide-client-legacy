package com.aptoide.amethyst.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.GridRecyclerFragment;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapter.main.UpdatesTabAdapter;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.database.schema.Schema;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.services.UpdatesService;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.UpdateHeaderRow;
import com.aptoide.models.displayables.InstallRow;
import com.aptoide.models.displayables.UpdateRow;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;

/**
 * Created by rmateus on 17/06/15.
 */
public class UpdatesFragment extends GridRecyclerFragment {
    SwipeRefreshLayout swipeContainer;
    ProgressBar progressBar;

    private ArrayList<Displayable> displayableList = new ArrayList<>();
    private static final int MIN_SPAN_SIZE = 1;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(getView());
        UpdatesTabAdapter adapter = new UpdatesTabAdapter(displayableList,getActivity());
        getRecyclerView().setAdapter(adapter);
        refreshUi(displayableList);
        if (swipeContainer != null) {
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            reloadFromDb();
                        }
                    });
                }
            });
        }
        BUCKET_SIZE = AptoideUtils.UI.getEditorChoiceBucketSize();
    }

    @Override
    public void setLayoutManager(RecyclerView recyclerView) {
        super.setLayoutManager(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanCount(AptoideUtils.UI.getEditorChoiceBucketSize());
        }
    }

    @Subscribe
    public void refreshStoresEvent(OttoEvents.RepoAddedEvent event) {
        Logger.d("AptoideUpdates", "RepoAddedEvent event");
        startUpdatesService();
    }

    @Subscribe
    public void refreshStoresEvent(OttoEvents.RepoDeletedEvent event) {
        Logger.d("AptoideUpdates", "RepoAddedEvent event");
        startUpdatesService();
    }

    @Subscribe
    public void refreshStoresEvent(OttoEvents.RepoCompleteEvent event) {
        startUpdatesService();
    }

    @Subscribe
    public void newEvent(OttoEvents.GetUpdatesFinishedEvent event) {
        Logger.d("AptoideUpdates", "GetUpdatesFinishedEvent event");
        refreshUi(displayableList);
    }

    @Subscribe
    public void newEvent(OttoEvents.ExcludedUpdateRemovedEvent event) {
        Logger.d("AptoideUpdates", "ExcludedUpdateRemoved event");
        refreshUi(displayableList);
    }

    private void startUpdatesService() {
        Intent intent = new Intent(getActivity(), UpdatesService.class);
        getActivity().startService(new Intent(intent));
    }

    /**
     * Post a background Runnable that reads the Updates from the DataBase.
     * After it finishes, populates the recycler view on the UiThread.
     *
     * @param list
     */
    private void refreshUi(final ArrayList<Displayable> list) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Logger.i("AptoideUpdates", "(refreshUi) UI Thread = " + AptoideUtils.UI.isUiThread());

                AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
                Cursor cursor = database.getUpdatesTabList();
                final PackageManager pm = Aptoide.getContext().getPackageManager();
                final ArrayList<UpdateRow> updatesToAdd = new ArrayList<>();
                final ArrayList<InstallRow> installsToAdd = new ArrayList<>();

                ArrayList<String> excludedPackages = database.getExcludedApksAsList();

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    UpdateRow row = new UpdateRow(MIN_SPAN_SIZE);

                    int packageName = cursor.getColumnIndex(Schema.Updates.COLUMN_PACKAGE);
                    int fileSize = cursor.getColumnIndex(Schema.Updates.COLUMN_FILESIZE);
                    int alt_path = cursor.getColumnIndex(Schema.Updates.COLUMN_ALT_URL);
                    int path = cursor.getColumnIndex(Schema.Updates.COLUMN_URL);
                    int icon = cursor.getColumnIndex(Schema.Updates.COLUMN_ICON);
                    int verName = cursor.getColumnIndex(Schema.Updates.COLUMN_UPDATE_VERNAME);
                    int md5sum = cursor.getColumnIndex(Schema.Updates.COLUMN_MD5);
                    int repoName = cursor.getColumnIndex(Schema.Updates.COLUMN_REPO);
                    int version_code = cursor.getColumnIndex(Schema.Updates.COLUMN_VERCODE);

                    String path_url = cursor.getString(path);

                    if (path_url != null) {
                        row.packageName = cursor.getString(packageName);
                        try {
                            PackageInfo packageInfo = pm.getPackageInfo(row.packageName, 0);
                            row.versionCode = cursor.getInt(version_code);
                            row.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                            row.icon = cursor.getString(icon);
                            row.versionNameInstalled = packageInfo.versionName;

                            String string = cursor.getString(verName);
                            if (string == null) {
                                row.versionName = packageInfo.versionName;
                            } else {
                                row.versionName = string;
                            }

                            row.md5sum = cursor.getString(md5sum);
                            row.storeName = cursor.getString(repoName);
                            row.path = cursor.getString(path);
                            row.path_alt = cursor.getString(alt_path);
                            row.fileSize = cursor.getLong(fileSize);

                            // If it's an excluded update, don't add it.
                            if (!excludedPackages.contains(row.packageName)) {
                                updatesToAdd.add(row);
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            Logger.printException(e);
                        }

                    } else {

                        try {
                            PackageInfo packageInfo = pm.getPackageInfo(cursor.getString(packageName), 0);
                            ApplicationInfo appInfo = packageInfo.applicationInfo;

                            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                                InstallRow app = new InstallRow(MIN_SPAN_SIZE);

                                app.appName = appInfo.loadLabel(pm).toString();
                                app.packageName = appInfo.packageName;
                                app.versionName = packageInfo.versionName;
                                app.icon = "android.resource://" + cursor.getString(packageName) + "/" + appInfo.icon;
                                app.firstInstallTime = packageInfo.firstInstallTime;

                                installsToAdd.add(app);
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            Logger.printException(e);
                        }
                    }
                }

                Collections.sort(installsToAdd, new Comparator<InstallRow>() {
                    @Override
                    public int compare(InstallRow lhs, InstallRow rhs) {

                        return lhs.firstInstallTime == rhs.firstInstallTime ? 0 :
                                (rhs.firstInstallTime < lhs.firstInstallTime ? -1 : 1);
                    }
                });

                Collections.sort(updatesToAdd, new Comparator<UpdateRow>() {
                    @Override
                    public int compare(UpdateRow lhs, UpdateRow rhs) {
                        return lhs.appName.compareToIgnoreCase(rhs.appName);
                    }
                });

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (isAdded()) {
                            list.clear();

                            if (!updatesToAdd.isEmpty()) {
                                UpdateHeaderRow updatesHeader = new UpdateHeaderRow(getString(R.string.updates_tab), true, BUCKET_SIZE);
                                list.add(updatesHeader);
                                list.addAll(updatesToAdd);
                            }

                            if (!installsToAdd.isEmpty()) {
                                HeaderRow installHeader = new HeaderRow(getString(R.string.installed_tab), false, BUCKET_SIZE);
                                list.add(installHeader);
                                list.addAll(installsToAdd);
                            }

                            if (!list.isEmpty() && progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }

                            getRecyclerView().getAdapter().notifyDataSetChanged();
                        }
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        if (swipeContainer != null) {
                            swipeContainer.setRefreshing(false);
                        }
                    }
                });

            }
        });
    }


    @Subscribe
    public void newAppEvent(OttoEvents.InstalledApkEvent event) {
        Aptoide.getContext().startService(new Intent(Aptoide.getContext(), UpdatesService.class));
        refreshUi(displayableList);
    }

    @Subscribe
    public void onStoreCompleted(OttoEvents.RepoCompleteEvent event) {
        AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
        Aptoide.getContext().startService(new Intent(Aptoide.getContext(), UpdatesService.class));
    }

    @Subscribe
    public void removedAppEvent(OttoEvents.UnInstalledApkEvent event) {
        refreshUi(displayableList);
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }


    /**
     * Clears the Update's Database and starts the Update Service
     */
    @WorkerThread
    private void reloadFromDb() {

        Logger.i("AptoideUpdates", "(reloadFromDb) UI Thread = " + AptoideUtils.UI.isUiThread());
        AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
        database.invalidateUpdates();
        Intent intent = new Intent(getActivity(), UpdatesService.class);
        intent.putExtra(UpdatesService.FORCE_UPDATE, true);
        getActivity().startService(intent);
    }


    /* ************ Registers and unregisters Otto's BusProvider ***********/
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

    protected void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout )view.findViewById(R.id.swipe_container);
        progressBar = (ProgressBar )view.findViewById(R.id.progress_bar);
    }

    public static UpdatesFragment newInstance(){
        return new UpdatesFragment();
    }

}