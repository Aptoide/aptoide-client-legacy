package com.aptoide.amethyst.analytics;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.BuildConfig;
import com.aptoide.amethyst.LoginActivity;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.preferences.EnumPreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.flurry.android.FlurryAgent;
import com.localytics.android.Localytics;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;


/**
 * Created by neuro on 07-05-2015.f
 */
public class Analytics {

    // Constantes globais a todos os eventos.
    public static final String ACTION = "Action";
    private static final boolean ACTIVATE = BuildConfig.LOCALYTICS_CONFIGURED;
    private static final int ALL = Integer.MAX_VALUE;
    private static final int LOCALYTICS = 1 << 0;
    private static final int FLURRY = 1 << 1;

    /**
     * Verifica se as flags fornecidas constam em accepted.
     *
     * @param flag     flags fornecidas
     * @param accepted flags aceitáveis
     * @return true caso as flags fornecidas constem em accepted.
     */
    private static boolean checkAcceptability(int flag, int accepted) {
        return (flag & accepted) == accepted;
    }

    private static void track(String event, String key, String attr, int flags) {

        try {
            if (!ACTIVATE)
                return;

            HashMap stringObjectHashMap = new HashMap<>();

            stringObjectHashMap.put(key, attr);

            track(event, stringObjectHashMap, flags);

            Logger.d("Analytics", "Event: " + event + ", Key: " + key + ", attr: " + attr);

        } catch (Exception e) {
            Log.d("Analytics", e.getStackTrace().toString());
        }

    }

    private static void track(String event, HashMap map, int flags) {
        try {
            if (!ACTIVATE)
                return;

            if (checkAcceptability(flags, LOCALYTICS))
                Localytics.tagEvent(event, map);

            if (checkAcceptability(flags, FLURRY))
                FlurryAgent.logEvent(event, map);

            Logger.d("Analytics", "Event: " + event + ", Map: " + map);

        } catch (Exception e) {
            Log.d("Analytics", e.getStackTrace().toString());
        }
    }

