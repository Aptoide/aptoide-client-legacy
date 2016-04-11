/*******************************************************************************
 * Copyright (c) 2015 Aptoide.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.dialogs.DialogPermissions;
import com.aptoide.amethyst.dialogs.FlagApkDialog;
import com.aptoide.amethyst.dialogs.MyAppStoreDialog;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.model.json.CheckUserCredentialsJson;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.ui.IMediaObject;
import com.aptoide.amethyst.ui.MyAccountActivity;
import com.aptoide.amethyst.ui.Screenshot;
import com.aptoide.amethyst.ui.Video;
import com.aptoide.amethyst.ui.callbacks.AddCommentCallback;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.AddApkFlagRequest;
import com.aptoide.amethyst.webservices.CheckUserCredentialsRequest;
import com.aptoide.amethyst.webservices.json.GetApkInfoJson;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.amethyst.webservices.v2.AddCommentRequest;
import com.aptoide.amethyst.webservices.v2.AlmostGenericResponseV2RequestListener;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.AllCommentsRequest;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.GetAppModel;
import com.aptoide.dataprovider.webservices.models.v2.GetComments;
import com.aptoide.dataprovider.webservices.models.v3.RateApp;
import com.aptoide.dataprovider.webservices.models.v7.GetApp;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.MoreVersionsAppViewItem;
import com.aptoide.models.displayables.NoCommentPlaceHolderRow;
import com.aptoide.models.stores.Store;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.RetryPolicy;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.aptoide.amethyst.adapter.DividerItemDecoration;
import com.aptoide.amethyst.adapter.ScreenshotsAdapter;
import com.aptoide.amethyst.adapter.store.CommentsStoreAdapter;
import com.aptoide.amethyst.callbacks.AddCommentVoteCallback;
import com.aptoide.amethyst.fragments.store.LatestCommentsFragment;
import com.aptoide.amethyst.openiab.PaidAppPurchaseActivity;
import com.aptoide.amethyst.services.DownloadService;
import com.aptoide.amethyst.ui.MoreCommentsActivity;
import com.aptoide.amethyst.ui.MoreVersionsActivity;
import com.aptoide.amethyst.ui.SearchManager;
import com.aptoide.amethyst.ui.WrappingLinearLayoutManager;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.utils.ReferrerUtils;
import com.aptoide.amethyst.webservices.GetApkInfoRequestFromId;

import lombok.Getter;

import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.TRUSTED;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.UNKNOWN;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.WARNING;

/**
 *
 * Created by hsousa
 */
public class AppViewActivity extends AptoideBaseActivity implements AddCommentVoteCallback, FlagApkDialog.ApkFlagCallback {

    /**
     * True when activity is created.
     */
    private boolean lifecycleController = false;
    static String TAG = AppViewActivity.class.getSimpleName();
    public static final short DOWNGRADE_REQUEST_CODE = 456;
    private static final short Purchase_REQUEST_CODE = 30333;
    private static final short MAX_VISIBLE_COMMENTS = 3;

    private static final short MAX_COMMENTS_REQUEST = 4;

    private static final String APP_NOT_AVAILABLE = "410 Gone";

    private ResultBundle resultBundle;

    private static int getPercentage(Number total, Number quantity) {
        if (quantity.intValue() == 0 || total.intValue() == 0) {
            return 0;
        } else {
            return (quantity.intValue() * 100 / total.intValue());
        }
    }

    @Nullable
    private static PackageInfo getPackageInfo(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    static class ResultBundle {
        final int requestCode;
        final int resultCode;
        final Intent data;

        public ResultBundle(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }
    }
    
    enum BtnInstallState {
        INSTALL(0), DOWNGRADE(1), UPDATE(2), OPEN(3), BUY(4);
        int state;
        BtnInstallState(int state) {
            this.state = state;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_view_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, AppViewFragment.newInstance(lifecycleController)).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment != null && resultBundle != null) {
            fragment.onActivityResult(resultBundle.requestCode, resultBundle.resultCode, resultBundle.data);
            resultBundle = null;
        }

        lifecycleController = true;
    }

    @Override
    protected void onStop() {
        final FragmentManager manager = getSupportFragmentManager();
        final Fragment fragment = manager.findFragmentById(R.id.content);
        manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final AppViewFragment fragment = AppViewFragment.newInstance(lifecycleController);
        final FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected String getScreenName() {
        return "App View";
    }

    @Override
    public void voteComment(final int commentId, final AddApkCommentVoteRequest.CommentVote vote) {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment != null) {
            ((AddCommentVoteCallback) fragment).voteComment(commentId, vote);
        }
    }

