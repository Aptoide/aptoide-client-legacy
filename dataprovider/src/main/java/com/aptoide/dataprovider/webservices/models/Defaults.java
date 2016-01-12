package com.aptoide.dataprovider.webservices.models;

import android.os.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 06-09-2013
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class Defaults {

    public static final String BASE_V7_URL = "/ws2.aptoide.com/api/7";

    public static final String PATH_SDCARD       = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String PATH_CACHE        = PATH_SDCARD + "/.aptoide/";
    public static final String PATH_CACHE_ICONS  = PATH_CACHE + "icons/";
    public static final String PATH_CACHE_APKS   = PATH_CACHE + "apks/";
    public static final String DEFAULT_STORE_NAME = "apps";
    public static final long DEFAULT_STORE_ID = 15;

    public static final String URI_SEARCH_BAZAAR = "http://m.aptoide.com/searchview.php?search=";
    public static final String AUTO_UPDATE_URL = "http://imgs.aptoide.com/latest_version_v7.xml";

    public static final String BACKUP_APPS_NAME = "Aptoide Backup Apps";
    public static final String BACKUP_APPS_PACKAGE = "pt.aptoide.backupapps";

    public static final boolean ALWAYS_UPDATE = false;
}