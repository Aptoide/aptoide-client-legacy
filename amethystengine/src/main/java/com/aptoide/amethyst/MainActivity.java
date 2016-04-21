package com.aptoide.amethyst;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.dialogs.MyAppStoreDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.model.json.OAuth;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.tutorial.TutorialActivity;
import com.aptoide.amethyst.ui.MyAccountActivity;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Base64;
import com.aptoide.amethyst.utils.Configs;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.ChangeUserSettingsRequest;
import com.aptoide.amethyst.webservices.OAuth2AuthenticationRequest;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.amethyst.webservices.v2.AlmostGenericResponseV2RequestListener;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.stores.Store;
import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

import com.aptoide.amethyst.adapter.MainPagerAdapter;
import com.aptoide.amethyst.callbacks.AddCommentVoteCallback;
import com.aptoide.amethyst.pushnotification.PushNotificationReceiver;
import com.aptoide.amethyst.services.DownloadService;
import com.aptoide.amethyst.services.UpdatesService;
import com.aptoide.amethyst.ui.BadgeView;
import com.aptoide.amethyst.ui.ExcludedUpdatesActivity;
import com.aptoide.amethyst.ui.RollbackActivity;
import com.aptoide.amethyst.ui.ScheduledDownloadsActivity;
import com.aptoide.amethyst.ui.SearchManager;
import com.aptoide.amethyst.ui.SettingsActivity;
import com.aptoide.amethyst.ui.dialogs.AddStoreDialog;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.utils.InstalledAppsHelper;

public class MainActivity extends AptoideBaseActivity implements AddCommentVoteCallback {

    public static final String TAG = MainActivity.class.getSimpleName();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    NavigationView mNavigationView;
    PagerSlidingTabStrip tabs;

    private BadgeView badgeUpdates;

    /* Usado para guardar o estado da seleccao dos items */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final int WIZARD_REQ_CODE = 50;
    private int mCurrentSelectedPosition;
    private boolean mAccountBoxExpanded, isLoggedin;

    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    private DownloadService downloadService;
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
    private boolean wizardWasExecuted = false;

    private SharedPreferences sharedPreferences = AptoideUtils.getSharedPreferences();

