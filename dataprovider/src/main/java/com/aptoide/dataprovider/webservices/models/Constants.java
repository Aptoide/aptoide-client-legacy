package com.aptoide.dataprovider.webservices.models;

/**
 * Created by rmateus on 04/06/15.
 */
public class Constants {

    public static final String DOWNLOAD_FROM_KEY = "download_from";
    public static final String ROLLBACK_FROM_KEY = "rollback_from";
    public static final String GET_BACKUP_APPS_KEY = "getBackupApps";
    public static final String UPDATE_FROM_KEY = "update_from";
    public static final String FROM_RELATED_KEY = "fromRelated";
    public static final String FROM_SPONSORED_KEY = "fromSponsored";
    public static final String WHERE_FROM_KEY = "whereFrom";
    public static final String SEARCH_FROM_KEY = "search_result";
    public static final String FROM_COMMENT_KEY = "fromComment";
    public static final String FROM_MY_APP_KEY = "fromMyapp";
    public static final String FROM_TIMELINE_KEY = "fromTimeline";
    public static final String FROM_APKFY_KEY = "Started_From_Apkfy";

    public static final String STORE_SUBSCRIBED_KEY = "storeSubscribed";

    public static final String APPNAME_KEY = "appName";
    public static final String ICON_KEY = "icon";
    public static final String DOWNLOADS_KEY = "downloads";
    public static final String RATING_KEY = "rating";
    public static final String GRAPHIC_KEY = "featureGraphic";
    public static final String MD5SUM_KEY = "md5sum";
    public static final String FILESIZE_KEY = "fileSize";
    public static final String STOREID_KEY = "storeId";
    public static final String STORENAME_KEY = "storeName";
    public static final String PACKAGENAME_KEY = "packageName";
    public static final String VERSIONNAME_KEY = "versionName";
    public static final String APP_ID_KEY = "appId";
    public static final String DOWNLOAD_ID_KEY = "downloadId";
    public static final String THEME_KEY = "theme";
    public static final String STOREAVATAR_KEY = "storeAvatar";
    public static final String LOCATION_KEY = "location";
    public static final String KEYWORD_KEY = "keyword";
    public static final String CPC_KEY = "cpc";
    public static final String CPI_KEY = "cpi";
    public static final String CPD_KEY = "cpd";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String MARKET_INTENT = "market_intent";

    public static final String LOCAL_TOP_APPS_VALUE = "local_top_apps";
    public static final String APPS_LIST = "apps_list";

    public final static String NEW_REPO_EXTRA = "newrepo";
    public final static int NEW_REPO_FLAG = 12345;
    public final static String USER_QUEUE_NAME = "queueName";
    public final static String USER_AVATAR = "useravatar";
    public final static String USER_REPO = "userRepo";
    public final static String USER_NAME = "username";
    public final static String USER_LOGIN_TYPE = "loginType";
    public final static String STORE_PASSWORD = "password";

    /********* Action Events Constants ****************/
    public static final String EVENT_ACTION_URL = "eventActionUrl";
    public static final String EVENT_NAME = "eventName";
    public static final String EVENT_TYPE = "eventType";
    public static final String EVENT_LABEL = "label";

    /********* Review Bundle Constants ****************/
    public static final String HOMEPAGE_KEY = "homepage";

    /********* Comments Bundle Constants ****************/
    public static final String COMMENT_ID_KEY = "commentId";
    public static final String REPLAYING_TO_KEY = "replyingTo";