    @Override
    public void addApkFlagClick(final String flag) {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment != null) {
            ((FlagApkDialog.ApkFlagCallback) fragment).addApkFlagClick(flag);
        }
    }

    @Nullable
    public DownloadService getService() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment != null) {
            return ((AppViewFragment) fragment).downloadService;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resultBundle = new ResultBundle(requestCode, resultCode, data);
    }

    public static class AppViewFragment extends Fragment implements FlagApkDialog.ApkFlagCallback, AddCommentVoteCallback, ServiceConnection {

        private String download_from;

        private static final String BADGE_DIALOG_TAG = "badgeDialog";
        protected SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
        private boolean lifecycleController;

        public static AppViewFragment newInstance(boolean lifecycleController) {
            AppViewFragment f = new AppViewFragment();
            Bundle args = new Bundle();
            args.putBoolean("lifecycleController", lifecycleController);
            f.setArguments(args);
            return f;
        }


        public AppViewFragment() { }

        Toolbar mToolbar;
        ImageView mFeaturedGraphic;
        ImageView mAppIcon;
        View mStoreView;
        ImageView mStoreAvatar;
        TextView mStoreName;
        TextView mStoreUsers;
        CollapsingToolbarLayout mCollapsingToolbarLayout;
        TextView mVersionName;
        TextView mDownloadsNumber;
        TextView mFileSize;
        RatingBar mRatingBarTop;
        RatingBar mRatingBar;
        TextView mDescription;
        TextView mSeeMore;
        LinearLayout mSeeMoreLayout;

        Button mButtonInstall;
        NestedScrollView mContentView;
        ProgressBar mProgressBar;
        AppBarLayout mAppBarLayout;
        ScrollView layoutNoNetwork;
        ScrollView layoutError;
        ScrollView layoutError410;
        TextView retryError;
        TextView retryNoNetwork;
        RecyclerView recyclerComments;
        TextView mSeeMoreComments;
        LinearLayout mLatestVersionLayout;
        Button mButtonGetLatest;
        Button mButtonUninstall;
        Button mButtonSubscribe;
        RelativeLayout mTrustedLayout;
        RelativeLayout mWarningLayout;
        RelativeLayout mUnknownLayoutt;
        RelativeLayout badgeLayout;
        ImageView mBadgeMarket;
        ImageView mBadgeSignature;
        ImageView mBadgeFlag;
        ImageView mBadgeAntiVirus;
        LinearLayout mFlagsLayout;
        Button mButtonFlagThisApp;
        ImageView mArrow;
        TextView tvAgvRating;
        TextView tvNumberRates;
        ProgressBar progressBarRating5;
        ProgressBar progressBarRating4;
        ProgressBar progressBarRating3;
        ProgressBar progressBarRating2;
        ProgressBar progressBarRating1;
        RatingBar avgRatingBar;
        TextView tvNumberOfStarts5;
        TextView tvNumberOfStarts4;
        TextView tvNumberOfStarts3;
        TextView tvNumberOfStarts2;
        TextView tvNumberOfStarts1;
        TextView tvNumberGoodFlags;
        TextView tvNumberFakeFlags;
        TextView tvNumberFreezeFlags;
        TextView tvNumberLicenceFlags;
        TextView tvNumberVirusFlags;
        View manualReviewLayout;
        LinearLayout mInstallAndLatestVersionLayout;
        RelativeLayout mDownloadProgressLayout;
        ImageView mActionResume;
        TextView mProgressText;
        ProgressBar mDownloadingProgress;
        RelativeLayout mMoreVersionsLayoutHeader;
        Button mMoreVersionsLayoutButton;
        TextView mMoreVersionsTitle;
        TextView mWebsiteLabel;
        TextView mEmailLabel;
        TextView mPrivacyLabel;
        TextView mPermissionsLabel;
        RecyclerView mScreenshotsList;

        RecyclerView mMoreVersionsList;

        int descriptionLines;


        private String cpd;
        private String[] referrer = new String[1];
        private WebView[] webview = new WebView[1];


        private boolean fromSponsored;

        private DownloadService downloadService;
        private volatile CountDownLatch downloadServiceLatch;
        ////// evil fields from the v6's AppView
        private boolean isPaidToschedule = true;
        private String installedSignature;
        private boolean isFromActivityResult;
        private boolean autoDownload;
        private boolean isDownloadCompleted;

        private int bucketSize;
        private int recyclerOffset;

        /** See More/Less description logic */
        private boolean extended = false;

        /** Y coordinate in order to scroll when hiding the description */
        int scrollPosition = 0;

        /**
         * v7 json attributes
         */
        @Getter private long appId;
        private long adId;
        private String signature;
        private String path;
        private long fileSize; //this is just the fileSize of the apk
        private String altPath;
        private String appName;
        private String versionName;
        private int versionCode;
        private long downloads;
        private String packageName;
        private String developer;
        private String storeName;
        private String storeAvatar;
        private EnumStoreTheme storeTheme;
        private long storeSubscribers;
        private String screen;
        private int minSdk;
        private GetAppMeta.Pay pay;
        private String md5sum;
        private String iconUrl;
        private float rating;
        private long storeId;
        private String description;
        private String graphic;
        private String wUrl;
        private String userVote;
        private GetAppMeta.Obb obb;
        private List<String> permissions;
        private GetAppMeta.File.Malware malware;
        private String website;
        private String email;

        private String privacy;

        /**
         * In v6 is the hashCode of the md5sum. Used as an Id for the downloadService
         */
        private long downloadId;
        private List<GetAppMeta.Media.Screenshot> screenshots;
        private List<GetAppMeta.Media.Video> videos;
        /**
         * Atributes for comments
         */
        protected List<Displayable> displayableCommentsList = new ArrayList<>();
        /**
         * flag to control when the secondary requests should use cache or not
         */
        boolean forceReload;
        /**
         * Flag to inform if there is a more recent version
         */
        boolean latestAvailable;
        /**
         * When it's not the latest, use this Id to call a new AppViewActivity
         */
        long latestAppId;
        /**
         * Flag to inform if the app is an update to an already installed app
         */
        boolean isUpdate;
        /**
         * Controls if the App is installed
         */
        boolean isInstalled;
        /**
         * Used to control the cache of GetApkInfo, for the "pay app" use-case
         */
        private boolean refreshOnResume;
        /**
         * When we want an app to start autoDownloading <b>without</b> showing the yes/no dialog
         * (but can still show the "Root" dialog)
         */
        boolean forceAutoDownload;

        private ApkSuggestionJson.Ads appSuggested;

        private RequestManager glide;

        /**
         * Used to control when we need to refresh the install/open button when coming back from the onResume
         */
        private boolean reloadButtons;

        private ResultBundle resultBundle;

        private RequestListener<GetAppModel> listener = new RequestListener<GetAppModel>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                handleErrorCondition(spiceException);
            }

            @Override
            public void onRequestSuccess(GetAppModel model) {
                boolean unrecoverableErrorsFound = false;
                try {
                    appId = model.getApp.nodes.meta.data.id.longValue();
                    signature = model.getApp.nodes.meta.data.file.signature.sha1;
                    path = model.getApp.nodes.meta.data.file.path;
                    altPath = model.getApp.nodes.meta.data.file.pathAlt;
                    fileSize = model.getApp.nodes.meta.data.file.filesize.longValue();
                    appName = model.getApp.nodes.meta.data.name;
                    versionName = model.getApp.nodes.meta.data.file.vername;
                    versionCode = model.getApp.nodes.meta.data.file.vercode.intValue();
                    downloads = model.getApp.nodes.meta.data.stats.downloads.longValue();
                    packageName = model.getApp.nodes.meta.data.packageName;
                    developer = model.getApp.nodes.meta.data.developer.name;
                    website = model.getApp.nodes.meta.data.developer.website;
                    email = model.getApp.nodes.meta.data.developer.email;
                    privacy = model.getApp.nodes.meta.data.developer.privacy;
                    storeName = model.getApp.nodes.meta.data.store.name;
                    storeAvatar = model.getApp.nodes.meta.data.store.avatar;
                    storeSubscribers = model.getApp.nodes.meta.data.store.stats.subscribers.longValue();
                    storeTheme = EnumStoreTheme.get(model.getApp.nodes.meta.data.store.appearance.theme);
                    screen = model.getApp.nodes.meta.data.file.hardware.screen;
                    minSdk = model.getApp.nodes.meta.data.file.hardware.sdk.intValue();
                    pay = model.getApp.nodes.meta.data.pay;
                    md5sum = model.getApp.nodes.meta.data.file.md5sum;
                    iconUrl = AptoideUtils.UI.parseIcon(getActivity(), model.getApp.nodes.meta.data.icon);
                    rating = model.getApp.nodes.meta.data.stats.rating.avg.floatValue();
                    screenshots = model.getApp.nodes.meta.data.media.screenshots;
                    videos = model.getApp.nodes.meta.data.media.videos;
                    graphic = model.getApp.nodes.meta.data.graphic;
                    wUrl = model.getApp.nodes.meta.data.urls.w;
                    storeId = model.getApp.nodes.meta.data.store.id.longValue();
                    description = model.getApp.nodes.meta.data.media.description;
                    downloadId = md5sum.hashCode();
                    obb = model.getApp.nodes.meta.data.obb;
                    permissions = model.getApp.nodes.meta.data.file.usedPermissions;
                    malware = model.getApp.nodes.meta.data.file.malware;

                    Analytics.ViewedApplication.view(packageName, developer, download_from);

                    if (model.getApp.nodes.versions != null && !model.getApp.nodes.versions.list.isEmpty() &&
                            model.getApp.nodes.meta.data.file.vercode.longValue() < model.getApp.nodes.versions.list.get(0).file.vercode.longValue()) {

                        latestAvailable = true;
                        latestAppId = model.getApp.nodes.versions.list.get(0).id.longValue();
                    }

                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Crashlytics.setLong("appId", appId);
                    Crashlytics.setString("packageName", packageName);
                    Crashlytics.setString("md5sum", md5sum);
                    handleErrorCondition(e);
                    unrecoverableErrorsFound = true;
                }


                if (!unrecoverableErrorsFound) {
                    handleSuccessCondition();
                    updateUI();
                    updateBadges();
                    updateFlags(model.getApp);
                    populateScreenShotsView();
                    populateSeeMore();
                    populatePermissionsTable(model.getApp);
                    updateStoreInfo();
                    checkInstallation();
                    handleLatestVersionLogic();
                    populateMoreVersions(model);
                    requestComments(false);
                    showDialogIfComingFromBrowser();
                    showDialogIfComingFromAPKFY();
                    populateRatings(model.getApp);

                    if (!fromSponsored) {
                        new AppViewMiddleSuggested((AppViewActivity) getActivity(), getView().findViewById(R.id.middleAppViewContainer), spiceManager, appId, packageName, model
                                .getApp.nodes.meta.data.media.keywords);
                        getOrganicAds();
                    }

                    if (isFromActivityResult || forceAutoDownload) {
                        download();
                    }

                    if (resultBundle != null) {
                        onActivityResult(resultBundle.requestCode, resultBundle.resultCode, resultBundle.data);
                        resultBundle = null;
                    }
                }
            }
        };

        private boolean isApkFy;
        private static boolean isApkFyDialogShowed = false;

        private void showDialogIfComingFromAPKFY() {
            if (obb != null ) {
                AptoideUtils.AppUtils.checkPermissions(getActivity());
            }

            isApkFy = getActivity().getIntent().getBooleanExtra(Constants.FROM_APKFY_KEY, false);
            if (isApkFy && !isPaidApp() && !isApkFyDialogShowed) {
                isApkFyDialogShowed = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit();
                    edit.putString(AptoideConfiguration.PREF_PATH_CACHE_APK, Aptoide.getContext().getFilesDir().getPath()).apply();
                }
                final InstallListener installListener = new InstallListener(iconUrl, appName, versionName, packageName, md5sum, isPaidApp());
                DialogFragment dialog = AptoideDialog.myAppInstall(appName, installListener, null);
                AptoideDialog.showDialogAllowingStateLoss(dialog, getChildFragmentManager(), Constants.FROM_APKFY_KEY);
            }
        }

        private RequestListener<GetApkInfoJson> getApkInfoJsonRequestListener = new RequestListener<GetApkInfoJson>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) { }

            @Override
            public void onRequestSuccess(GetApkInfoJson getApkInfoJson) {
                final GetApkInfoJson.Payment payment = getApkInfoJson.getPayment();

                Logger.d("AppViewActivity", "User_ID: " + SecurePreferences.getInstance().getInt("User_ID", 0));
                getActivity().supportInvalidateOptionsMenu();

                if (!getApkInfoJson.getStatus().equals("OK")) {
                    return;
                }

                if (getApkInfoJson.getPayment().status.equals("OK")) {
                    mButtonInstall.setText(R.string.install);
                    InstallListener installListener = new InstallListener(iconUrl, appName, versionName, packageName, md5sum, true);
                    mButtonInstall.setOnClickListener(installListener);
                    mButtonInstall.setVisibility(View.VISIBLE);
                    path = getApkInfoJson.getApk().getPath();
                } else {
                    mButtonInstall.setText(getString(R.string.buy) + " (" + payment.symbol + " " + payment.getAmount() + ")");
                    mButtonInstall.setVisibility(View.VISIBLE);
                    final Activity thisActivity = getActivity();
                    if (SecurePreferences.getInstance().getInt("User_ID", 0) == 0) {
                        Logger.d("AppViewActivity", "is 0, making request");
                        CheckUserCredentialsRequest request = CheckUserCredentialsRequest.buildDefaultRequest(thisActivity, SecurePreferences.getInstance().getString("access_token", null));
                        spiceManager.execute(request, new RequestListener<CheckUserCredentialsJson>() {

                            @Override
                            public void onRequestFailure(SpiceException spiceException) {
                                Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onRequestSuccess(CheckUserCredentialsJson checkUserCredentialsJson) {
                                if ("OK".equals(checkUserCredentialsJson.getStatus())) {
                                    SharedPreferences.Editor securePreferences = SecurePreferences.getInstance().edit();

                                    securePreferences.putInt("User_ID", checkUserCredentialsJson.getId());
                                    Logger.d("AppViewActivity", "updating user id to " + checkUserCredentialsJson.getId());
                                    securePreferences.commit();
                                } else {
                                    Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    mButtonInstall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FlurryAgent.logEvent("App_View_Clicked_On_Buy_Button");
                            final AccountManager accountManager = AccountManager.get(thisActivity);
                            final Account[] accounts = accountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType());

                            if (accounts.length > 0) {
                                final Intent i = new Intent(thisActivity, PaidAppPurchaseActivity.class);
                                i.putExtra("packageName", packageName);
                                i.putExtra("ID", payment.metadata.id);
                                i.putExtra("user", accounts[0].name);
                                i.putExtra("icon", iconUrl);
                                i.putExtra("label", appName);
                                i.putExtra("price", payment.amount.floatValue());
                                i.putExtra("currency_symbol", payment.symbol);
                                i.putParcelableArrayListExtra("PaymentServices", new ArrayList<Parcelable>(payment.payment_services));

                                startActivityForResult(i, Purchase_REQUEST_CODE);

                            } else {
                                Toast.makeText(Aptoide.getContext(), "You need to login to purchase.", Toast.LENGTH_LONG).show();
                                accountManager.addAccount(Aptoide.getConfiguration().getAccountType(), AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, thisActivity, new AccountManagerCallback<Bundle>() {
                                    @Override
                                    public void run(AccountManagerFuture<Bundle> bundleAccountManagerFuture) {
                                        refreshOnResume = true;
                                    }
                                }, new Handler(Looper.getMainLooper()));
                            }
                        }
                    });
                }
                mButtonInstall.setVisibility(View.VISIBLE);
            }
        };



        @Override
        public void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            glide = Glide.with(this);

            lifecycleController = getArguments().getBoolean("lifecycleController");

            if (savedInstanceState != null) {
                appId = savedInstanceState.getLong(Constants.APP_ID_KEY);
                appName = savedInstanceState.getString(Constants.APPNAME_KEY);
                fileSize = savedInstanceState.getLong(Constants.FILESIZE_KEY);
                downloadId = savedInstanceState.getLong(Constants.DOWNLOAD_ID_KEY);
                downloads = savedInstanceState.getLong(Constants.DOWNLOADS_KEY);
                storeId = savedInstanceState.getLong(Constants.STOREID_KEY);
                storeAvatar = savedInstanceState.getString(Constants.STOREAVATAR_KEY);
                packageName = savedInstanceState.getString(Constants.PACKAGENAME_KEY);
                versionName = savedInstanceState.getString(Constants.VERSIONNAME_KEY);
                md5sum = savedInstanceState.getString(Constants.MD5SUM_KEY);
            } else {
                forceAutoDownload = getActivity().getIntent().getBooleanExtra("forceAutoDownload", false);
            }
        }

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
            BusProvider.getInstance().register(this);
            spiceManager.start(getActivity());
            final View view = inflater.inflate(R.layout.fragment_app_view, container, false);
            bindViews(view);
            recyclerOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            bucketSize = AptoideUtils.UI.getBucketSize();
            mRatingBar.setOnRatingBarChangeListener(new RatingBarClickListener());
            return view;
        }

        private void bindViews(View view) {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mFeaturedGraphic = (ImageView) view.findViewById(R.id.featured_graphic);
            mAppIcon = (ImageView) view.findViewById(R.id.app_icon);
            mStoreView = (View) view.findViewById(R.id.store);
            mStoreAvatar = (ImageView) view.findViewById(R.id.store_avatar);
            mStoreName = (TextView) view.findViewById(R.id.store_name);
            mStoreUsers = (TextView) view.findViewById(R.id.store_number_users);
            mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
            mVersionName = (TextView) view.findViewById(R.id.versionName);
            mDownloadsNumber = (TextView) view.findViewById(R.id.downloads_number);
            mFileSize = (TextView) view.findViewById(R.id.file_size);
            mRatingBarTop = (RatingBar) view.findViewById(R.id.rating_bar_top);
            mRatingBar = (RatingBar) view.findViewById(R.id.ratingbar_appview);
            mDescription = (TextView) view.findViewById(R.id.description);
            mSeeMore = (TextView) view.findViewById(R.id.see_more_button);
            mSeeMoreLayout = (LinearLayout) view.findViewById(R.id.see_more_layout);
            mButtonInstall = (Button) view.findViewById(R.id.btn_install);
            mContentView = (NestedScrollView) view.findViewById(R.id.scrollview_content);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
            mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
            layoutNoNetwork = (ScrollView) view.findViewById(R.id.no_network_connection);
            layoutError = (ScrollView) view.findViewById(R.id.error);
            layoutError410 = (ScrollView) view.findViewById(R.id.error410);
            retryError = (TextView) view.findViewById(R.id.retry_error);
            retryNoNetwork = (TextView) view.findViewById(R.id.retry_no_network);
            recyclerComments = (RecyclerView) view.findViewById(R.id.appview_comments_list);
            mSeeMoreComments = (TextView) view.findViewById(R.id.see_more_comments);
            mLatestVersionLayout = (LinearLayout) view.findViewById(R.id.latestversion_layout);
            mButtonGetLatest = (Button) view.findViewById(R.id.btn_get_latest);
            mButtonUninstall = (Button) view.findViewById(R.id.btn_uninstall);
            mButtonSubscribe = (Button) view.findViewById(R.id.btn_subscribe);
            mTrustedLayout = (RelativeLayout) view.findViewById(R.id.trusted_layout);
            mWarningLayout = (RelativeLayout) view.findViewById(R.id.warning_layout);
            mUnknownLayoutt = (RelativeLayout) view.findViewById(R.id.unknown_layout);
            badgeLayout = (RelativeLayout) view.findViewById(R.id.badge_layout);
            mBadgeMarket = (ImageView) view.findViewById(R.id.iv_market_badge);
            mBadgeSignature = (ImageView) view.findViewById(R.id.iv_signature_badge);
            mBadgeFlag = (ImageView) view.findViewById(R.id.iv_flag_badge);
            mBadgeAntiVirus = (ImageView) view.findViewById(R.id.iv_antivirus_badge);
            mFlagsLayout = (LinearLayout) view.findViewById(R.id.flags_layout);
            mButtonFlagThisApp = (Button) view.findViewById(R.id.btn_flag_this_app);
            mArrow = (ImageView) view.findViewById(R.id.iv_arrow);
            tvAgvRating = (TextView) view.findViewById(R.id.appview_avg_rating);
            tvNumberRates = (TextView) view.findViewById(R.id.tv_number_of_rates);
            progressBarRating5 = (ProgressBar) view.findViewById(R.id.appview_rating_bar5);
            progressBarRating4 = (ProgressBar) view.findViewById(R.id.appview_rating_bar4);
            progressBarRating3 = (ProgressBar) view.findViewById(R.id.appview_rating_bar3);
            progressBarRating2 = (ProgressBar) view.findViewById(R.id.appview_rating_bar2);
            progressBarRating1 = (ProgressBar) view.findViewById(R.id.appview_rating_bar1);
            avgRatingBar = (RatingBar) view.findViewById(R.id.appview_rating_bar_avg);
            tvNumberOfStarts5 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number5);
            tvNumberOfStarts4 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number4);
            tvNumberOfStarts3 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number3);
            tvNumberOfStarts2 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number2);
            tvNumberOfStarts1 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number1);
            tvNumberGoodFlags = (TextView) view.findViewById(R.id.number_good_flags);
            tvNumberFakeFlags = (TextView) view.findViewById(R.id.number_fake_flags);
            tvNumberFreezeFlags = (TextView) view.findViewById(R.id.number_freeze_flags);
            tvNumberLicenceFlags = (TextView) view.findViewById(R.id.number_licence_flags);
            tvNumberVirusFlags = (TextView) view.findViewById(R.id.number_virus_flags);
            manualReviewLayout = (View) view.findViewById(R.id.manual_reviewed_message_layout);
            mInstallAndLatestVersionLayout = (LinearLayout) view.findViewById(R.id.install_and_latest_version_layout);
            mDownloadProgressLayout = (RelativeLayout) view.findViewById(R.id.download_progress_layout);
            mActionResume = (ImageView) view.findViewById(R.id.ic_action_resume);
            mProgressText = (TextView) view.findViewById(R.id.text_progress);
            mDownloadingProgress = (ProgressBar) view.findViewById(R.id.downloading_progress);
            mMoreVersionsLayoutHeader = (RelativeLayout) view.findViewById(R.id.more_layout);
            mMoreVersionsLayoutButton = (Button) view.findViewById(R.id.more);
            mMoreVersionsTitle = (TextView) view.findViewById(R.id.title);
            mWebsiteLabel = (TextView) view.findViewById(R.id.website_label);
            mEmailLabel = (TextView) view.findViewById(R.id.email_label);
            mPrivacyLabel = (TextView) view.findViewById(R.id.privacy_policy_label);
            mPermissionsLabel = (TextView) view.findViewById(R.id.permissions_label);
            mScreenshotsList = (RecyclerView) view.findViewById(R.id.screenshots_list);
            mMoreVersionsList = (RecyclerView) view.findViewById(R.id.more_versions_recycler);

            descriptionLines = view.getContext().getResources().getInteger(R.integer.minimum_description_lines);
        }

        @Override
        public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            final AppViewActivity appViewActivity = (AppViewActivity) getActivity();
            appViewActivity.setSupportActionBar(mToolbar);
            ActionBar supportActionBar = appViewActivity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
            continueLoading();
        }

        @Override
        public void onStart() {
            super.onStart();
            downloadServiceLatch = new CountDownLatch(1);
            getActivity().bindService(new Intent(getActivity(), DownloadService.class), this, BIND_AUTO_CREATE);
        }

        @Override
        public void onResume() {
            super.onResume();
            if (pay != null && pay.price != null) {
                setupInstallButton();
            }

            if (reloadButtons) {
                checkInstallation();
                updateStoreInfo();
                handleLatestVersionLogic();
                reloadButtons = false;
            }

            populateDownloadUI();
        }

        @Override
        public void onStop() {
            if (downloadService != null) {
//                downloadService = null;
                getActivity().unbindService(this);
            }
            super.onStop();
        }

        @Override
        public void onDestroyView() {
            BusProvider.getInstance().unregister(this);
            spiceManager.shouldStop();
            if (webview[0] != null) {
                webview[0].removeAllViews();
                webview[0].destroy();
            }
            AptoideUtils.UI.unbindDrawables(getView());
            super.onDestroyView();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
//            Aptoide.getRefWatcher(getContext()).watch(this);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putLong(Constants.APP_ID_KEY, appId);
            outState.putString(Constants.MD5SUM_KEY, md5sum);
            outState.putString(Constants.APPNAME_KEY, appName);
            outState.putString(Constants.ICON_KEY, iconUrl);
            outState.putLong(Constants.DOWNLOADS_KEY, downloads);
            outState.putFloat(Constants.RATING_KEY, rating);
            outState.putString(Constants.GRAPHIC_KEY, graphic);
            outState.putLong(Constants.FILESIZE_KEY, fileSize);
            outState.putLong(Constants.STOREID_KEY, storeId);
            outState.putString(Constants.STORENAME_KEY, storeName);
            outState.putString(Constants.PACKAGENAME_KEY, packageName);
            outState.putString(Constants.VERSIONNAME_KEY, versionName);
            outState.putLong(Constants.DOWNLOAD_ID_KEY, downloadId);
        }

        @Override
        public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_appview_activity, menu);
            SearchManager.setupSearch(menu, getActivity());
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int i = item.getItemId();
            if (i == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            } else if (i == R.id.menu_share) {
                FlurryAgent.logEvent("App_View_Clicked_On_Share_Button");

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.install) + " \"" + appName + "\"");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, wUrl);

                if (wUrl != null) {
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
                }
            } else if (i == R.id.menu_schedule) {
                new AptoideDatabase(Aptoide.getDb()).scheduledDownloadIfMd5(packageName, md5sum, versionName, storeName, appName, iconUrl);
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == DOWNGRADE_REQUEST_CODE) {
                try {
                    // md5sum is not loaded yet
                    if (md5sum == null) {
                        resultBundle = new ResultBundle(requestCode, resultCode, data);
                        return;
                    }
                    getActivity().getPackageManager().getPackageInfo(packageName, 0);
                    Toast.makeText(getActivity(), getString(R.string.downgrade_requires_uninstall), Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    isFromActivityResult = true;
                    executeSpiceRequestWithMd5(md5sum, storeName);
                }
            } else if (requestCode == Purchase_REQUEST_CODE) {
                Logger.d("AppViewActivity", "AppViewActivity onActivityResult Purchase_REQUEST_CODE");
                if (resultCode == RESULT_OK) {
                    refreshOnResume = true;
                    autoDownload = false;
                }
            }
        }

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            downloadService = ((DownloadService.LocalBinder) service).getService();

            if (downloadService.getDownload(downloadId).getDownload() != null) {
                onDownloadUpdate(downloadService.getDownload(downloadId).getDownload());
            } else {
                mInstallAndLatestVersionLayout.setVisibility(View.VISIBLE);
                mDownloadProgressLayout.setVisibility(View.GONE);
            }
            downloadServiceLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) { }

        private void populateSeeMore() {
            if (description != null && description.length() > 250) {
                try {
                    // Fix for AN-348: replace the & with &amp; (that's was causing the pushback buffer full)
                    mDescription.setText(Html.fromHtml(description.replace("\n", "<br/>").replace("&", "&amp;")));
                    mDescription.setMovementMethod(LinkMovementMethod.getInstance());
                    mSeeMoreLayout.setOnClickListener(extendListener);
                } catch (Exception e) {
                    Logger.printException(e);
                    mSeeMoreLayout.setVisibility(View.GONE);
                    mDescription.setText(getString(R.string.error_APK_Description));
                }

            } else {
                mSeeMoreLayout.setVisibility(View.GONE);
                mDescription.setText(getString(R.string.error_APK_Description));
            }
        }

        private void populateRatings(GetApp getApp) {
            if (getApp.nodes == null || getApp.nodes.meta == null || getApp.nodes.meta.data== null
                    || getApp.nodes.meta.data.stats == null|| getApp.nodes.meta.data.stats.rating == null){
                return;
            }

            GetAppMeta.Stats.Rating ratings = getApp.nodes.meta.data.stats.rating;

            try {
                float numberfloat = ratings.avg.floatValue();
                //Avg ratings
                tvAgvRating.setText(String.format("%.1f", numberfloat));
            } catch (NullPointerException | IllegalFormatException e) {
                tvAgvRating.setText(ratings.avg.toString());
            }

            long numberVotes = ratings.votes.get(0).count.longValue() + ratings.votes.get(1).count.longValue()
                    + ratings.votes.get(2).count.longValue() + ratings.votes.get(3).count.longValue()
                    + ratings.votes.get(4).count.longValue();
            tvNumberRates.setText(String.valueOf(numberVotes));
            avgRatingBar.setRating(ratings.avg.floatValue());

            //rating bars
            progressBarRating5.setProgress(getPercentage(numberVotes, ratings.votes.get(0).count));
            tvNumberOfStarts5.setText(ratings.votes.get(0).count.toString());
            progressBarRating4.setProgress(getPercentage(numberVotes, ratings.votes.get(1).count));
            tvNumberOfStarts4.setText(ratings.votes.get(1).count.toString());
            progressBarRating3.setProgress(getPercentage(numberVotes, ratings.votes.get(2).count));
            tvNumberOfStarts3.setText(ratings.votes.get(2).count.toString());
            progressBarRating2.setProgress(getPercentage(numberVotes, ratings.votes.get(3).count));
            tvNumberOfStarts2.setText(ratings.votes.get(3).count.toString());
            progressBarRating1.setProgress(getPercentage(numberVotes, ratings.votes.get(4).count));
            tvNumberOfStarts1.setText(ratings.votes.get(4).count.toString());
        }

        private void populatePermissionsTable(final GetApp getApp) {
            String websiteAux;
            String emailAux;
            final String privacyAux;

            if (TextUtils.isEmpty(website)) {
                websiteAux = getString(R.string.na);
            } else {
                websiteAux = website;
                mWebsiteLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AptoideUtils.Algorithms.copyToClipBoard(getActivity(), website)) {
                            Toast.makeText(getActivity(), R.string.website_copied, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (TextUtils.isEmpty(email)) {
                emailAux = getString(R.string.na);
            } else {
                emailAux = email;
                mEmailLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AptoideUtils.Algorithms.copyToClipBoard(getActivity(), email)) {
                            Toast.makeText(getActivity(), R.string.email_copied, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (TextUtils.isEmpty(privacy)) {
                privacyAux = getString(R.string.na);
            } else {
                privacyAux = privacy;
                mPrivacyLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AptoideUtils.Algorithms.copyToClipBoard(getActivity(), privacyAux)) {
                            Toast.makeText(getActivity(), R.string.privacy_copied, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
//        if (TextUtils.isEmpty(email)) {
//            email = getString(R.string.na);
//        }
//        if (TextUtils.isEmpty(privacy)) {
//            privacy = getString(R.string.na);
//        }

            mWebsiteLabel.setText(Html.fromHtml(AptoideUtils.StringUtils.getFormattedString(getActivity(), R.string.website, websiteAux)));
            mEmailLabel.setText(Html.fromHtml(AptoideUtils.StringUtils.getFormattedString(getActivity(), R.string.email, emailAux)));
            mPrivacyLabel.setText(Html.fromHtml(AptoideUtils.StringUtils.getFormattedString(getActivity(), R.string.privacy_policy, privacyAux)));
            if (getApp == null || getApp.nodes == null || getApp.nodes.meta == null || getApp.nodes.meta.data == null
                    || getApp.nodes.meta.data.file == null || getApp.nodes.meta.data.file.usedPermissions == null
                    || getApp.nodes.meta.data.file.usedPermissions.isEmpty()) {

                mPermissionsLabel.setText(getString(R.string.permissions_na));
            } else {

                mPermissionsLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogPermissions dialogPermissions = DialogPermissions.newInstance(getApp, appName, versionName, iconUrl, AptoideUtils.StringUtils.formatBits(AptoideUtils.AppUtils.sumFileSizes(fileSize, obb)));
                        dialogPermissions.show(getChildFragmentManager(), "");
                    }
                });
            }
        }

        RequestListener<GetComments> requestCommentListener = new RequestListener<GetComments>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Logger.printException(spiceException);
            }

            @Override
            public void onRequestSuccess(GetComments get) {
                forceReload = false;
                if (get != null && get.list != null) {

                    if (get.list.size() > MAX_VISIBLE_COMMENTS) {
                        mSeeMoreComments.setVisibility(View.VISIBLE);
                        mSeeMoreComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), MoreCommentsActivity.class);
                                i.putExtra(Constants.STORENAME_KEY, storeName);
                                i.putExtra(Constants.PACKAGENAME_KEY, packageName);
                                i.putExtra(Constants.VERSIONNAME_KEY, versionName);
                                i.putExtra(Constants.EVENT_LABEL, appName); // The label is used to populate the title
                                // in the MoreActivity
                                startActivity(i);
                            }
                        });
                    }

                    displayableCommentsList.clear();
                    displayableCommentsList.add(new HeaderRow("Comments", false, bucketSize));

                    if (!get.list.isEmpty()) {

                        for (int i = 0; i < get.list.size() && i < MAX_VISIBLE_COMMENTS; i++) {
                            displayableCommentsList.add(LatestCommentsFragment.createComment(get.list.get(i)));
                        }

                        displayableCommentsList = LatestCommentsFragment.sortComments(displayableCommentsList);

                    } else {
                        displayableCommentsList.add(new NoCommentPlaceHolderRow(bucketSize));
                    }

                    CommentsStoreAdapter adapter = new CommentsStoreAdapter(displayableCommentsList, getActivity(), getResources().getColor(EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT.getStoreHeader()), true, addCommentCallback);

                    recyclerComments.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            outRect.set(recyclerOffset, recyclerOffset, recyclerOffset, 0);
                        }
                    });
                    recyclerComments.setLayoutManager(new WrappingLinearLayoutManager(getActivity()));
                    recyclerComments.setNestedScrollingEnabled(false);
                    recyclerComments.setAdapter(adapter);
                }
            }
        };

        AddCommentCallback addCommentCallback = new AddCommentCallback() {
            @Override
            public void addComment(String comment, String answerTo) {
                if (comment != null && comment.length() < Constants.MIN_COMMENT_CHARS) {
                    Toast.makeText(getActivity(), R.string.error_IARG_100, Toast.LENGTH_LONG).show();
                    return;
                }

                AddCommentRequest request = new AddCommentRequest(getActivity());
                request.setApkversion(versionName);
                request.setPackageName(packageName);
                request.setRepo(storeName);
                request.setText(comment);

                if (answerTo != null) {
                    request.setAnswearTo(answerTo);
                }

                spiceManager.execute(request, addCommentRequestListener);
                AptoideDialog.pleaseWaitDialog().show(getChildFragmentManager(), "pleaseWaitDialog");
            }
        };

        RequestListener<GenericResponseV2> addCommentRequestListener = new AlmostGenericResponseV2RequestListener() {
            @Override
            public void CaseOK() {
                Toast.makeText(getActivity(), getString(R.string.comment_submitted), Toast.LENGTH_LONG).show();
                refresh();
                dismissDialog();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                super.onRequestFailure(spiceException);
                dismissDialog();
            }

            @Override
            public void onRequestSuccess(GenericResponseV2 genericResponse) {
                super.onRequestSuccess(genericResponse);
                dismissDialog();
            }

            protected void dismissDialog() {
                DialogFragment pd = (DialogFragment) getChildFragmentManager().findFragmentByTag("pleaseWaitDialog");
                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }
            }
        };

        View.OnClickListener badgeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AptoideDialog.badgeDialogV7(malware, appName, malware.rank).show(getFragmentManager(), BADGE_DIALOG_TAG);
            }
        };

        private View.OnClickListener extendListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extendSeeMore();
            }
        };

        /**
         * Callback when dismissing MyAppInstallDialog
         */
        DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (!new AptoideDatabase(Aptoide.getDb()).existsRepo(storeName)) {
                    AptoideDialog.addMyAppStore("http://" + storeName + ".store.aptoide.com/", appsAddStoreInterface).show(getChildFragmentManager(), "myAppStore");
                }
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
                        AptoideUtils.RepoUtils.startParse(store.getName(), getActivity(), spiceManager);
                    }
                };
            }
        };

        private void updateFlags(GetApp getApp) {

            if (getApp == null || getApp.nodes == null || getApp.nodes.meta == null || getApp.nodes.meta.data == null
                    || getApp.nodes.meta.data.file == null || getApp.nodes.meta.data.file.flags == null) {
                return;
            }

            GetAppMeta.File.Flags flags = getApp.nodes.meta.data.file.flags;

            if (GetAppMeta.File.Flags.GOOD.equals(flags.review)) {
                mFlagsLayout.setVisibility(View.GONE);
                mButtonFlagThisApp.setVisibility(View.GONE);
                manualReviewLayout.setVisibility(View.VISIBLE);

            } else  {
                manualReviewLayout.setVisibility(View.GONE);
                mFlagsLayout.setVisibility(View.VISIBLE);
                mButtonFlagThisApp.setVisibility(View.VISIBLE);
                mButtonFlagThisApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AptoideUtils.AccountUtils.isLoggedIn(getActivity())) {
                            AptoideDialog.flagAppDialog(userVote).show(getChildFragmentManager(), "flagAppDialog");
                        } else {
                            Intent intent = new Intent(getActivity(), MyAccountActivity.class);
                            getActivity().startActivity(intent);
                        }
                    }
                });
                if (flags.votes != null) {
                    for (GetAppMeta.File.Flags.Vote vote : flags.votes) {
                        Number numberVotes = vote.count;
                        if (vote.type.toUpperCase().equals(GetAppMeta.File.Flags.Vote.GOOD)) {
                            tvNumberGoodFlags.setText(String.valueOf(numberVotes));
                        } else if (vote.type.toUpperCase().equals(GetAppMeta.File.Flags.Vote.FREEZE)) {
                            tvNumberFreezeFlags.setText(String.valueOf(numberVotes));
                        } else if (vote.type.toUpperCase().equals(GetAppMeta.File.Flags.Vote.FAKE)) {
                            tvNumberFakeFlags.setText(String.valueOf(numberVotes));
                        } else if (vote.type.toUpperCase().equals(GetAppMeta.File.Flags.Vote.LICENSE)) {
                            tvNumberLicenceFlags.setText(String.valueOf(numberVotes));
                        } else if (vote.type.toUpperCase().equals(GetAppMeta.File.Flags.Vote.VIRUS)) {
                            tvNumberVirusFlags.setText(String.valueOf(numberVotes));
                        }
                    }
                }
            }
        }

        private void updateBadges() {
            if (malware == null || malware.rank == null) {
                return;
            }

            badgeLayout.setOnClickListener(badgeClickListener);


            switch (malware.rank) {
                case TRUSTED:
                    mTrustedLayout.setVisibility(View.VISIBLE);
                    mTrustedLayout.setOnClickListener(badgeClickListener);

                    break;
                case WARNING:
                    mWarningLayout.setVisibility(View.VISIBLE);
                    mWarningLayout.setOnClickListener(badgeClickListener);

                    break;
                case UNKNOWN:
                    mUnknownLayoutt.setVisibility(View.VISIBLE);
                    mUnknownLayoutt.setOnClickListener(badgeClickListener);
                    break;
            }

            if (malware.reason != null) {

                if (malware.reason.thirdpartyValidated != null && GetAppMeta.File.Malware.GOOGLE_PLAY.equalsIgnoreCase(malware.reason.thirdpartyValidated.store)) {
                    mBadgeMarket.setVisibility(View.VISIBLE);
                }

                if (malware.reason.signatureValidated != null && GetAppMeta.File.Malware.PASSED.equals(malware.reason.signatureValidated.status)) {
                    mBadgeSignature.setVisibility(View.VISIBLE);
                }

                if (malware.reason.scanned != null && GetAppMeta.File.Malware.PASSED.equals(malware.reason.scanned.status)) {
                    mBadgeAntiVirus.setVisibility(View.VISIBLE);
                }

                if (malware.reason.manualQA != null && GetAppMeta.File.Malware.PASSED.equals(malware.reason.manualQA.status)) {
                    mBadgeFlag.setVisibility(View.VISIBLE);
                }
            }
        }

        private void updateStoreInfo() {

            final Context context = getActivity();
            if (TextUtils.isEmpty(storeAvatar)) {
                glide.fromResource().load(R.drawable.ic_avatar_apps).transform(new CircleTransform(context)).into(mStoreAvatar);
            } else {
                glide.load(storeAvatar).bitmapTransform(new CircleTransform(context)).into(mStoreAvatar);
            }

            mStoreName.setText(storeName);
            mStoreName.setTextColor(getResources().getColor(storeTheme.getStoreHeader()));
            mStoreUsers.setText(String.valueOf(storeSubscribers));
            if(Build.VERSION.SDK_INT > 10 ) {
                mButtonSubscribe.setBackgroundDrawable(getResources().getDrawable(storeTheme.getButtonLayout()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mButtonSubscribe.setElevation(0);
                }
            }
            mButtonSubscribe.setTextColor(getResources().getColor(storeTheme.getStoreHeader()));

            OpenStoreOnClickListener openStoreOnClickListener = new OpenStoreOnClickListener();
            mStoreView.setOnClickListener(openStoreOnClickListener);

            final boolean subscribed = new AptoideDatabase(Aptoide.getDb()).existsStore(storeId);
            if (subscribed) {
                int checkmarkDrawable = storeTheme.getCheckmarkDrawable();
                mButtonSubscribe.setCompoundDrawablesWithIntrinsicBounds(checkmarkDrawable, 0, 0, 0);
                mButtonSubscribe.setText(getString(R.string.appview_subscribed_store_button_text));
                mButtonSubscribe.setOnClickListener(openStoreOnClickListener);

            } else {
                int plusMarkDrawable = storeTheme.getPlusmarkDrawable();
                mButtonSubscribe.setCompoundDrawablesWithIntrinsicBounds(plusMarkDrawable, 0, 0, 0);
                mButtonSubscribe.setText(getString(R.string.appview_subscribe_store_button_text));
                mButtonSubscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AptoideUtils.RepoUtils.startParse(storeName, getActivity(), spiceManager);
                    }
                });
            }
        }

        class OpenStoreOnClickListener implements View.OnClickListener{

            @Override
            public void onClick(View v) {
                reloadButtons = true;
                final Context context = getActivity();
                final Intent intent = StoresActivity.newIntent(context,
                        storeId, storeName, storeAvatar, storeTheme.ordinal());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        }

        private void updateUI() {

            if (graphic != null) {
                glide.load(graphic).into(mFeaturedGraphic);
            } else if (screenshots != null && screenshots.size() > 0 && !TextUtils.isEmpty(screenshots.get(0).url)) {
                glide.load(screenshots.get(0).url).into(mFeaturedGraphic);
            }

            if (iconUrl != null) {
                glide.load(iconUrl).into(mAppIcon);
            }

            mVersionName.setText(versionName);
            mDownloadsNumber.setText(AptoideUtils.StringUtils.withSuffix(downloads));
            mFileSize.setText(AptoideUtils.StringUtils.formatBits(AptoideUtils.AppUtils.sumFileSizes(fileSize, obb)));
            mRatingBarTop.setRating(rating);
            mCollapsingToolbarLayout.setTitle(appName);
        }

        private void populateMoreVersions(GetAppModel model) {
            GetApp getApp = model.getApp;
            mMoreVersionsList.setNestedScrollingEnabled(false);

            /**
             * If the size of the list is 1, it means it's the own app and should be removed.
             */
            if (getApp == null || getApp.nodes == null || getApp.nodes.versions == null || getApp.nodes.versions.list == null
                    || getApp.nodes.versions.list.size() < 2) {

                mMoreVersionsLayoutHeader.setVisibility(View.GONE);
                mMoreVersionsList.setVisibility(View.GONE);
                return;
            }

            mMoreVersionsLayoutButton.setClickable(false);
            mMoreVersionsLayoutHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), MoreVersionsActivity.class);
                    intent.putExtra(Constants.PACKAGENAME_KEY, packageName);
                    intent.putExtra(Constants.EVENT_LABEL, appName);
                    startActivity(intent);
                }
            });

            mMoreVersionsTitle.setText(R.string.appview_other_versions_in_others_stores);

            // AN-227: remove the first app if it's the same as this
            if (!model.list.isEmpty()
                    &&  model.list.get(0).versionCode == versionCode) {

                model.list.remove(0);
            }


            // Moar hack: only use one row
            List<MoreVersionsAppViewItem> list = new ArrayList<>();
            for (int i = 0; i < model.list.size() && i < bucketSize; i++) {
                list.add(model.list.get(i));
            }

            MoreAppViewVersionsAdapter adapter = new MoreAppViewVersionsAdapter(list);

            mMoreVersionsList.setAdapter(adapter);
            final short recyclerViewOffset = (short) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            mMoreVersionsList.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.set(recyclerViewOffset, recyclerViewOffset, recyclerViewOffset, recyclerViewOffset);
                }
            });

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), bucketSize);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    if (!(mMoreVersionsList.getAdapter() instanceof SpannableRecyclerAdapter)) {
                        throw new IllegalStateException("RecyclerView adapter must extend SpannableRecyclerAdapter");
                    }

                    return AptoideUtils.UI.getSpanSize(mMoreVersionsList, position);
                }
            });

            gridLayoutManager.setSpanCount(bucketSize);

            mMoreVersionsList.setLayoutManager(gridLayoutManager);
        }

        private void setupInstallButton() {
            setBtnState();
            BtnInstallState state = (BtnInstallState) mButtonInstall.getTag();
            switch (state) {
                case BUY:
                    changebtInstalltoBuy();
                    mButtonInstall.setVisibility(View.GONE);
                    break;
                case DOWNGRADE:
                    mButtonInstall.setText(R.string.downgrade);
                    break;
                case UPDATE:
                    mButtonInstall.setText(R.string.update);
                    break;
                case INSTALL:
                    mButtonInstall.setText(R.string.install);
                    break;
                case OPEN:
                    mButtonInstall.setText(R.string.open);
                    changebtInstalltoOpen();
                    break;
            }
        }

        private void changebtInstalltoBuy() {

            GetApkInfoRequestFromId getApkInfoRequestFromId = new GetApkInfoRequestFromId(getActivity());
            getApkInfoRequestFromId.setAppId(Long.toString(appId));

            spiceManager.execute(getApkInfoRequestFromId, getApkInfoJsonRequestListener);
        }

        public void setBtnState() {
            PackageInfo info = getPackageInfo(getActivity(), packageName);
            //check if the app is installed
            if (info == null) {
                //check if the app is payed
                if (pay != null && pay.price.doubleValue() > 0) {
                    mButtonInstall.setTag(BtnInstallState.BUY);
                } else {
                    mButtonInstall.setTag(BtnInstallState.INSTALL);
                }
            } else {
                if (versionCode > info.versionCode) {
                    mButtonInstall.setTag(BtnInstallState.UPDATE);
                } else if (versionCode < info.versionCode) {
                    mButtonInstall.setTag(BtnInstallState.DOWNGRADE);
                } else {
                    mButtonInstall.setTag(BtnInstallState.OPEN);
                }
            }
        }

        private void checkInstallation() {
            mButtonInstall.setEnabled(true);
            if (pay != null && pay.price.doubleValue() > 0) {
                isPaidToschedule = true;
//                mButtonInstall.setVisibility(View.GONE);
                InstallListener installListener = new InstallListener(iconUrl, appName, versionName, packageName, md5sum, isPaidApp());
                mButtonInstall.setOnClickListener(installListener);
            } else {
                PackageInfo info = getPackageInfo(getActivity(), packageName);
                if (info == null) {
                    mButtonInstall.setText(getString(R.string.install));
                    mButtonInstall.setOnClickListener(new InstallListener(iconUrl, appName, versionName, packageName, md5sum, isPaidApp()));
                    isInstalled = false;
                } else {
                    isInstalled = true;
                    try {
                        installedSignature = AptoideUtils.Algorithms.computeSHA1sumFromBytes(info.signatures[0].toByteArray()).toUpperCase(Locale.ENGLISH);
                    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                        Logger.printException(e);
                    }
                    if (versionCode > info.versionCode) {
                        isUpdate = true;
                        mButtonInstall.setText(getString(R.string.update));
                        mButtonInstall.setEnabled(true);
                        mButtonInstall.setOnClickListener(new InstallListener(iconUrl, appName, versionName, packageName, md5sum, isPaidApp()));
//                    UpdateAppVersionInstalled(info.versionName);
                    } else if (versionCode < info.versionCode) {
                        isUpdate = false;
                        mButtonInstall.setText(getString(R.string.downgrade));
                        mButtonInstall.setOnClickListener(new DowngradeListener(iconUrl, appName, info.versionName, versionName, info.packageName));
//                    UpdateAppVersionInstalled(info.versionName);
                    } else {
                        changebtInstalltoOpen();
                    }
                    getActivity().supportInvalidateOptionsMenu();
                }
            }

            setupInstallButton();
        }

        private void changebtInstalltoOpen() {
            final Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
            mButtonInstall.setText(getString(R.string.open));
            if (i != null) {
                mButtonInstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            Logger.printException(e);
                        }
                    }
                });
            } else {
                mButtonInstall.setEnabled(false);
            }
        }

        /**
         * Changes maxLines of the description textView. Needs to be synchronized because it can be
         * called simultaneously on two different views.
         */
        public synchronized void extendSeeMore() {

            /** The MAX_LINES parameter cannot be too long, or the animation will not be seen smoothly */
            final int MAX_LINES = 200;
            final int ANIMATION_DELAY = 0;
            int maxLines;
            int[] values;
            String text;

            if (extended) {
                maxLines = descriptionLines;
                values = new int[]{MAX_LINES, maxLines};
                text = getString(R.string.see_more);
                mArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_down_arrow));
            } else {
                maxLines = MAX_LINES;
                values = new int[]{descriptionLines, maxLines};
                text = getString(R.string.see_less);
                mArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_up_arrow));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ObjectAnimator animation = ObjectAnimator.ofInt(mDescription, "maxLines", values);
                animation.setDuration(ANIMATION_DELAY).start();
            } else {
                mDescription.setMaxLines(maxLines);
            }

            if (extended) {
                mContentView.scrollTo(0, scrollPosition);
            } else {
                scrollPosition = mDescription.getLeft();
            }

            mSeeMore.setText(text);
            extended = !extended;
        }

        private void getOrganicAds() {
            if (lifecycleController) {
                return;
            }

            final GetAdsRequest getAdsRequest = new GetAdsRequest("", false);

            getAdsRequest.setLocation("appview");
            getAdsRequest.setKeyword("__NULL__");
            getAdsRequest.setRepo(storeName);
            getAdsRequest.setPackage_name(packageName);
            getAdsRequest.setLimit(1);
            getAdsRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getRetryCount() {
                    return 0;
                }

                @Override
                public void retry(SpiceException e) {
                }

                @Override
                public long getDelayBeforeRetry() {
                    return 0;
                }
            });
            getAdsRequest.setTimeout(2000);

            spiceManager.execute(getAdsRequest, new RequestListener<ApkSuggestionJson>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                }

                @Override
                public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {
                    try {

                        ApkSuggestionJson.Ads appSuggested = apkSuggestionJson.getAds().get(0);

                        if (apkSuggestionJson.getAds().size() == 0) {
                            return;
                        }
                        if (appSuggested.getPartner() == null) {
                            return;
                        }
                        if (appSuggested.getPartner().getPartnerData() == null) {
                            return;
                        }

                        appSuggested = apkSuggestionJson.getAds().get(0);

                        String clickUrl = appSuggested.getPartner().getPartnerData().getClick_url();
                        final String adPackageName = appSuggested.getData().getPackageName();
                        long id = appSuggested.getData().getId().longValue();
                        adId = appSuggested.getInfo().getAd_id();

                        ReferrerUtils.extractReferrer(packageName, appId, adId, -1, clickUrl, spiceManager, null, ReferrerUtils.RETRIES);

                        OkHttpClient client = new OkHttpClient();

                        Request cpc_click = new Request.Builder().url(appSuggested.getInfo().getCpc_url()).build();

                        client.newCall(cpc_click).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                            }
                        });

                        Intent intent = getActivity().getIntent();
                        intent.putExtra("cpi", appSuggested.getInfo().getCpi_url());
                        getActivity().setIntent(intent);
                    } catch (Exception e) {
                        Logger.printException(e);
                    }
                }
            });
        }

        @Subscribe
        public void refreshStoresEvent(OttoEvents.RepoAddedEvent event) {
            Toast.makeText(getActivity(), AptoideUtils.StringUtils.getFormattedString(getActivity(), com.aptoide.amethyst.R.string.store_subscribed, storeName), Toast.LENGTH_SHORT).show();
            updateStoreInfo();
        }
        @Subscribe
        public void onDownloadEventUpdate(OttoEvents.DownloadEvent download) {
            if (downloadService != null && download.getId() == downloadId) {
                onDownloadUpdate(downloadService.getDownload(download.getId()).getDownload());
            }
        }

        @Subscribe
        public void onInstalledEvent(OttoEvents.InstalledApkEvent event) {
            refresh();
        }

        @Subscribe
        public void onUnInstalledEvent(OttoEvents.UnInstalledApkEvent event) {
            refresh();
        }

        @Subscribe
        public void onRefresh(OttoEvents.AppViewRefresh event) {
            refresh();
        }

        @Subscribe
        public void onDownloadUpdate(OttoEvents.DownloadInProgress event) {
            if (event != null) {
                Download download = event.getDownload();
                onDownloadUpdate(download);
            }
        }

        private void refresh() {
            forceReload = true;
            executeSpiceRequestWithAppId(appId, storeName, packageName);
        }

        /**
         * Hides the layout with the install button and shows the download progress bar.
         * Creates a {@link Download} object and send it to the {@link DownloadService}
         *
         */
        private void download() {
            this.showRootDialog();

            Download download = new Download();
            download.setId(downloadId);
            download.setName(appName);
            download.setVersion(versionName);
            download.setIcon(iconUrl);
            download.setPackageName(packageName);
            download.setMd5(md5sum);
            download.setPaid(isPaidApp());

            if (!isUpdate) {
                download.setCpiUrl(getActivity().getIntent().getStringExtra("cpi"));
            }
            try {
                //TODO Main thread synchronization
                downloadServiceLatch.await();
            } catch (InterruptedException e) {
                Logger.printException(e);
            }
            if (downloadService == null) {
                return;
            }
            downloadService.downloadFromV7WithObb(path, altPath, md5sum, fileSize, appName, packageName, versionName, iconUrl, appId, pay != null, obb, download, permissions);

            isFromActivityResult = false;
            autoDownload = false;

            populateDownloadUI();
        }

        private void onDownloadUpdate(Download download) {
            if (download != null && download.getId() == downloadId) {
                mInstallAndLatestVersionLayout.setVisibility(View.GONE);
                mDownloadProgressLayout.setVisibility(View.VISIBLE);

                switch (download.getDownloadState()) {
                    case ACTIVE:
                        mActionResume.setVisibility(View.GONE);
                        mDownloadingProgress.setIndeterminate(false);
                        mDownloadingProgress.setProgress(download.getProgress());
                        mProgressText.setText(download.getProgress() + "% - " + AptoideUtils.StringUtils.formatBits((long) download.getSpeed()) + "/s");
                        break;
                    case INACTIVE:
                        break;
                    case COMPLETE:
                        mInstallAndLatestVersionLayout.setVisibility(View.VISIBLE);
                        mDownloadProgressLayout.setVisibility(View.GONE);
                        break;
                    case PENDING:
                        mActionResume.setVisibility(View.GONE);
                        mDownloadingProgress.setIndeterminate(false);
                        mDownloadingProgress.setProgress(download.getProgress());
                        mProgressText.setText(getString(R.string.download_pending));
                        break;
                    case ERROR:
                        mActionResume.setVisibility(View.VISIBLE);
                        mProgressText.setText(download.getDownloadState().name());
                        mDownloadingProgress.setIndeterminate(false);
                        mDownloadingProgress.setProgress(download.getProgress());
                        break;
                }
            }
        }

        private void setShareTimeLineButton() {
            final CheckBox btinstallshare = (CheckBox) getView().findViewById(R.id.btinstallshare);
            if (Preferences.getBoolean(Preferences.TIMELINE_ACEPTED_BOOL, false)) {
                btinstallshare.setVisibility(View.VISIBLE);
                btinstallshare.setChecked(Preferences.getBoolean(Preferences.SHARE_TIMELINE_DOWNLOAD_BOOL, true));
                btinstallshare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final Map<String, String> shareTimelineParams = new HashMap<String, String>();
                        shareTimelineParams.put("Share_Timeline", String.valueOf(isChecked));
                        FlurryAgent.logEvent("App_View_Clicked_On_Share_Timeline_Checkbox", shareTimelineParams);
                        Preferences.putBooleanAndCommit(Preferences.SHARE_TIMELINE_DOWNLOAD_BOOL, isChecked);
                    }
                });
            } else {
                btinstallshare.setVisibility(View.INVISIBLE);
            }
        }

        private void populateDownloadUI() {
            setShareTimeLineButton();

            getView().findViewById(R.id.ic_action_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (downloadService != null) {
                        FlurryAgent.logEvent("App_View_Canceled_Download");
                        downloadService.stopDownload(downloadId);
                    }

                }
            });

            getView().findViewById(R.id.ic_action_resume).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (downloadService != null) {
                        downloadService.resumeDownload(downloadId);
                    }
                }
            });

            if (downloadService != null && downloadService.getDownload(downloadId).getDownload() != null) {
                onDownloadUpdate(downloadService.getDownload(downloadId).getDownload());
            }

            if(refreshOnResume) {
                spiceManager.removeDataFromCache(GetApkInfoJson.class);
                refreshOnResume = false;
            }

        }

        /**
         * Will create the views that show images and video screenshots.
         */
        private void populateScreenShotsView() {
            ArrayList<IMediaObject> mediaObjects = new ArrayList<>();

            if(videos != null) {
                // Add Videos' screenshots first
                for (GetAppMeta.Media.Video video : videos) {
                    mediaObjects.add(new Video(video.thumbnail, video.url));
                }
            }

            if (screenshots != null) {
                for (GetAppMeta.Media.Screenshot screenshot : screenshots) {
                    mediaObjects.add(new Screenshot(screenshot.url, screenshot.getOrientation()));
                }
            }

            ScreenshotsAdapter adapter = new ScreenshotsAdapter(glide, mediaObjects);
            mScreenshotsList.addItemDecoration(new DividerItemDecoration(AptoideUtils.getPixels(getActivity(), 5)));
            mScreenshotsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mScreenshotsList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed
            mScreenshotsList.setAdapter(adapter);

            if(mediaObjects.isEmpty()) {
                mScreenshotsList.setVisibility(View.GONE);
            }
        }

        private boolean isPaidApp() {
            return pay != null && pay.price.doubleValue() > 0;
        }

        private void handleErrorCondition(Exception e) {

            Logger.printException(e);
            mProgressBar.setVisibility(View.GONE);
            mAppBarLayout.setVisibility(View.GONE);
            mContentView.setVisibility(View.GONE);

            if (e != null && e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().equals(APP_NOT_AVAILABLE)) {
                layoutError410.setVisibility(View.VISIBLE);
                Toolbar mToolbar = (Toolbar) getView().findViewById(R.id.toolbar_410);
                final AppViewActivity activity = (AppViewActivity) getActivity();
                activity.setSupportActionBar(mToolbar);
                ActionBar supportActionBar = activity.getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle("Aptoide");
                    supportActionBar.setHomeButtonEnabled(true);
                    supportActionBar.setDisplayHomeAsUpEnabled(true);
                }
            } else if (e instanceof NoNetworkException) {
                layoutError.setVisibility(View.GONE);
                layoutNoNetwork.setVisibility(View.VISIBLE);
                retryNoNetwork.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mProgressBar != null && layoutNoNetwork != null) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            layoutNoNetwork.setVisibility(View.GONE);
                        }
                        refresh();
                    }
                });
            } else {
                showGenericError();
            }
        }

        private void handleSuccessCondition() {
            mContentView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            layoutError.setVisibility(View.GONE);
            layoutNoNetwork.setVisibility(View.GONE);
            mAppBarLayout.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.VISIBLE);
        }

        private void showGenericError() {
            layoutNoNetwork.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
            retryError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mProgressBar != null && layoutError != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        layoutError.setVisibility(View.GONE);
                    }
                    refresh();
                }
            });
        }

        private void handleLatestVersionLogic() {
            if (latestAvailable) {

                mLatestVersionLayout.setVisibility(View.GONE);
                mButtonUninstall.setVisibility(View.GONE);
                mButtonGetLatest.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mButtonGetLatest.setElevation(0);
                }
                mButtonGetLatest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), AppViewActivity.class);
                        i.putExtra(Constants.FROM_RELATED_KEY, true);
                        i.putExtra(Constants.APP_ID_KEY, latestAppId);
                        i.putExtra(Constants.APPNAME_KEY, appName);
                        i.putExtra(Constants.PACKAGENAME_KEY, packageName);
                        i.putExtra(Constants.DOWNLOAD_FROM_KEY, "app_view_more_multiversion");
                        startActivity(i);
                    }
                });
            } else if (isInstalled) {
                mButtonGetLatest.setVisibility(View.GONE);
                mLatestVersionLayout.setVisibility(View.GONE);
                mButtonUninstall.setVisibility(View.VISIBLE);
                mButtonUninstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reloadButtons = true;
                        Fragment uninstallFragment = new UninstallRetainFragment();
                        Bundle args = new Bundle();
                        args.putString("name", appName);
                        args.putString("package", packageName);
                        args.putString("version", versionName);
                        args.putString("icon", iconUrl);
                        uninstallFragment.setArguments(args);
                        getFragmentManager().beginTransaction().add(uninstallFragment, "uninstallFrag").commit();
                    }
                });
            } else {
                mButtonUninstall.setVisibility(View.GONE);
                mButtonGetLatest.setVisibility(View.GONE);
                mLatestVersionLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void addApkFlagClick(String flag) {

            final DialogFragment dialogFragment = AptoideDialog.pleaseWaitDialog();
            dialogFragment.show(getChildFragmentManager(), "pleaseWaitDialog");

            AddApkFlagRequest flagRequest = new AddApkFlagRequest();
            flagRequest.setRepo(storeName);
            flagRequest.setMd5sum(md5sum);
            flagRequest.setFlag(flag);
            userVote = flag;
            spiceManager.execute(AptoideUtils.RepoUtils.buildFlagAppRequest(storeName, flag, md5sum), new RequestListener<GenericResponseV2>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    dialogFragment.dismiss();
                }

                @Override
                public void onRequestSuccess(GenericResponseV2 genericResponseV2) {
                    if (genericResponseV2.getStatus().toUpperCase().equals("OK")) {
                        BusProvider.getInstance().post(new OttoEvents.AppViewRefresh());
                        Toast.makeText(getActivity(), R.string.flag_added, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                    }
                    spiceManager.removeDataFromCache(GetApp.class, md5sum);
                    try {
                        dialogFragment.dismiss();
                    } catch (Exception e) {
                        Logger.e(TAG, e.getMessage());
                    }
                }
            });
        }

        @Override
        public void voteComment(int commentId, AddApkCommentVoteRequest.CommentVote vote) {
            RequestListener<GenericResponseV2> commentRequestListener = new AlmostGenericResponseV2RequestListener() {
                @Override
                public void CaseOK() {
                    Toast.makeText(getActivity(), getString(R.string.vote_submitted), Toast.LENGTH_LONG).show();
                }
            };

            AptoideUtils.VoteUtils.voteComment(
                    spiceManager,
                    commentId,
                    storeName,
                    SecurePreferences.getInstance().getString("token", "empty"),
                    commentRequestListener,
                    vote);
        }

        private void showRootDialog() {
            if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).contains("allowRoot") && !Aptoide.IS_SYSTEM) {
                final Handler handler = new Handler(Looper.getMainLooper());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (AptoideUtils.AppUtils.isRooted()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // in a try because we might be already out of this Activity
                                    try {
                                        AptoideDialog.allowRootDialog().show(getChildFragmentManager(), "allowRoot");
                                    } catch (Exception e) {
                                        Logger.printException(e);
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        }


        private void showDialogIfComingFromBrowser() {
            if (getActivity().getIntent().getBooleanExtra(Constants.FROM_MY_APP_KEY, false) && !isPaidApp()) {
                final InstallListener installListener = new InstallListener(iconUrl, appName, versionName, packageName, md5sum, isPaidApp());
                DialogFragment dialog = AptoideDialog.myAppInstall(appName, installListener, onDismissListener);
                AptoideDialog.showDialogAllowingStateLoss(dialog, getChildFragmentManager(),"myApp");
            }
        }

        private void requestComments(boolean useCache) {
            long cacheExpiryDuration = (useCache || !forceReload) ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

            // call list apkcomments, which need: storeName, packageName, versionName
            // http://webservices.aptoide.com/webservices/2/listApkComments/apps/com.android.vending/5.12.9/json
            AllCommentsRequest request = new AllCommentsRequest();

            request.storeName = storeName;
            request.versionName = versionName;
            request.packageName = packageName;
            request.filters = Aptoide.filters;
            request.limit = MAX_COMMENTS_REQUEST;
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(getActivity());

            spiceManager.execute(request, "comments" + appName + appId + storeName + versionName + packageName, cacheExpiryDuration, requestCommentListener);
        }

        private void continueLoading() {
            final Intent intent = getActivity().getIntent();
            if (intent.getBooleanExtra(Constants.FROM_RELATED_KEY, false)) {
                appId = intent.getLongExtra(Constants.APP_ID_KEY, 0);
                md5sum = intent.getStringExtra(Constants.MD5SUM_KEY);
                appName = intent.getStringExtra(Constants.APPNAME_KEY);
                iconUrl = intent.getStringExtra(Constants.ICON_KEY);
                downloads = intent.getLongExtra(Constants.DOWNLOADS_KEY, 0);
                rating = intent.getFloatExtra(Constants.RATING_KEY, 0.0f);
                graphic = intent.getStringExtra(Constants.GRAPHIC_KEY);
                fileSize = intent.getLongExtra(Constants.FILESIZE_KEY, 0);
                storeId = intent.getLongExtra(Constants.STOREID_KEY, 0);
                storeName = intent.getStringExtra(Constants.STORENAME_KEY);
                packageName = intent.getStringExtra(Constants.PACKAGENAME_KEY);
                versionName = intent.getStringExtra(Constants.VERSIONNAME_KEY);

                executeSpiceRequestWithAppId(appId, storeName, packageName);
            } else if (intent.getBooleanExtra(Constants.FROM_SPONSORED_KEY, false)) {
                fromSponsored = true;
                appId = intent.getLongExtra(Constants.APP_ID_KEY, -1);
                adId = intent.getLongExtra(Constants.AD_ID_KEY, -1);
                packageName = intent.getStringExtra(Constants.PACKAGENAME_KEY);
                storeName = intent.getStringExtra(Constants.STORENAME_KEY);
                appName = intent.getStringExtra(Constants.APPNAME_KEY);
                String location = intent.getStringExtra(Constants.LOCATION_KEY);
                String keyword = intent.getStringExtra(Constants.KEYWORD_KEY);
                String cpc = intent.getStringExtra(Constants.CPC_KEY);
                String cpi = intent.getStringExtra(Constants.CPI_KEY);
                cpd = intent.getStringExtra(Constants.CPD_KEY);
                String whereFrom = intent.getStringExtra(Constants.WHERE_FROM_KEY);
                download_from = intent.getStringExtra(Constants.DOWNLOAD_FROM_KEY);

                executeSpiceRequestWithAppId(appId, storeName, packageName);
                AptoideUtils.AdNetworks.knock(cpc);

                if (intent.hasExtra("partnerExtra")) {
                    final String clickUrl = intent.getBundleExtra("partnerExtra").getString("partnerClickUrl");
                    ReferrerUtils.extractReferrer(packageName, appId, adId, -1, clickUrl, spiceManager, null, ReferrerUtils.RETRIES);
                }
            } else if (intent.getBooleanExtra(Constants.ROLLBACK_FROM_KEY, false)) {
                md5sum = intent.getStringExtra(Constants.MD5SUM_KEY);

                executeSpiceRequestWithMd5(md5sum, storeName);
            } else if (intent.getBooleanExtra(Constants.FROM_TIMELINE_KEY, false)) {  // From Timeline
                appName = intent.getStringExtra(Constants.APPNAME_KEY);
                storeName = intent.getStringExtra(Constants.STORENAME_KEY);
                md5sum = intent.getStringExtra(Constants.MD5SUM_KEY);

                executeSpiceRequestWithMd5(md5sum, storeName);
            } else if (intent.getBooleanExtra(Constants.FROM_COMMENT_KEY, false)) {
                appId = intent.getLongExtra(Constants.APP_ID_KEY, 0);
                appName = intent.getStringExtra(Constants.APPNAME_KEY);

                executeSpiceRequestWithAppId(appId, storeName, null);
            } else if (intent.getBooleanExtra("fromApkInstaller", false)) {
                autoDownload = true;
                appId = intent.getLongExtra(Constants.APP_ID_KEY, 0);
                appName = intent.getStringExtra(Constants.APPNAME_KEY);
                storeName = intent.getStringExtra(Constants.STORENAME_KEY);
                packageName = intent.getStringExtra(Constants.PACKAGENAME_KEY);

                executeSpiceRequestWithAppId(appId, storeName, packageName);
            } else if (intent.getBooleanExtra(Constants.GET_BACKUP_APPS_KEY, false)) {
                packageName = Defaults.BACKUP_APPS_PACKAGE;
                appName = Defaults.BACKUP_APPS_NAME;

                executeSpiceRequestWithPackageName(packageName, appName);
            } else if (intent.getBooleanExtra(Constants.FROM_MY_APP_KEY, false)) { // from browser
                appId = intent.getLongExtra(Constants.APP_ID_KEY, 0);

                executeSpiceRequestWithAppId(appId, storeName, packageName);
            } else if (intent.getBooleanExtra(Constants.SEARCH_FROM_KEY, false)) { // from search inside app
                appName = intent.getStringExtra(Constants.APPNAME_KEY);
                md5sum = intent.getStringExtra(Constants.MD5SUM_KEY);
                storeName = intent.getStringExtra(Constants.STORENAME_KEY);

                executeSpiceRequestWithMd5(md5sum, storeName);
            } else if (intent.getBooleanExtra(Constants.UPDATE_FROM_KEY, false)) {
                iconUrl = intent.getStringExtra(Constants.ICON_KEY);
                appName = intent.getStringExtra(Constants.APPNAME_KEY);
                versionName = intent.getStringExtra(Constants.VERSIONNAME_KEY);
                packageName = intent.getStringExtra(Constants.PACKAGENAME_KEY);
                md5sum = intent.getStringExtra(Constants.MD5SUM_KEY);
                storeName = intent.getStringExtra(Constants.STORENAME_KEY);

                executeSpiceRequestWithMd5(md5sum, storeName);
            } else if (intent.getBooleanExtra(Constants.MARKET_INTENT, false)) {
                packageName = intent.getStringExtra(Constants.PACKAGENAME_KEY);

                executeSpiceRequestWithPackageName(packageName, null);
            }


            mCollapsingToolbarLayout.setTitle(appName);
        }

        private void executeSpiceRequestWithAppId(long appId, @Nullable String storeName, String packageName) {
            long cacheExpiryDuration = forceReload ? DurationInMillis.ALWAYS_EXPIRED : DurationInMillis.ONE_HOUR;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildGetAppRequestFromAppId(appId, storeName, packageName),
                    appId + bucketSize,
                    cacheExpiryDuration,
                    listener);
        }

        private void executeSpiceRequestWithMd5(String md5, String storeName) {
            long cacheExpiryDuration = forceReload ? DurationInMillis.ALWAYS_EXPIRED : DurationInMillis.ONE_HOUR;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildGetAppRequestFromMd5(md5, storeName),
                    md5 + bucketSize,
                    cacheExpiryDuration,
                    listener);
        }

        private void executeSpiceRequestWithPackageName(String packageName, String appName) {
            long cacheExpiryDuration = forceReload ? DurationInMillis.ALWAYS_EXPIRED : DurationInMillis.ONE_HOUR;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildGetAppRequestFromPackageName(packageName),
                    packageName + appName + bucketSize,
                    cacheExpiryDuration,
                    listener);
        }

        private void executeSpiceRequestWithAppRate(long appId, float rating) {
            mRatingBar.setIsIndicator(true);
            spiceManager.execute(AptoideUtils.RepoUtils.buildRateRequest(appId, rating), new RequestListener<RateApp>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Toast.makeText(getActivity(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                    mRatingBar.setIsIndicator(false);
                }

                @Override
                public void onRequestSuccess(RateApp rateApp) {
                    mRatingBar.setIsIndicator(false);
                    Toast.makeText(getActivity(), getString(R.string.appview_rate_Success), Toast.LENGTH_SHORT).show();
                }
            });
        }

        class RatingBarClickListener implements RatingBar.OnRatingBarChangeListener {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                final Activity activity = getActivity();
                if (AptoideUtils.AccountUtils.isLoggedIn(activity)) {
                    executeSpiceRequestWithAppRate(appId, rating);
                } else {
                    Intent intent = new Intent(activity, MyAccountActivity.class);
                    activity.startActivity(intent);
                }
            }

        }

        public class DowngradeListener implements View.OnClickListener {

            private String icon;
            private String name;
            private String versionName;
            private String downgradeVersion;
            private String package_name;
            public DowngradeListener(String icon, String name, String versionName, String downgradeVersion, String package_name) {
                this.icon = icon;
                this.name = name;
                this.versionName = versionName;
                this.downgradeVersion = downgradeVersion;
                this.package_name = package_name;
            }

            @Override
            public void onClick(View v) {
                reloadButtons = true;
                Fragment downgrade = new UninstallRetainFragment();
                Bundle args = new Bundle();
                args.putString("name", name);
                args.putString("package", package_name);
                args.putString("version", versionName);
                args.putString("downgradeVersion", downgradeVersion);
                args.putString("icon", icon);
                downgrade.setArguments(args);

                getFragmentManager().beginTransaction().add(downgrade, "downgrade").commit();
            }
        }

        public class InstallListener implements View.OnClickListener, DialogInterface.OnClickListener {

            protected String icon;
            protected String name;
            protected String versionName;
            protected String package_name;
            protected String md5;
            private long downloadId;
            private boolean paid;
            public InstallListener(String icon, String name, String versionName, String packageName, String md5, boolean paid) {
                this.icon = icon;
                this.name = name;
                this.versionName = versionName;
                this.package_name = packageName;
                this.md5 = md5;
                this.paid = paid;
                this.downloadId = md5.hashCode();
            }

            protected Download makeDownLoad() {
                Download download = new Download();

                showRootDialog();

                download.setId(this.downloadId);
                download.setName(this.name);
                download.setVersion(this.versionName);
                download.setIcon(this.icon);
                download.setPackageName(this.package_name);
                download.setMd5(this.md5);
                download.setPaid(this.paid);
                if (!isUpdate) download.setCpiUrl(getActivity().getIntent().getStringExtra("cpi"));
                return download;
            }

            @Override
            public void onClick(View v) {
                reloadButtons = true;
                if (appSuggested != null && appSuggested.getInfo().getCpc_url() != null) {
                    AptoideUtils.AdNetworks.knock(appSuggested.getInfo().getCpd_url());
                }

                if (cpd != null) {
                    AptoideUtils.AdNetworks.knock(cpd);
                }

                download();

                Analytics.ClickedOnInstallButton.clicked(package_name, developer);
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // shouldnt check if which is positive?
                download();
            }
        }
    }
}