    private static void track(String event, int flags) {

        try {
            if (!ACTIVATE)
                return;

            if (checkAcceptability(flags, LOCALYTICS))
                Localytics.tagEvent(event);

            if (checkAcceptability(flags, FLURRY))
                FlurryAgent.logEvent(event);

            Logger.d("Analytics", "Event: " + event);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class Lifecycle {

        public static class Application {
            public static void onCreate(Context context) {

                if (!ACTIVATE)
                    return;

                // Integrate Localytics
                Localytics.integrate(context);

            }
        }

        public static class Activity {

            public static void onCreate(android.app.Activity activity) {

                if (!ACTIVATE)
                    return;

            }

            public static void onDestroy(android.app.Activity activity) {

                if (!ACTIVATE)
                    return;

            }

            public static void onResume(android.app.Activity activity, @Nullable String screenName) {

                if (!ACTIVATE)
                    return;

                // Localytics
                Localytics.onActivityResume(activity);

                if (!AptoideUtils.AccountUtils.isLoggedIn(activity)) {
                    Localytics.setCustomDimension(0, "Not Logged In");
                } else {
                    Localytics.setCustomDimension(0, "Logged In");
                }

                String cpuid = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext())
                        .getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), "NoInfo");

                Localytics.setCustomerId(cpuid);

                if (screenName != null) {
                    Localytics.tagScreen(screenName);
                }

                Localytics.handleTestMode(activity.getIntent());

                Logger.d("Analytics", "Event: CPU_ID: " + cpuid);
                Logger.d("Analytics", "Screen: " + screenName);

            }

            public static void onPause(android.app.Activity activity) {

                if (!ACTIVATE)
                    return;

                // Localytics
                Localytics.onActivityPaused(activity);

            }

            public static void onStart(android.app.Activity activity) {

                if (!ACTIVATE)
                    return;

                FlurryAgent.onStartSession(activity, BuildConfig.FLURRY_KEY);

            }

            public static void onStop(android.app.Activity activity) {

                if (!ACTIVATE)
                    return;

                FlurryAgent.onEndSession(activity);

            }

            public static void onNewIntent(android.app.Activity activity, Intent intent) {
                Localytics.onNewIntent(activity, intent);
            }
        }

    }

    public static class Screens {

        public static void tagScreen(String screenName) {

            if (!ACTIVATE)
                return;

            Logger.d("Analytics", "Localytics: Screens: " + screenName);

            Localytics.tagScreen(screenName);
            Localytics.upload();
        }
    }

    // TODO
    public static class Tutorial {
        public static final String EVENT_NAME = "Tutorial";
        public static final String STEP_ACCOMPLISHED = "Step Accomplished";

        public static void finishedTutorial(int lastFragment) {
            try {
                track(EVENT_NAME, STEP_ACCOMPLISHED, Integer.toString(lastFragment), LOCALYTICS);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Login {
        public static final String EVENT_NAME = "Logged in";

        public static void login(String username, LoginActivity.Mode mode) {

            if (!ACTIVATE)
                return;

            try {
                // TODO: Change to setCustomerId
                Localytics.setCustomerId(username);

                track(EVENT_NAME, ACTION, mode.toString(), LOCALYTICS);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static class UserRegister {

        public static final String EVENT_NAME = "User Registered";

        public static void registered() {
            track(EVENT_NAME, LOCALYTICS);
        }
    }

    // Novos
    public static class Rollback {

        private static final String EVENT_NAME = "Rollback";
        private static final String DOWNGRADED = "Downgraded";
        private static final String CLEAR = "Clear";

        public static void downgraded() {
//            track(EVENT_NAME, ACTION, DOWNGRADED, ALL);
        }

        public static void clear() {
//            track(EVENT_NAME, ACTION, CLEAR, ALL);
        }
    }

    public static class ScheduledDownloads {
        public static final String EVENT_NAME = "Scheduled Downloads";
        private static final String CLICK_ON_INSTALL_SELECTED = "Clicked on Install Selected";
        private static final String CLICK_ON_INVERT_SELECTION = "Clicked on Invert Selection";
        private static final String CLICK_ON_REMOVE_SELECTED = "Clicked on Remove Selected";

        public static void clickOnInstallSelected() {
//            track(EVENT_NAME, ACTION, CLICK_ON_INSTALL_SELECTED, ALL);
        }

        public static void clickOnInvertSelection() {
//            track(EVENT_NAME, ACTION, CLICK_ON_INVERT_SELECTION, ALL);
        }

        public static void clickOnRemoveSelected() {
//            track(EVENT_NAME, ACTION, CLICK_ON_REMOVE_SELECTED, ALL);
        }
    }

    public static class SendFeedback {

        public static final String EVENT_NAME = "Send Feedback";
        private static final String SEND_FEEDBACK = EVENT_NAME;

        public static void sendFeedback() {
            track(EVENT_NAME, ACTION, SEND_FEEDBACK, LOCALYTICS);
        }
    }

    public static class ExcludedUpdates {
        private static final String EVENT_NAME = "Excluded Updates";
        private static final String RESTORE_UPDATES = "Restore Updates";

        public static void restoreUpdates() {
            track(EVENT_NAME, ACTION, RESTORE_UPDATES, LOCALYTICS);
        }
    }

    /**
     * Incomplete
     */
    public static class Settings {

        public static final String EVENT_NAME = "Settings";
        private static final String CHECKED = "Checked";

        public static void onSettingChange(String s) {
//            track(EVENT_NAME, ACTION, s, ALL);
        }

        public static void onSettingChange(String s, boolean checked) {
//            track(EVENT_NAME, ACTION, s, ALL);
//
//            HashMap<String, String> objectObjectHashMap = new HashMap<>();
//            objectObjectHashMap.put(ACTION, s);
//            objectObjectHashMap.put(CHECKED, Boolean.valueOf(checked).toString());
        }
    }

    public static class Facebook {

        public static final String EVENT_NAME = "Facebook";

        public static final String JOIN = "Join";
        public static final String LOGIN = "Login";

        public static void join() {
            track(EVENT_NAME, ACTION, JOIN, LOCALYTICS);
        }

        public static void Login() {
            track(EVENT_NAME, ACTION, LOGIN, LOCALYTICS);
        }
    }

    public static class BackupApps {
        public static final String EVENT_NAME = "Opened Backup Apps";

        public static void open() {
            track(EVENT_NAME, LOCALYTICS);
        }
    }

    public static class Home {
        public static final String EVENT_NAME = "Home";

        public static final String CLICK_ON_MORE_ = "Click on More ";
        public static final String CLICK_ON_EDITORS_CHOISE = "Click On Editor's Choise";
        public static final String CLICK_ON_HIGHLIGHTED = "Click On Highlighted";
        public static final String CLICK_ON_HIGHLIGHTED_MORE = "Click On Highlighted More";
        public static final String CLICK_ON_APPLICATIONS = "Click On Applications";
        public static final String CLICK_ON_APPLICATIONS_MORE = "Click On Applications More";
        public static final String CLICK_ON_GAMES = "Click On Games";
        public static final String CLICK_ON_GAMES_MORE = "Click On Games More";
        public static final String CLICK_ON_REVIEWS = "Click On Reviews";
        public static final String CLICK_ON_REVIEWS_MORE = "Click On Reviews More";
        public static final String CLICK_ON_PUBLISHERS = "Click On Publishers";
        public static final String CLICK_ON_PUBLISHERS_MORE = "Click On Publishers More";
        public static final String CLICK_ON_APPS_ESSENTIALS = "Click On Apps Essentials";
        public static final String CLICK_ON_APPS_FOR_KIDS = "Click On Apps For Kids";

        public static void clickOnHighlighted() {
//            track(EVENT_NAME, ACTION, CLICK_ON_HIGHLIGHTED_MORE, ALL);
        }

        public static void generic(String s) {
            track(EVENT_NAME, ACTION, s, ALL);
        }

//        public static void clickOnApplicationsMore() {
//            track(EVENT_NAME, ACTION, CLICK_ON_APPLICATIONS_MORE, ALL);
//        }

        public static void clickOnReviewsMore() {
//            track(EVENT_NAME, ACTION, CLICK_ON_REVIEWS_MORE, ALL);
        }

        public static void clickOnMoreWidget(String widgetname) {
//            track(EVENT_NAME, ACTION, CLICK_ON_MORE_ + widgetname, ALL);
        }
    }

    public static class AdultContent {

        public static final String EVENT_NAME = "Adult Content";

        public static void lock() {
            track(EVENT_NAME, ACTION, "Click on On", LOCALYTICS);
        }

        public static void unlock() {
            track(EVENT_NAME, ACTION, "Click on Off", LOCALYTICS);
        }
    }

    public static class Top {
        public static final String EVENT_NAME = "Top";

        public static final String CLICK_ON_LOCAL_TOP_APPS_MORE = "Click on Local Top Apps More";
        public static final String CLICK_ON_TOP_APPLICATIONS_MORE = "Click on Top Applications More";
        public static final String CLICK_ON_LOCAL_TOP_STORES_MORE = "Click on Local Top Stores More";

    }

    public static class Stores {
        public static final String EVENT_NAME = "Stores";

        public static final String STORE_NAME = "Store Name";

        public static void enter(String storeName) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(ACTION, "Enter");
                map.put(STORE_NAME, storeName);

                track(EVENT_NAME, map, LOCALYTICS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void subscribe(String storeName) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(ACTION, "Subscribe");
                map.put(STORE_NAME, storeName);

                track(EVENT_NAME, map, LOCALYTICS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class Updates {
        public static final String EVENT_NAME = "Updates";

        public static void update() {
            track(EVENT_NAME, ACTION, "Update", LOCALYTICS);
        }

        public static void updateAll() {
            track(EVENT_NAME, ACTION, "Update All", LOCALYTICS);
        }

        public static void createReview() {
            track(EVENT_NAME, ACTION, "Create Review", LOCALYTICS);
        }
    }

    // TODO: Não está implementado na v6
    public static class DownloadManager {
        public static final String EVENT_NAME = "Download Manager";

        public static void clearDownloadComplete() {
//            track(EVENT_NAME, ACTION, "Clear download complete", ALL);
        }

        public static void clickDownloadComplete() {
//            track(EVENT_NAME, ACTION, "Click download complete", ALL);
        }

        public static void clearTopMenu() {
//            track(EVENT_NAME, ACTION, "Clear topmenu", ALL);
        }
    }

    public static class Search {
        public static final String EVENT_NAME_SEARCH_TERM = "Search Term";
        public static final String EVENT_NAME_POSITION = "Search Position";
        public static final String EVENT_NAME_SEARCH_OTHER_STORES = "Search Other Stores";
        public static final String EVENT_NAME_NO_SEARCH_RESULT = "No Search Result";

        public static final String SEARCH_POSITION = "Search Position";
        public static final String SUBSCRIBED = "Subscribed";
        public static final String REPO = "Repo";

        public static final String QUERY = "Query";
        public static final String INSIDE_STORE = "Inside Store";

        public static void searchPosition(int position, boolean subscribed, String repo) {

            HashMap<String, String> map = new HashMap<>();
            map.put(SEARCH_POSITION, String.valueOf(position));
            map.put(SUBSCRIBED, String.valueOf(subscribed));
            map.put(REPO, repo);

            track(EVENT_NAME_POSITION, map, LOCALYTICS);
        }

        public static void searchTerm(String query, String repo) {

            HashMap<String, String> map = new HashMap<>();
            map.put(QUERY, query);

            if (repo != null && !repo.isEmpty()) {
                map.put(INSIDE_STORE, String.valueOf(true));
                map.put(REPO, repo);
            } else {
                map.put(INSIDE_STORE, String.valueOf(false));
            }

            track(EVENT_NAME_SEARCH_TERM, map, LOCALYTICS);
        }

        public static void noSearchResultEvent(String query) {
            HashMap<String, String> map = new HashMap<>();
            map.put(QUERY, query);
            track(EVENT_NAME_NO_SEARCH_RESULT, map, ALL);
        }

        public static void searchOtherStores() {
            track(EVENT_NAME_SEARCH_OTHER_STORES, LOCALYTICS);
        }
    }

    public static class ApplicationInstall {
        public static final String EVENT_NAME = "Application Install";
        public static final String TRUSTED = "Trusted_Badge";
        private static final String TYPE = "Type";
        private static final String PACKAGE_NAME = "Package Name";
        private static final String REFERRED = "Referred";
        private static final String REPLACED = "Replaced";
        private static final String INSTALLED = "Installed";
        private static final String DOWNGRADED_ROLLBACK = "Downgraded Rollback";

        private static void innerTrack(String packageName, String type, @Nullable Boolean referred, int flags) {
            try {
                HashMap<String, String> stringObjectHashMap = new HashMap<>();

                stringObjectHashMap.put(TYPE, type);
                stringObjectHashMap.put(PACKAGE_NAME, packageName);

                if (referred != null) {
                    stringObjectHashMap.put(REFERRED, referred.toString());
                } else {
                    stringObjectHashMap.put(REFERRED, Boolean.toString(false));
                }

                track(EVENT_NAME, stringObjectHashMap, flags);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void innerTrack(String packageName, String type, @Nullable Boolean referred, int flags, String isTrusted) {
            try {
                HashMap<String, String> stringObjectHashMap = new HashMap<>();

                stringObjectHashMap.put(TYPE, type);
                stringObjectHashMap.put(PACKAGE_NAME, packageName);

                if (referred != null) {
                    stringObjectHashMap.put(REFERRED, referred.toString());
                } else {
                    stringObjectHashMap.put(REFERRED, Boolean.toString(false));
                }
                stringObjectHashMap.put(TRUSTED, isTrusted);

                track(EVENT_NAME, stringObjectHashMap, flags);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void installed(String packageName, boolean referred, String isTrusted) {
            innerTrack(packageName, INSTALLED, referred, ALL, isTrusted);
        }

        public static void replaced(String packageName) {
            innerTrack(packageName, REPLACED, null, ALL);
        }

        public static void downgraded(String packageName) {
            innerTrack(packageName, DOWNGRADED_ROLLBACK, null, ALL);
        }
    }

    public static class ApplicationLaunch {

        public static final String EVENT_NAME = "Application Launch (Aptoide Launch)";
        public static final String SOURCE = "Source";
        public static final String LAUNCHER = "Launcher";
        public static final String WEBSITE = "Website";
        public static final String NEW_UPDATES_NOTIFICATION = "New Updates Available";
        public static final String DOWNLOADING_UPDATES = "Downloading Updates";
        public static final String TIMELINE_NOTIFICATION = "Timeline Notification";
        public static final String NEW_REPO = "New Repository";
        public static final String URI = "Uri";

        public static void launcher() {
            track(EVENT_NAME, SOURCE, LAUNCHER, LOCALYTICS);

        }

        public static void website(String uri) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(SOURCE, WEBSITE);

                if (uri != null) {
                    map.put(URI, uri.substring(0, uri.indexOf(":")));
                }

                track(EVENT_NAME, map, LOCALYTICS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void newUpdatesNotification() {
            track(EVENT_NAME, SOURCE, NEW_UPDATES_NOTIFICATION, LOCALYTICS);
        }

        public static void downloadingUpdates() {
            track(EVENT_NAME, SOURCE, DOWNLOADING_UPDATES, LOCALYTICS);
        }

        public static void timelineNotification() {
            track(EVENT_NAME, SOURCE, TIMELINE_NOTIFICATION, LOCALYTICS);
        }

        public static void newRepo() {
            track(EVENT_NAME, SOURCE, NEW_REPO, LOCALYTICS);
        }
    }

    public static class ClickedOnInstallButton {
        public static final String EVENT_NAME = "Clicked on Install Button";

        private static final String CLICKED_ON_INSTALL_BUTTON = "Clicked on install button";

        private static final String APPLICATION_NAME = "Application Name";
        private static final String APPLICATION_PUBLISHER = "Application Publisher";
        public static final String WARNING_POPUP = "Warning";

//        public static void clicked(GetApkInfoJson getApkInfoJson) {
//            try {
//                HashMap<String, String> map = new HashMap<>();
//
//                map.put(APPLICATION_NAME, getApkInfoJson.getApk().packageName);
//                map.put(APPLICATION_PUBLISHER, getApkInfoJson.getMeta().getDeveloper().info.name);
//
//                track(CLICKED_ON_INSTALL_BUTTON, map, ALL);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        public static void clicked(String packageName, String developer, boolean isWarningPopupShowing) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(APPLICATION_NAME, packageName);
                map.put(APPLICATION_PUBLISHER, developer);
                map.put(WARNING_POPUP, Boolean.toString(isWarningPopupShowing));

                track(CLICKED_ON_INSTALL_BUTTON, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class DownloadComplete {
        public static final String EVENT_NAME = "Download Complete";

        private static final String APPLICATION_NAME = "Application Name";
        private static final String PACKAGE_NAME = "Package Name";
        private static final String TRUSTED_BADGE = "Trusted Badge";

        public static void downloadComplete(Download download) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(APPLICATION_NAME, download.getName());
                map.put(PACKAGE_NAME, download.getPackageName());
                map.put(TRUSTED_BADGE, download.getTrusted());

                track(EVENT_NAME, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class SourceDownloadCompleted {
        private static final String PACKAGE_NAME = "Package Name";
        public static final String EVENT_NAME = "_Download_Complete";

        public static void downloadCompleteWithSource(Download download) {
            HashMap<String, String> map = new HashMap<>();
            map.put(PACKAGE_NAME, download.getPackageName());
            track(download.getInstallationSource().replace(" More","")+EVENT_NAME, map, FLURRY);
        }
    }

    public static class SocialTimeline {

        public static final String EVENT_NAME = "Social Timeline";

        public static final String eventName = "Social Timeline";
        public static final String APPLICATION_NAME = "Application Name";
        public static final String action = "Action";
        public static final String like = "Like";
        public static final String DISLIKE = "Dislike";
        public static final String comment = "Comment";
        public static final String login = "Login";
        public static final String share = "Share";

        public static void like(String appName) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(action, like);
                map.put(APPLICATION_NAME, appName);

                track(eventName, map, LOCALYTICS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void dislike(String appName) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(action, DISLIKE);
                map.put(APPLICATION_NAME, appName);

                track(eventName, map, LOCALYTICS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void comment(String appName) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(action, comment);
                map.put(APPLICATION_NAME, appName);

                track(eventName, map, LOCALYTICS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void login() {
            // Declarado mas não utilizado, apenas para localização
            try {
//                track(eventName, action, login, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ViewedApplication {
        public static final String EVENT_NAME = "Viewed Application";

        private static final String VIEWED_APPLICATION = "Viewed Application";

        private static final String APPLICATION_NAME = "Application Name";
        private static final String TYPE = "Type";
        private static final String APPLICATION_PUBLISHER = "Application Publisher";
        private static final String SOURCE = "Source";
        private static final String TRUSTED = "Trusted Badge";
        public static final String ANTIVIRUS_POPUP_KEY = "Pop-Up";

        public static void view(String packageName, String developer, String download_from, String isTrusted, String popup) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(APPLICATION_NAME, packageName);
                map.put(APPLICATION_PUBLISHER, developer);
                map.put(SOURCE, download_from);
                map.put(TRUSTED, isTrusted);
                map.put(ANTIVIRUS_POPUP_KEY, popup);

                track(VIEWED_APPLICATION, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Dimenstions {

        private static void setDimension(int i, String s) {
            if (!ACTIVATE) {
                return;
            }

            Logger.d("Analytics", "Dimension: " + i + ", Value: " + s);

            Localytics.setCustomDimension(i, s);
        }

        public static void setPartnerDimension(String partner) {
            setDimension(1, partner);
        }

        public static void setVerticalDimension(String verticalName) {
            setDimension(2, verticalName);
        }

        public static void setGmsPresent(boolean b) {
            if (b) {
                setDimension(3, "GMS Present");
            } else {
                setDimension(3, "GMS Not Present");
            }
        }

        public static class Vertical {
            public static final String SMARTPHONE = "smartphone";
        }
    }

    public static class LTV {
        public static void cpi(String packageName, String revenue) {
            ltv("CPI Click", packageName, Double.valueOf(revenue));
        }

        public static void purchasedApp(String packageName, double revenue) {
            ltv("App Purchase", packageName, revenue);
        }

        private static void ltv(String eventName, String packageName, double revenue) {
            if (!ACTIVATE) {
                return;
            }

            try {
                HashMap<String, String> map = new HashMap<>();

                Double revenueDouble = Double.valueOf(revenue);
                Long value = revenueDouble.longValue();

                map.put("packageName", packageName);

                Logger.d("Analytics", "LTV: " + eventName + ": " + packageName + ", " + value);

                Localytics.tagEvent(eventName, map, value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static class AppViewViewedFrom {

        public static final String APP_VIEWED_OPEN_FROM_EVENT_NAME_KEY = "App_Viewed_Open_From";

        public static void appviewViewedFrom(String viewed, String rank, String packageName) {
            if (!TextUtils.isEmpty(viewed)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Source", viewed);
                map.put("Trusted Badge", rank);
                map.put("Package Name", packageName);
                track(APP_VIEWED_OPEN_FROM_EVENT_NAME_KEY, map, FLURRY);
            }
        }
    }

    public static class HomePageEditorsChoice {

        public static final String HOME_PAGE_EDITORS_CHOICE = "Home_Page_Editors_Choice";

        public static void sendHomePageEdiorsChoiceEvent(int position, String appName, boolean isHome) {
            HashMap<String, String> map = new HashMap<>();
            map.put("Application Name", appName);
            if (isHome) {
                map.put("Search Position", "More_" + Integer.valueOf(position).toString());
            } else {
                map.put("Search Position", "Home_" + Integer.valueOf(position).toString());
            }
            track(HOME_PAGE_EDITORS_CHOICE, map, FLURRY);
        }
    }

    public static class HomePageBundles {

        public static final String HOME_PAGE_BUNDLES = "Home_Page_Bundles";

        public static void sendHomePageBundleEvent(String category) {
            sendHomePageBundleEvent(category, "n/a");
        }

        public static void sendHomePageBundleEvent(String category, String subCategory) {
            HashMap<String, String> map = new HashMap<>();
            map.put("Category", category);
            map.put("Sub-Category", subCategory);
            track(HOME_PAGE_BUNDLES, map, FLURRY);
        }

    }

    public static class ClickonWarningPopupButtons {


        public static final String CLICK_ON_WARNING_POP_UP_BUTTONS = "Click on Warning Pop up Buttons";
        public static final String ACTION = "Action";

        public static void sendClickonWarningPopupButtons(String action) {
            HashMap<String, String> map = new HashMap<>();
            map.put(ACTION, action);
            track(CLICK_ON_WARNING_POP_UP_BUTTONS, map, ALL);
        }
    }

    public static class ClickOnAdultSwitch {

        public static final String EVENT_NAME = "Click on Adult Content";
        public static final String ACTION = "Action";

        public static void sendSwitchPressedEvent(boolean isChecked) {
            HashMap<String, String> map = new HashMap<>();
            map.put(ACTION, Boolean.toString(isChecked));
            track(EVENT_NAME, map, FLURRY);
        }
    }

    public static class AppFlaggedEvent {

        public static final String EVENT_NAME = "Flag Pop up";
        public static final String ACTION = "Flag";

        public static void sendSwitchPressedEvent(String flag) {
            HashMap<String, String> map = new HashMap<>();
            map.put(ACTION, flag);
            track(EVENT_NAME, map, FLURRY);
        }
    }

    public static class OpenSubscribeStoreFromAppviewEvent {

        public static final String EVENT_NAME = "Viewed_App_Click_On_Store_Banner";
        public static final String ACTION = "Action";

        public static void sendOpenSubscribeStoreFromAppviewEvent(String action) {
            HashMap<String, String> map = new HashMap<>();
            map.put(ACTION, action);
            track(EVENT_NAME, map, FLURRY);
        }

    }
}
