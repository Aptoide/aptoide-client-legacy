package com.aptoide.amethyst;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.dialogs.MyAppStoreDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.pushnotification.PushNotificationReceiver;
import com.aptoide.amethyst.services.DownloadService;
import com.aptoide.amethyst.services.UpdatesService;
import com.aptoide.amethyst.ui.BadgeView;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Configs;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.ChangeUserSettingsRequest;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.stores.Store;
import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public abstract class BaseMainActivity extends AptoideBaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected ViewPager mViewPager;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    protected PagerSlidingTabStrip tabs;

    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    protected DownloadService downloadService;

    private boolean mAccountBoxExpanded, isLoggedin;

    protected BadgeView badgeUpdates;

    private SharedPreferences sharedPreferences = AptoideUtils.getSharedPreferences();
    protected boolean wizardWasExecuted = sharedPreferences.getBoolean("isFirstRun", false);


    OnAccountsUpdateListener onAccountsUpdateListener = new OnAccountsUpdateListener() {
        @Override
        public void onAccountsUpdated(Account[] accounts) {

//            initDrawerHeader(); // TODO: 19-10-2015 Drawer Navigation logic
            BusProvider.getInstance().post(new OttoEvents.SocialTimelineInitEvent(true));
            BusProvider.getInstance().post(new OttoEvents.RepoCompleteEvent(-1));
//            refrebootsh = true;
        }
    };

    private ServiceConnection downloadServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Logger.d(TAG, "onServiceConnected");
            downloadService = ((DownloadService.LocalBinder) binder).getService();
            BusProvider.getInstance().post(new OttoEvents.DownloadServiceConnected());

            if (getIntent().hasExtra("new_updates") && mViewPager != null) {
                mViewPager.setCurrentItem(3);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected");
        }
    };

    /**
     * Callback to be provided to a Dialog in order to add a store
     */
    MyAppStoreDialog.MyAppsAddStoreInterface appsAddStoreInterface = new MyAppStoreDialog.MyAppsAddStoreInterface() {
        @Override
        public DialogInterface.OnClickListener getOnMyAppAddStoreListener(final String repo) {
            return new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Store store = new Store();
                    store.setBaseUrl(AptoideUtils.RepoUtils.formatRepoUri(repo));
                    store.setName(AptoideUtils.RepoUtils.split(repo));
                    AptoideUtils.RepoUtils.startParse(store.getName(), BaseMainActivity.this, spiceManager);
                }
            };
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        openDrawerOnBoot(wizardWasExecuted);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AptoideUtils.AppUtils.checkPermissions(this);
        BusProvider.getInstance().register(this);

        bindViews();
        //spiceManager.start(this);
        //AccountManager.get(this).addOnAccountsUpdatedListener(onAccountsUpdateListener, new Handler(Looper.getMainLooper()), false);

        //handleIntentExtras(getIntent());

        startService(new Intent(this, UpdatesService.class));
        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, BIND_AUTO_CREATE);

        if (AptoideUtils.getSharedPreferences().getBoolean("checkautoupdate", true)) {
            new AutoUpdate(this).execute();
        }

        Analytics.Dimenstions.setPartnerDimension(Aptoide.getConfiguration().getStoreType());
        Analytics.Dimenstions.setVerticalDimension(Aptoide.getConfiguration().getVertical());
        Analytics.Dimenstions.setGmsPresent(AptoideUtils.GoogleServices.checkGooglePlayServices(this));
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Temporary workaround from memory issues
        AptoideUtils.UI.unbindDrawables(mDrawerLayout);

        BusProvider.getInstance().unregister(this);

        if (downloadService != null) {
            unbindService(downloadServiceConnection);
        }

        AccountManager.get(this).removeOnAccountsUpdatedListener(onAccountsUpdateListener);
