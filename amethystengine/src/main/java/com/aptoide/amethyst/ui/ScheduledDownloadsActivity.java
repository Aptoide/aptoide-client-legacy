/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.database.SimpleCursorLoader;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.models.ScheduledDownloadItem;
import com.bumptech.glide.Glide;
import com.octo.android.robospice.SpiceManager;

import java.util.HashMap;


import com.aptoide.amethyst.dialogs.ScheduledDownloadsDialog;
import com.aptoide.amethyst.services.DownloadService;


public class ScheduledDownloadsActivity extends AptoideBaseActivity implements LoaderCallbacks<Cursor>, ScheduledDownloadsDialog.DialogCallback {

    public static final String ARG_DOWNLOAD_ALL = "downloadAll";

    public static Intent newIntent(@NonNull final Context context, final boolean dowloadAll) {
        final Intent intent = new Intent(context, ScheduledDownloadsActivity.class);
        intent.putExtra(ARG_DOWNLOAD_ALL, dowloadAll);
        return intent;
    }


    Toolbar mToolbar;

    private ListView lv;
    private AptoideDatabase db;
    private CursorAdapter adapter;
    private HashMap<Long, ScheduledDownload> scheduledDownloadsMap = new HashMap<Long, ScheduledDownload>();
    private DownloadService downloadService;
    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            downloadService = ((DownloadService.LocalBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private int i;
    private boolean showDownloadAll;

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
//        FlurryAgent.onStartSession(this, getResources().getString(R.string.FLURRY_KEY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
//        FlurryAgent.onEndSession(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        mToolbar.setCollapsible(false);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.setting_schdwntitle));

        lv = (ListView) findViewById(android.R.id.list);
        lv.setDivider(null);
        db = new AptoideDatabase(Aptoide.getDb());
        bindService(new Intent(this, DownloadService.class), conn, Context.BIND_AUTO_CREATE);


        adapter = new CursorAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {

            @Override
            public View newView(Context context, Cursor arg1, ViewGroup arg2) {
                return LayoutInflater.from(context).inflate(R.layout.row_sch_download, null);
            }

            @Override
            public void bindView(View convertView, Context arg1, Cursor c) {
                ScheduledDownload scheduledDownload = scheduledDownloadsMap.get(c.getLong(c.getColumnIndex("_id")));

                // The child views in each row.
                CheckBox checkBoxScheduled;
                TextView textViewName;
                TextView textViewVersion;
                ImageView imageViewIcon;

                if (convertView.getTag() == null) {
                    textViewName = (TextView) convertView.findViewById(R.id.name);
                    textViewVersion = (TextView) convertView.findViewById(R.id.appversion);
                    checkBoxScheduled = (CheckBox) convertView.findViewById(R.id.schDwnChkBox);
                    imageViewIcon = (ImageView) convertView.findViewById(R.id.appicon);
                    convertView.setTag(new Holder(textViewName, textViewVersion, checkBoxScheduled, imageViewIcon));

                    checkBoxScheduled.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CheckBox cb = (CheckBox) v;
                            ScheduledDownload schDownload = (ScheduledDownload) cb.getTag();
                            schDownload.setChecked(cb.isChecked());
                        }
                    });
                }
                // Reuse existing row view
                else {
                    // Because we use a ViewHolder, we avoid having to call findViewById().
                    Holder viewHolder = (Holder) convertView.getTag();
                    checkBoxScheduled = viewHolder.checkBoxScheduled;
                    textViewVersion = viewHolder.textViewVersion;
                    textViewName = viewHolder.textViewName;
                    imageViewIcon = viewHolder.imageViewIcon;
                }


                // Tag the CheckBox with the Planet it is displaying, so that we can
                // access the planet in onClick() when the CheckBox is toggled.
                checkBoxScheduled.setTag(scheduledDownload);

                // Display planet data
                checkBoxScheduled.setChecked(scheduledDownload.isChecked());
                textViewName.setText(scheduledDownload.getName());
                textViewVersion.setText(scheduledDownload.getVersion_name());

                Glide.with(ScheduledDownloadsActivity.this).load(scheduledDownload.getIcon()).into(imageViewIcon);
            }
        };


        getSupportLoaderManager().initLoader(0, null, this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View item, int arg2, long arg3) {
                ScheduledDownload scheduledDownload = (ScheduledDownload) ((Holder) item.getTag()).checkBoxScheduled.getTag();
                scheduledDownload.toggleChecked();
                Holder viewHolder = (Holder) item.getTag();
                viewHolder.checkBoxScheduled.setChecked(scheduledDownload.isChecked());
            }

        });

        if(getIntent().hasExtra(ARG_DOWNLOAD_ALL)) {
            ScheduledDownloadsDialog pd = new ScheduledDownloadsDialog();
            pd.show(getSupportFragmentManager(), "installAllScheduled");
        }


        lv.setAdapter(adapter);
    }

    protected int getContentView() {
        return R.layout.page_sch_downloads;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SimpleCursorLoader(this) {

            @Override
            public Cursor loadInBackground() {
                return db.getScheduledDownloads();
            }

        };
    }




    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor c) {
        scheduledDownloadsMap.clear();
        if (c.getCount() == 0) {
            findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ScheduledDownload scheduledDownload = new ScheduledDownload(c.getLong(c.getColumnIndex("_id")), true);

            scheduledDownload.setPackage_name(c.getString(c.getColumnIndex("package_name")));
            scheduledDownload.setMd5(c.getString(c.getColumnIndex("md5")));
            scheduledDownload.setName(c.getString(c.getColumnIndex("name")));
            scheduledDownload.setVersion_name(c.getString(c.getColumnIndex("version_name")));
            scheduledDownload.setRepo_name(c.getString(c.getColumnIndex("repo_name")));
            scheduledDownload.setIcon(c.getString(c.getColumnIndex("icon")));

            scheduledDownloadsMap.put(c.getLong(c.getColumnIndex("_id")), scheduledDownload);
        }
        adapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scheduled_downloads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();


        if (i == android.R.id.home || i == R.id.home) {
            finish();
        } else if (i == R.id.menu_install) {

            Analytics.ScheduledDownloads.clickOnInstallSelected();

            if (isAllChecked()) {
                for (ScheduledDownload scheduledDownload : scheduledDownloadsMap.values()) {
                    if (scheduledDownload.isChecked()) {
                        downloadService.startScheduledDownload(scheduledDownload);
                    }
                }
            } else {
                Toast toast = Toast.makeText(Aptoide.getContext(), R.string.schDown_nodownloadselect, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (i == R.id.menu_remove) {
            Analytics.ScheduledDownloads.clickOnRemoveSelected();
            Log.d("ScheduledDownloadsActivity-onOptionsItemSelected", "remove");
            if (isAllChecked()) {
                for (ScheduledDownload scheduledDownload : scheduledDownloadsMap.values()) {
                    if (scheduledDownload.isChecked()) {
                        db.deleteScheduledDownload(scheduledDownload.getMd5());
//                        FlurryAgent.logEvent("Scheduled_Downloads_Removed_Apps");
                    }
                }
                getSupportLoaderManager().restartLoader(0, null, this);
            }else{
                Toast toast = Toast.makeText(Aptoide.getContext(), R.string.schDown_nodownloadselect, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (i == R.id.menu_invert) {
            Analytics.ScheduledDownloads.clickOnInvertSelection();
//            FlurryAgent.logEvent("Scheduled_Downloads_Inverted_Apps");
            for (ScheduledDownload scheduledDownload : scheduledDownloadsMap.values()) {
                scheduledDownload.toggleChecked();
            }
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

//    @Override
//    protected String getScreenName() {
//        return "Scheduled Downloads";
//    }

    public boolean isAllChecked() {
        if (scheduledDownloadsMap.isEmpty()) {
            return false;
        }
        for (Long scheduledDownload : scheduledDownloadsMap.keySet()) {
            if (scheduledDownloadsMap.get(scheduledDownload).checked) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onOkClick() {
        AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");
        for (ScheduledDownload scheduledDownload : scheduledDownloadsMap.values()) {
            downloadService.startScheduledDownload(scheduledDownload);
        }
        finish();
    }

    @Override
    public void onCancelClick() {
       finish();
    }

    private static class ScheduledDownload extends ScheduledDownloadItem {
        private boolean checked = false;
        private long id;


        public ScheduledDownload(long id, boolean checked) {
            this.id = id;
            this.checked = checked;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void toggleChecked() {
            checked = !checked;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    private static class Holder {
        public CheckBox checkBoxScheduled;
        public TextView textViewName;
        public TextView textViewVersion;
        public ImageView imageViewIcon;

        public Holder(TextView textView, TextView textViewVersion, CheckBox checkBox, ImageView imageView) {
            this.checkBoxScheduled = checkBox;
            this.textViewName = textView;
            this.textViewVersion = textViewVersion;
            this.imageViewIcon = imageView;
        }
    }

    @Override
    protected String getScreenName() {
        return "Scheduled Downloads";
    }

}