    /** possible usages found throughout v6 */
//    i.putExtra("fromMyapp", true);
//    i.putExtra("fromRelated", true);
//    i.putExtra("fromSponsored", true);
//    i.putExtra("item", true);
//    i.putExtra("developer", true);
//    i.putExtra("version", true);

//    i.putExtra("download_from", "app_view_related_apps");
//    i.putExtra("download_from", "app_view_more_multiversion");
//    i.putExtra("download_from", "app_view_more_from_publisher");
//    i.putExtra("download_from", "editors_choice");
//    i.putExtra("download_from", "featured");
//    i.putExtra("download_from", "home");
//    i.putExtra("download_from", "home_page");
//    i.putExtra("download_from", "latest_comments");
//    i.putExtra("download_from", "latest_likes");
//    i.putExtra("download_from", "market_intent");
//    i.putExtra("download_from", "more_featured_editors_choice");
//    i.putExtra("download_from", "my_app_with_cpi");
//    i.putExtra("download_from", "my_app");
//    i.putExtra("download_from", "recommended_apps");
//    i.putExtra("download_from", "related_apps");
//    i.putExtra("download_from", "rollback");
//    i.putExtra("download_from", "search_result");
//    i.putExtra("download_from", "search_results");
//    i.putExtra("download_from", "sponsored");
//    i.putExtra("download_from", "store");
//    i.putExtra("download_from", "timeline");
//    i.putExtra("download_from", "updates");
//    i.putExtra("download_from", "webinstall");

//    i.putExtra("whereFrom", "editorsChoice");
//    i.putExtra("whereFrom", "sponsored");

//    i.putExtra("id", id);
//    i.putExtra("cpi", appSuggested.getInfo().getCpi_url());
//    i.putExtra("cpc", appSuggested.getInfo().getCpc_url());
//    i.putExtra("md5sum", md5sum);
//    i.putExtra("search", param);
//    i.putExtra("appName", ((AppViewActivity) getActivity()).getName());
//    i.putExtra("location", "homepage");
//    i.putExtra("keyword", "__NULL__");
//    i.putExtra("repoName", appSuggested.getData().getRepo());
//    i.putExtra("packageName", appSuggested.getData().getPackageName());
//    i.putExtra("versionCode", ((AppViewActivity) getActivity()).getVersionCode());
//    i.putExtra("appNameplusversion", item.getName());

//    if (appSuggested.getPartner() != null) {
//        Bundle bundle = new Bundle();
//
//        bundle.putString("partnerType", appSuggested.getPartner().getPartnerInfo().getName());
//        bundle.putString("partnerClickUrl", appSuggested.getPartner().getPartnerData().getClick_url());
//
//        i.putExtra("partnerExtra", bundle);
//    }

    public static final String WEBINSTALL_HOST = "amqp.webservices.aptoide.com";
    public static final String WEBINSTALL_QUEUE_EXCLUDED =  "wiQueueExcluded";
    public static final long WEBINSTALL_SYNC_POLL_FREQUENCY = 360;

    public static final String LOGIN_USER_LOGIN 	= "usernameLogin";

    /**
     * Store contexts
     */
    public static final String HOME_CONTEXT = "home";
    public static final String STORE_CONTEXT = "store";

    /**
     * Webservices layout types
     */
    public static final String LAYOUT_BRICK = "BRICK";
    public static final String LAYOUT_GRID = "GRID";
    public static final String LAYOUT_LIST = "LIST";

    /**
     * Hidden addult items constants
     */
    public static final String ADULT_DIALOG = "adultDialog";
    public static final String HIDDEN_ADULT_DIALOG = "hidden_adult_dialog";
    public static final String SHOW_ADULT_HIDDEN= "showadulthidden";
    /**
     * Tag for Localytics Screens.
     */
    public static final String LOCALYTICS_TAG = "LOCALYTICS_TAG";

    /**
     * Ads Stuff
     */
    public static final String AD_ID_KEY = "adId";
    public static final String PARTNER_TYPE_KEY = "partnerType";
    public static final String PARTNER_CLICK_URL_KEY = "partnerClickUrl";
    public static final String PARTNER_EXTRA = "partnerExtra";


    /**
     * Permissions for Android M
     */
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Minimum number of characters in a comment
     */
    public static final short MIN_COMMENT_CHARS = 10;
    public static final int GLOBAL_STORE = 0;


    /**
     * Key for matureCheckBox
     */
    public static final String MATURE_CHECK_BOX = "matureChkBox";


}