    @Override
    protected void onResume() {
        super.onResume();
        setupUserInfoNavigationDrawer();

        if (wizardWasExecuted) {
            wizardWasExecuted = false;
            final int OPEN_DELAY = 200;
            AptoideUtils.Concurrency.post(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        // Fix for AN-359: if the openDrawer line crashes, it wont execute the runnable.
                        mDrawerLayout.openDrawer(GravityCompat.START);
                        AptoideUtils.Concurrency.post(MainActivity.this, new Runnable() {
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected String getScreenName() {
        return null;
    }

    private void setupUserInfoNavigationDrawer() {
        // https://code.google.com/p/android/issues/detail?id=190226
        View header = mNavigationView.getHeaderView(0);

        ((TextView) header.findViewById(R.id.profile_email_text)).setText(AptoideUtils.getSharedPreferences().getString(Configs.LOGIN_USER_LOGIN, ""));
        ((TextView) header.findViewById(R.id.profile_name_text)).setText(AptoideUtils.getSharedPreferences().getString(Constants.USER_NAME, ""));

        ImageView profileImage = (ImageView) header.findViewById(R.id.profile_image);
        String userProfilePicPath = AptoideUtils.getSharedPreferences().getString(Constants.USER_AVATAR, "");
        if (AptoideUtils.AccountUtils.isLoggedIn(MainActivity.this)) {
            Glide.with(this).load(userProfilePicPath).transform(new CircleTransform(this)).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.user_account_white);
        }

        MenuItem item = mNavigationView.getMenu().getItem(0);
        if (AptoideUtils.AccountUtils.isLoggedIn(MainActivity.this)) {
            item.setTitle(R.string.my_account);
            item.setIcon(R.drawable.ic_action_accounts);
        } else {
            item.setIcon(R.drawable.user_account_grey);
            item.setTitle(R.string.navigation_drawer_signup_login);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        bindViews();

        if(!checkIfInstalled()) {
            createShortCut();
        }

        setUpToolbar();
        setupPager();
        setupBadge();
        setupUserInfoNavigationDrawer();

        BusProvider.getInstance().register(this);
        spiceManager.start(this);

        if (savedInstanceState == null) {

            if (getIntent().hasExtra(Constants.NEW_REPO_EXTRA) && getIntent().getFlags() == Constants.NEW_REPO_FLAG) {
                ArrayList<String> repos = getIntent().getExtras().getStringArrayList("newrepo");
                if (repos != null) {

                    for (final String repoUrl : repos) {

                        if (new AptoideDatabase(Aptoide.getDb()).existsRepo(AptoideUtils.RepoUtils.formatRepoUri(repoUrl))) {
                            Toast.makeText(this, getString(R.string.store_already_added), Toast.LENGTH_LONG).show();
                        } else if (getIntent().getBooleanExtra("nodialog", false)) {
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
                    getIntent().removeExtra(Constants.NEW_REPO_EXTRA);
                }
            } else if (getIntent().hasExtra("fromDownloadNotification")) {
                Analytics.ApplicationLaunch.downloadingUpdates();
                mViewPager.setCurrentItem(5);
            } else if (getIntent().hasExtra("fromTimeline")) {
                Analytics.ApplicationLaunch.timelineNotification();
                mViewPager.setCurrentItem(4);
            } else if (getIntent().hasExtra("new_updates")) {
                Analytics.ApplicationLaunch.newUpdatesNotification();
                mViewPager.setCurrentItem(3);
            } else {
                Analytics.ApplicationLaunch.launcher();

            }

            syncInstalledApps();
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!sPref.getBoolean("firstrun", true)) {
                AptoideUtils.AppUtils.checkPermissions(this);
            }
            executeWizard();
            startPushNotifications();
        } else {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        if (!hasBootOptions()) {
            SharedPreferences.Editor edit = AptoideUtils.getSharedPreferences().edit();
            edit.remove(AptoideConfiguration.PREF_PATH_CACHE_APK).apply();
        }
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                mDrawerLayout.closeDrawers();

                menuItem.setChecked(true);
                int i = menuItem.getItemId();
                if (i == R.id.navigation_item_my_account) {
                    mCurrentSelectedPosition = 0;
                    Intent loginIntent = new Intent(MainActivity.this, MyAccountActivity.class);
                    startActivity(loginIntent);
                    Logger.d("Debug", "Opening MyAccountActivity");
                    return true;
                } else if (i == R.id.navigation_item_rollback) {
                    Intent rollbackIntent = new Intent(MainActivity.this, RollbackActivity.class);
                    startActivity(rollbackIntent);
                    mCurrentSelectedPosition = 1;
                    return true;
                } else if (i == R.id.navigation_item_setting_schdwntitle) {
                    Intent scheduledIntent = new Intent(MainActivity.this, ScheduledDownloadsActivity.class);
                    startActivity(scheduledIntent);
                    mCurrentSelectedPosition = 2;
                    return true;
                } else if (i == R.id.navigation_item_excluded_updates) {
                    mCurrentSelectedPosition = 3;
                    Intent excludedIntent = new Intent(MainActivity.this, ExcludedUpdatesActivity.class);
                    startActivity(excludedIntent);
                    return true;
                } else if (i == R.id.navigation_item_settings) {
                    Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingIntent);
                    mCurrentSelectedPosition = 4;
                    return true;
                } else if (i == R.id.navigation_item_facebook) {
                    AptoideUtils.SocialMedia.showFacebook(MainActivity.this);
                    mCurrentSelectedPosition = 5;
                    return true;
                } else if (i == R.id.navigation_item_twitter) {
                    AptoideUtils.SocialMedia.showTwitter(MainActivity.this);
                    mCurrentSelectedPosition = 6;
                    return true;
                } else if (i == R.id.navigation_item_backup_apps) {
                    showBackupApps();
                    Analytics.BackupApps.open();
                    return true;
                } else if (i == R.id.send_feedback) {
                    FeedBackActivity.screenshot(MainActivity.this);
                    startActivity(new Intent(MainActivity.this, FeedBackActivity.class));

                    return true;
                } else {
                    return true;
                }
            }
        });

        AccountManager.get(this).addOnAccountsUpdatedListener(onAccountsUpdateListener, new Handler(Looper.getMainLooper()), false);

        startService(new Intent(this, UpdatesService.class));
        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, BIND_AUTO_CREATE);

        if (AptoideUtils.getSharedPreferences().getBoolean("checkautoupdate", true)) {
            new AutoUpdate(this).execute();
        }

        Analytics.Dimenstions.setPartnerDimension(getPartnerName());
        Analytics.Dimenstions.setVerticalDimension(getVertical());
        Analytics.Dimenstions.setGmsPresent(AptoideUtils.GoogleServices.checkGooglePlayServices(this));
    }

    protected int getContentView() {
        return R.layout.activity_main;
    }

    protected void bindViews() {
        mViewPager = (ViewPager )findViewById(R.id.pager);
        mDrawerLayout = (DrawerLayout )findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar )findViewById(R.id.toolbar);
        mNavigationView = (NavigationView )findViewById(R.id.nav_view);
        tabs = (PagerSlidingTabStrip )findViewById(R.id.tabs);
    }

    private boolean hasBootOptions() {
        String appId = null;
        try {
            final String sourceDir = getPackageManager().getPackageInfo("cm.aptoide.pt", 0).applicationInfo.sourceDir;
            final ZipFile myZipFile = new ZipFile(sourceDir);
            final InputStream is = myZipFile.getInputStream(myZipFile.getEntry("META-INF/aob"));

            Properties properties = new Properties();
            properties.load(is);
            Intent intent = null;
            if (properties.containsKey("downloadId")) {
                intent = new Intent(this, AppViewActivity.class);

                appId = properties.getProperty("downloadId");
                long savedId = sharedPreferences.getLong("downloadId", 0);

                if (Long.valueOf(appId) != savedId) {
                    sharedPreferences.edit().putLong("downloadId", Long.valueOf(appId)).apply();

                    intent.putExtra("fromApkInstaller", true);
                    intent.putExtra(Constants.FROM_APKFY_KEY, true);
                    intent.putExtra(Constants.APP_ID_KEY, Long.valueOf(appId));


                    if (properties.containsKey("cpi_url")) {

                        String cpi = properties.getProperty("cpi_url");

                        try {
                            cpi = URLDecoder.decode(cpi, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        intent.putExtra(Constants.CPI_KEY, cpi);
                    }

                    FlurryAgent.logEvent("Started_From_Apkfy");
                    startActivityForResult(intent, WIZARD_REQ_CODE);

                    return true;
                }

            } else if (properties.containsKey("aptword")) {
                String param = properties.getProperty("aptword");

                if (!TextUtils.isEmpty(param)) {

                    param = param.replaceAll("\\*", "_").replaceAll("\\+", "/");

                    String json = new String(Base64.decode(param.getBytes(), 0));

                    Log.d("AptoideAptWord", json);

                    ObjectMapper mapper = new ObjectMapper();

                    ApkSuggestionJson.Ads ad = mapper.readValue(json, ApkSuggestionJson.Ads.class);

                    intent = new Intent(this, AppViewActivity.class);
                    long id = ad.getData().getId().longValue();
                    long adId = ad.getInfo().getAd_id();
                    intent.putExtra(Constants.APP_ID_KEY, id);
                    intent.putExtra(Constants.AD_ID_KEY, adId);
                    intent.putExtra(Constants.PACKAGENAME_KEY, ad.getData().getPackageName());
                    intent.putExtra(Constants.STORENAME_KEY, ad.getData().getRepo());
                    intent.putExtra(Constants.FROM_SPONSORED_KEY, true);
                    intent.putExtra(Constants.LOCATION_KEY, Constants.HOMEPAGE_KEY);
                    intent.putExtra(Constants.KEYWORD_KEY, "__NULL__");
                    intent.putExtra(Constants.CPC_KEY, ad.getInfo().getCpc_url());
                    intent.putExtra(Constants.CPI_KEY, ad.getInfo().getCpi_url());
                    intent.putExtra(Constants.WHERE_FROM_KEY, Constants.FROM_SPONSORED_KEY);
                    intent.putExtra(Constants.DOWNLOAD_FROM_KEY, Constants.FROM_SPONSORED_KEY);

                    if (ad.getPartner() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.PARTNER_TYPE_KEY, ad.getPartner().getPartnerInfo().getName());
                        bundle.putString(Constants.PARTNER_CLICK_URL_KEY, ad.getPartner().getPartnerData().getClick_url());
                        intent.putExtra(Constants.PARTNER_EXTRA, bundle);
                    }
                    startActivityForResult(intent, WIZARD_REQ_CODE);

                }
                return true;
            }


        } catch (Exception e) {
            if (appId != null) {
                Crashlytics.setString("APKFY_APP_ID", appId);
            }
            Logger.d(TAG, e.getMessage());
            Crashlytics.logException(e);
        }
        return false;
    }

    private void syncInstalledApps() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Aptoide.getConfiguration().resetPathCacheApks();
                InstalledAppsHelper.syncInstalledApps(MainActivity.this);
            }
        });

    }

    private void showBackupApps() {
        Intent intent;
        if (AptoideUtils.AppUtils.isAppInstalled(this, Defaults.BACKUP_APPS_PACKAGE)) {
            intent = getPackageManager().getLaunchIntentForPackage(Defaults.BACKUP_APPS_PACKAGE);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(MainActivity.this, AppViewActivity.class);
            intent.putExtra(Constants.GET_BACKUP_APPS_KEY, true);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case WIZARD_REQ_CODE:
                if (resultCode == RESULT_OK && data.getBooleanExtra("addDefaultRepo", false)) {
                    AptoideUtils.RepoUtils.addDefaultAppsStore(this);
                    wizardWasExecuted = true;
                    //Logger.d("Start-addDefaultRepo", "added default repo "+ repoUrl);
                }
                break;
        }

    }

    public void executeWizard() {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sPref.getBoolean("firstrun", true)) {

            Intent newToAptoideTutorial = new Intent(this, TutorialActivity.class);
            startActivityForResult(newToAptoideTutorial, WIZARD_REQ_CODE);
            sPref.edit().putBoolean("firstrun", false).apply();
            try {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("version", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode).apply();
            } catch (PackageManager.NameNotFoundException e) {
                Logger.printException(e);
            }

        } else {

            try {

                if (Aptoide.isUpdate()) {

                    int previousVersion = PreferenceManager.getDefaultSharedPreferences(this).getInt("version", 0);

                    if (previousVersion < 467) {

                        Intent whatsNewTutorial = new Intent(this, TutorialActivity.class);
                        whatsNewTutorial.putExtra("isUpdate", true);
                        startActivityForResult(whatsNewTutorial, WIZARD_REQ_CODE);

                    }

                    if (previousVersion > 430 && previousVersion < 438) {
                        updateAccount();
                    }

                    PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("version", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode).apply();

                }
            } catch (PackageManager.NameNotFoundException e) {
                Logger.printException(e);
            }

        }
    }

    private void updateAccount() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final AccountManager manager = AccountManager.get(this);
        final Account[] accountsByType = manager.getAccountsByType(Aptoide.getConfiguration().getAccountType());

        if (accountsByType.length > 0 || "APTOIDE".equals(sharedPreferences.getString("loginType", null))) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    OAuth2AuthenticationRequest oAuth2AuthenticationRequest = new OAuth2AuthenticationRequest();
                    oAuth2AuthenticationRequest.setMode(LoginActivity.Mode.APTOIDE);
                    oAuth2AuthenticationRequest.setUsername(accountsByType[0].name);
                    oAuth2AuthenticationRequest.setPassword(manager.getPassword(accountsByType[0]));

                    try {
                        //oAuth2AuthenticationRequest.setHttpRequestFactory(AndroidHttp.newCompatibleTransport().createRequestFactory());

                        OAuth oAuth = oAuth2AuthenticationRequest.loadDataFromNetwork();

                        String refreshToken = oAuth.getRefreshToken();

                        String actualToken = manager.blockingGetAuthToken(accountsByType[0], AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false);
                        manager.invalidateAuthToken(Aptoide.getConfiguration().getAccountType(), actualToken);
                        manager.setAuthToken(accountsByType[0], AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, refreshToken);
                        SecurePreferences.getInstance().edit().putString("access_token", oAuth.getAccess_token()).apply();
                    } catch (Exception e) {
                        Logger.printException(e);
                        AccountManager.get(MainActivity.this).removeAccount(accountsByType[0], null, null);
                    }

                }
            }).start();


        } else if (accountsByType.length > 0) {
            AccountManager.get(this).removeAccount(accountsByType[0], null, null);
        }

    }

    private void setupPager() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(mViewPager);
        tabs.setOnPageChangeListener(getViewPagerChangeListener());
    }

    /**
     * Needs Pager to be setup first
     */
    public void setupBadge() {
        badgeUpdates = new BadgeView(this, ((LinearLayout) tabs.getChildAt(0)).getChildAt(3));
        Cursor data = new AptoideDatabase(Aptoide.getDb()).getUpdates();
        int size = data.getCount();
        data.close();
        updateBadge(size);
    }

    public void updateBadge(int num) {
        badgeUpdates.setTextSize(11);

        if (num > 0) {
            badgeUpdates.setText(String.valueOf(num));
            if (!badgeUpdates.isShown()) badgeUpdates.show(true);
        } else {
            if (badgeUpdates.isShown()) badgeUpdates.hide(true);
        }
    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setLogo(R.drawable.ic_aptoide_toolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    public void showAddStoreDialog(View v) {
        DialogFragment dialog = new AddStoreDialog();
        dialog.show(getSupportFragmentManager(), "addStoreDialog");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager.setupSearch(menu, this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the alarms for the PushNotifications
     */
    public void startPushNotifications() {
        Intent i = new Intent(this, PushNotificationReceiver.class);
        i.setAction(Aptoide.getConfiguration().getAction());

        PushNotificationReceiver.setPendingIntents(this);
    }

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
                    AptoideUtils.RepoUtils.startParse(store.getName(), MainActivity.this, spiceManager);
                }
            };
        }
    };

    public DownloadService getDownloadService() {
        return downloadService;
    }

    @Subscribe
    public void newEvent(OttoEvents.GetUpdatesFinishedEvent event) {
        Logger.d("AptoideUpdates", "GetUpdatesFinishedEvent event");
        updateBadge(event.numUpdates);
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

    @Subscribe
    public void newEvent(OttoEvents.RedrawNavigationDrawer event) {
        setupUserInfoNavigationDrawer();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Temporary workaround from memory issues
        AptoideUtils.UI.unbindDrawables(findViewById(R.id.drawer_layout));

        BusProvider.getInstance().unregister(this);
        spiceManager.shouldStop();

        if (downloadService != null) {
            unbindService(downloadServiceConnection);
        }

        AccountManager.get(this).removeOnAccountsUpdatedListener(onAccountsUpdateListener);
//        if(isFinishing()) stopService(new Intent(this, RabbitMqService.class));
    }

    OnAccountsUpdateListener onAccountsUpdateListener = new OnAccountsUpdateListener() {
        @Override
        public void onAccountsUpdated(Account[] accounts) {

//            initDrawerHeader(); // TODO: 19-10-2015 Drawer Navigation logic
            BusProvider.getInstance().post(new OttoEvents.SocialTimelineInitEvent(true));
            BusProvider.getInstance().post(new OttoEvents.RepoCompleteEvent(-1));
//            refresh = true;
        }
    };

    @Override
    public void voteComment(int commentId, AddApkCommentVoteRequest.CommentVote vote) {
        RequestListener<GenericResponseV2> commentRequestListener = new AlmostGenericResponseV2RequestListener() {
            @Override
            public void CaseOK() {
                Toast.makeText(MainActivity.this, getString(R.string.vote_submitted), Toast.LENGTH_LONG).show();
            }
        };

        AptoideUtils.VoteUtils.voteComment(
                spiceManager,
                commentId,
                Defaults.DEFAULT_STORE_NAME,
                SecurePreferences.getInstance().getString("token", "empty"),
                commentRequestListener,
                vote);
    }

    private ViewPager.OnPageChangeListener getViewPagerChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        Analytics.Screens.tagScreen("List Apps (Start)");
                        break;
                    case 1:
                        Analytics.Screens.tagScreen("Community");
                        break;
                    case 2:
                        Analytics.Screens.tagScreen("Stores");
                        break;
                    case 3:
                        Analytics.Screens.tagScreen("Updates");
                        break;
                    case 4:
                        Analytics.Screens.tagScreen("Social Timeline");
                        break;
                    case 5:
                        Analytics.Screens.tagScreen("Downloads");
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    public String getPartnerName() {
        return "vanilla";
    }

    public String getVertical() {
        return Analytics.Dimenstions.Vertical.SMARTPHONE;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("new_updates")) {
            Analytics.ApplicationLaunch.newUpdatesNotification();
        } else if (intent.hasExtra("fromDownloadNotification")) {
            Analytics.ApplicationLaunch.downloadingUpdates();
        } else if (intent.hasExtra("fromTimeline")) {
            Analytics.ApplicationLaunch.timelineNotification();
        } else {
            Analytics.ApplicationLaunch.launcher();
        }

    }

    public void createShortCut() {
        Intent shortcutIntent = new Intent(this, MainActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Aptoide");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(intent);
        sharedPreferences.edit().putBoolean("isinstalled", true).apply();
    }


    private boolean checkIfInstalled() {
        return sharedPreferences.getBoolean("isinstalled", false);
    }

}