//        if(isFinishing()) stopService(new Intent(this, RabbitMqService.class));
    }

    @Override
    protected String getScreenName() {
        return null;
    }

    protected void handleIntentExtras(Intent intent) {
        if (intent.hasExtra(Constants.NEW_REPO_EXTRA) && intent.getFlags() == Constants.NEW_REPO_FLAG) {
            ArrayList<String> repos = intent.getExtras().getStringArrayList("newrepo");
            if (repos != null) {

                for (final String repoUrl : repos) {

                    if (new AptoideDatabase(Aptoide.getDb()).existsRepo(AptoideUtils.RepoUtils.formatRepoUri(repoUrl))) {
                        Toast.makeText(this, getString(R.string.store_already_added), Toast.LENGTH_LONG).show();
                    } else if (intent.getBooleanExtra("nodialog", false)) {
                        Store store = new Store();
                        store.setBaseUrl(AptoideUtils.RepoUtils.formatRepoUri(repoUrl));
                        store.setName(AptoideUtils.RepoUtils.split(repoUrl));
                        AptoideUtils.RepoUtils.startParse(store.getName(), this, spiceManager);
                        mViewPager.setCurrentItem(2);
//                        FlurryAgent.logEvent("Added_Store_From_My_App_Installation");
                    } else {
                        AptoideDialog.addMyAppStore(repoUrl, appsAddStoreInterface).show(getSupportFragmentManager(), "addStoreMyApp");
                        mViewPager.setCurrentItem(2);
                    }

                }
                intent.removeExtra(Constants.NEW_REPO_EXTRA);
            }
        } else if (intent.hasExtra("fromDownloadNotification")) {
            Analytics.ApplicationLaunch.downloadingUpdates();
            mViewPager.setCurrentItem(5);
        } else if (intent.hasExtra("fromTimeline")) {
            Analytics.ApplicationLaunch.timelineNotification();
            mViewPager.setCurrentItem(4);
        } else if (intent.hasExtra("new_updates")) {
            Analytics.ApplicationLaunch.newUpdatesNotification();
            mViewPager.setCurrentItem(3);
        } else {
            Analytics.ApplicationLaunch.launcher();

        }
    }


    public DownloadService getDownloadService() {
        return downloadService;
    }

    @Subscribe
    public void newEvent(OttoEvents.GetUpdatesFinishedEvent event) {
        Logger.d("AptoideUpdates", "GetUpdatesFinishedEvent event");
        updateBadge(badgeUpdates, event.numUpdates);
    }

    @Subscribe
    public void installFromManager(OttoEvents.InstallAppFromManager event) {
        downloadService.startExistingDownload(event.getId());
    }

    @Subscribe
    public void installAppFromUpdateRow(OttoEvents.StartDownload event) {
        if (event != null && event.getRow() != null) {
            downloadService.installAppFromUpdateRow(event.getRow());
        } else {
            Logger.i(this, "OttoEvents.StartDownload event was null");
        }
    }

    @Subscribe
    public void matureLock(OttoEvents.MatureEvent event) {
        if (event == null) {
            Logger.i(this, "OttoEvents.StartDownload event was null");
        } else {
            matureLock(event.isMature());
        }
    }

    public void matureLock(boolean mature) {
        setMatureSwitchSetting(mature);
        PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putBoolean(Constants.MATURE_CHECK_BOX, mature).apply();
//        FlurryAgent.logEvent("Unlocked_Mature_Content");
//        Analytics.AdultContent.unlock();
        BusProvider.getInstance().post(new OttoEvents.RepoCompleteEvent(-1));
    }

    public void setMatureSwitchSetting(boolean value) {
        if (isLoggedin) {
            ChangeUserSettingsRequest request = new ChangeUserSettingsRequest();
            request.changeMatureSwitchSetting(value);
            spiceManager.execute(request, new RequestListener<GenericResponseV2>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                }

                @Override
                public void onRequestSuccess(GenericResponseV2 genericResponseV2) {
                }
            });
        }
    }

    abstract protected void setupBadge(int position);


    protected void updateBadge(BadgeView badgeUpdates, int num) {

        if(badgeUpdates != null) {
            badgeUpdates.setTextSize(11);

            if (num > 0) {
                badgeUpdates.setText(String.valueOf(num));
                if (!badgeUpdates.isShown()) badgeUpdates.show(true);
            } else {
                if (badgeUpdates.isShown()) badgeUpdates.hide(true);
            }
        }
    }

    protected void openDrawerOnBoot(boolean isFirstRun) {
        if (isFirstRun) {
            wizardWasExecuted = false;
            final int OPEN_DELAY = 200;
            AptoideUtils.Concurrency.post(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        // Fix for AN-359: if the openDrawer line crashes, it wont execute the runnable.
                        mDrawerLayout.openDrawer(GravityCompat.START);
                        AptoideUtils.Concurrency.post(BaseMainActivity.this, new Runnable() {
                            @Override
                            public void run() {
                                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                            }
                        }, OPEN_DELAY + 2000);
                    } catch (Exception e) {
                        Logger.printException(e);
                    }
                }
            }, OPEN_DELAY);
        }
    }

    abstract protected void bindViews();

    protected abstract void navigationDrawerIntentBuild();

    abstract protected void createShortCut(int icon);

    /**
     * Set the alarms for the PushNotifications
     */
    public void startPushNotifications() {
        Intent i = new Intent(this, Aptoide.getConfiguration().getPushNotificationsReceiver());
        i.setAction(Aptoide.getConfiguration().getAction());

        PushNotificationReceiver.setPendingIntents(this);
    }

}
