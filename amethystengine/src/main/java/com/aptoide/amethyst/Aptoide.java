package com.aptoide.amethyst;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.database.SQLiteDatabaseHelper;
import com.aptoide.amethyst.preferences.ManagerPreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;
import roboguice.util.temp.Ln;

/**
 * Created by rmateus on 28/05/15.
 */
public class Aptoide extends Application {



    public static boolean IS_SYSTEM;
    public static boolean DEBUG_MODE = Log.isLoggable("APTOIDE", Log.DEBUG);

    private static Context context;
    private static SQLiteDatabase db;
    private static AptoideConfiguration configuration = null;
    private static boolean webInstallServiceRunning;

    private static AptoideThemePicker themePicker;

    public static Context getContext() {
        return context;
    }

    @Nullable
    public static String filters;

    public static AptoideThemePicker getThemePicker() {
        return themePicker;
    }

    public static void setThemePicker(AptoideThemePicker themePicker) {
        Aptoide.themePicker = themePicker;
    }

    // See SharedPreferences#registerOnSharedPreferenceChangeListener
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private void setAdvertisingIdClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String aaid = "";
                if (AptoideUtils.GoogleServices.checkGooglePlayServices(context)) {
                    try {
                        aaid = AdvertisingIdClient.getAdvertisingIdInfo(Aptoide.this).getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    byte[] data = new byte[16];
                    String deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    SecureRandom secureRandom = new SecureRandom();
                    secureRandom.setSeed(deviceId.hashCode());
                    secureRandom.nextBytes(data);
                    aaid = UUID.nameUUIDFromBytes(data).toString();
                }

                AptoideUtils.getSharedPreferences().edit().putString("advertisingIdClient", aaid).apply();
            }
        }).start();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Analytics.Lifecycle.Application.onCreate(this);

        setAdvertisingIdClient();

        context = this;
        db = SQLiteDatabaseHelper.getInstance(this).getReadableDatabase();
        filters = AptoideUtils.HWSpecifications.filters(this);

        ManagerPreferences.getInstance(this) //inits the ManagerPreferences
                .preferences
                .registerOnSharedPreferenceChangeListener(
                        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                            @Override
                            public void onSharedPreferenceChanged(
                                    final SharedPreferences sharedPreferences, final String key) {
                                if (TextUtils.equals(key, "hwspecsChkBox")) {
                                    filters = AptoideUtils.HWSpecifications.filters(Aptoide.this);
                                }
                            }
                        });


        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(!BuildConfig.FABRIC_CONFIGURED).build())
                .build();
        Fabric.with(this, crashlyticsKit);

        setConfiguration(getAptoideConfiguration());
        initImageLoader();
        setDebugMode();
        checkIsSystem();
        setThemePicker(getNewThemePicker());
        Crashlytics.setString("Language", getResources().getConfiguration().locale.getLanguage());
        AptoideUtils.CrashlyticsUtils.subsctibeActivityLiveCycleEvent();
    }

    /**
     * Set the default debugging mode. There are several ways to set this outside the Application:
     * 1) set by the Android, also by multiple ways (check documentations of Log.isLoggable): <br />
     *      <i>setprop log.tag.APTOIDE DEBUG</i>
     * 2) set by us: via a flag in SharedPreferences
     *
     */
    private void setDebugMode() {
        boolean debugMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("debugmode", false);
        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        DEBUG_MODE = DEBUG_MODE | debugMode | isDebuggable;
        if(DEBUG_MODE){
            // Set RoboSpice log level
            Ln.getConfig().setLoggingLevel(Log.VERBOSE);

            Toast.makeText(this, "Debug mode is: " + Aptoide.DEBUG_MODE, Toast.LENGTH_LONG).show();
        }
    }

    public AptoideThemePicker getNewThemePicker() {
        return new AptoideThemePicker();
    }

    private void checkIsSystem() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.PERMISSION_GRANTED);
            IS_SYSTEM = (info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.printException(e);
        }
    }

    /**
     * Inits nostra13's ImageLoader
     */
    private void initImageLoader() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showStubImage(R.drawable.icon_non_available)
                .build();

        FileNameGenerator generator = new FileNameGenerator() {
            @Override
            public String generate(String s) {

                if(s!=null){
                    return s.substring(s.lastIndexOf('/') + 1);
                } else {
                    return null;
                }
            }
        };

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .discCache(new UnlimitedDiscCache(new File(getConfiguration().getPathCacheIcons()), null, generator))
                .imageDownloader(new ImageDownloaderWithPermissions(getContext()))
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public AptoideConfiguration getAptoideConfiguration() {
        return new AptoideConfiguration();
    }

    public static AptoideConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AptoideConfiguration configuration) {
        Aptoide.configuration = configuration;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static class ImageDownloaderWithPermissions extends BaseImageDownloader {

        /** {@value} */
        public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
        /** {@value} */
        public static final int DEFAULT_HTTP_READ_TIMEOUT = 10 * 1000; // milliseconds

        public ImageDownloaderWithPermissions(Context context) {
            this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);

        }

        public ImageDownloaderWithPermissions(Context context, int connectTimeout, int readTimeout) {
            super(context, connectTimeout, readTimeout);
        }

        @Override
        public InputStream getStream(String imageUri, Object extra) throws IOException {

            boolean download = AptoideUtils.NetworkUtils.isIconDownloadPermitted(context);

            switch (Scheme.ofUri(imageUri)) {
                case HTTP:
                case HTTPS:
                    if(download){
                        return getStreamFromNetwork(imageUri, extra);
                    }
                    return null;
                case FILE:
                    return getStreamFromFile(imageUri, extra);
                case CONTENT:
                    return getStreamFromContent(imageUri, extra);
                case ASSETS:
                    return getStreamFromAssets(imageUri, extra);
                case DRAWABLE:
                    return getStreamFromDrawable(imageUri, extra);
                case UNKNOWN:
                default:
                    return getStreamFromOtherSource(imageUri, extra);
            }
        }
    }

    public static boolean isUpdate() throws PackageManager.NameNotFoundException {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("version", 0) < getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionCode;
    }

    public static void setWebInstallServiceRunning(boolean webInstallServiceRunning) {
        Aptoide.webInstallServiceRunning = webInstallServiceRunning;
    }

}
