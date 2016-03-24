package com.aptoide.amethyst.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.LoginActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.SignUpActivity;
import com.aptoide.dataprovider.webservices.models.Defaults;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 06-09-2013
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class AptoideConfiguration {

    public static final String LOGIN_USER_LOGIN = "usernameLogin";
    private static final String PREF_PATH_CACHE_ICONS = "dev_mode_path_cache_icons";
    private static final String PREF_DEFAULT_STORE = "dev_mode_featured_store";
    public static final String REPOS_SYNCED = "REPOS_SYNCED";
    private static final String PREF_PATH_CACHE = "dev_mode_path_cache";
    private static String MARKETNAME = "Aptoide";
    private static final String PREF_URI_SEARCH = "dev_mode_uri_search";
    private static final String PREF_AUTO_UPDATE_URL = "dev_mode_auto_update_url";
    private static final String PREF_ALWAYS_UPDATE = "dev_mode_always_update";

    public static final String PREF_PATH_CACHE_APK = "dev_mode_path_cache_apks";
    private static Context context = Aptoide.getContext();
    private static SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
    private Class<?> appViewActivityClass  ;
    private Class<?> moreActivityClass;
    private Class settingsActivityClass;


    public String getPathCacheIcons() {

        String pathIcons = sPref.getString(PREF_PATH_CACHE_ICONS, Defaults.PATH_CACHE_ICONS);

        new File(pathIcons).mkdirs();

        return pathIcons;
    }

    public void resetPathCacheApks() {
        sPref.edit().remove(PREF_PATH_CACHE_APK).commit();
    }

    public String getPathCacheApks() {
        String path = sPref.getString(PREF_PATH_CACHE_APK, Defaults.PATH_CACHE_APKS);

        new File(path).mkdirs();

        return path;
    }

    public String getAccountType() {
        return AccountGeneral.ACCOUNT_TYPE;
    }

    //FIXME verificar valor de retorno
    public String getTimelineActivitySyncAdapterAuthority() {
        return "cm.aptoide.pt.TimelineActivity";
    }
    //FIXME verificar valor de retorno
    public String getTimeLinePostsSyncAdapterAuthority() {
        return "cm.aptoide.pt.TimelinePosts";
    }

//    public Class<?> getAppViewActivityClass() {
//        return appViewActivityClass;
//    }

    public Class<?> getMoreActivityClass() {
        return moreActivityClass;
    }

    public Class getSettingsActivityClass() {
        return settingsActivityClass;
    }

    public String getUriSearch() {
        return sPref.getString(PREF_URI_SEARCH, Defaults.URI_SEARCH_BAZAAR);
    }

    /**
     * TODO: refactor via injection dependency, MainActivity cannot be safely moved to amethyst
     */
//    public Class getStartActivityClass() {
//        return com.aptoide.amethyst.MainActivity.class;
//    }
//    public Class getIABPurchaseActivityClass(){
//        return IABPurchaseActivity.class;
//    }
//    public Class getAppViewActivityClass() {
//        return com.aptoide.amethyst.AppViewActivity.class;
//    }

    /**
     * Account Configurations
     */
    public class AccountGeneral {


        /**
         * Account type id
         */
        public static final String ACCOUNT_TYPE = "cm.aptoide.pt";

        /**
         * Auth token types
         */
        public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
        public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Aptoide account";

        public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
        public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Aptoide account";
    }

    public Class getSignUpActivityClass() {
        return SignUpActivity.class;
    }

    public String getExtraId(){
        return "";
    }

    public String getMarketName() {
        return MARKETNAME;
    }

    public String getUpdatesSyncAdapterAuthority(){
        return Aptoide.getContext().getPackageName() + ".UpdatesProvider";
    }

    public String getSearchAuthority(){
//        return Aptoide.getContext().getPackageName() + ".SuggestionProvider";
        return "com.aptoide.amethyst.SuggestionProvider";
    }

    public String getAutoUpdatesSyncAdapterAuthority(){
        return Aptoide.getContext().getPackageName() + ".AutoUpdateProvider";
//        public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Aptoide account";
    }
    public String getDefaultStore() {
        return sPref.getString(PREF_DEFAULT_STORE, Defaults.DEFAULT_STORE_NAME);
    }
    public String getPathCache() {
        String cache = sPref.getString(PREF_PATH_CACHE, Defaults.PATH_CACHE);
        new File(cache).mkdirs();
        return cache;
    }

    public String getAutoUpdateUrl() {
        return sPref.getString(PREF_AUTO_UPDATE_URL, Defaults.AUTO_UPDATE_URL);
    }

    public boolean isAlwaysUpdate() {
        return sPref.getBoolean(PREF_ALWAYS_UPDATE, Defaults.ALWAYS_UPDATE);
    }

    public int getIcon() {
        return R.drawable.icon_brand_aptoide;
    }

    public String getTrackUrl() {
        return "cm.aptoide.pt.PushNotificationTrackUrl";
    }

    public String getAction() {
        return "cm.aptoide.pt.PushNotification";
    }

    public String getActionFirstTime() {
        return "cm.aptoide.pt.PushNotificationFirstTime";
    }

}
