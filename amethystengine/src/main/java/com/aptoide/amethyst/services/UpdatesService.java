package com.aptoide.amethyst.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.Webservices;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.UpdatesApi;
import com.aptoide.dataprovider.webservices.models.UpdatesResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.aptoide.amethyst.MainActivity;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

/**
 * Created by rmateus on 15/06/15.
 */
public class UpdatesService extends Service {

    public static final String FORCE_UPDATE = "force_update";
    public static final int MAX_UPDATES = 50;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    static ScheduledExecutorService executor;
    GetUpdates task = new GetUpdates();
    static int retries = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("AptoideUpdates", "OnStartCommand");

        synchronized (this) {

            // AN-342: The updates tab is not populated on the first run when the device doesn't have internet connection
            if (AptoideUtils.NetworkUtils.isNetworkAvailable(getApplicationContext()) || !(new AptoideDatabase(Aptoide.getDb()).hasInstalled())) {

                if (executor == null) {
                    executor = Executors.newSingleThreadScheduledExecutor();
                    executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
                }

                if (intent != null && intent.hasExtra(FORCE_UPDATE)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                executor.shutdown();
                                Logger.d("AptoideUpdates", "Awaiting previous executor to terminate");
                                executor.awaitTermination(2, TimeUnit.MINUTES);
                                Logger.d("AptoideUpdates", "Previous terminatated");
                            } catch (InterruptedException e) {
                                Logger.printException(e);
                            }

                            executor = Executors.newSingleThreadScheduledExecutor();
                            executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
                        }
                    }).start();
                }
            } else {

                if (executor != null) {
                    executor.shutdown();
                }

                broadcastFinishEvent();
                executor = null;
                stopSelf();
            }
        }

        return START_STICKY_COMPATIBILITY;
    }

    public class GetUpdates implements Runnable {
        @Override
        public void run() {
            List<UpdatesResponse.UpdateApk> responseList = new ArrayList<>();
            AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());


            if (!database.hasInstalled()) {
                Logger.d("AptoideUpdates", "First run install");
                PackageManager packageManager = Aptoide.getContext().getPackageManager();
                if (packageManager != null) {

                    List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
                    for (PackageInfo anInstalledPackage : installedPackages) {
                        try {
                            UpdatesApi.Package aPackage = new UpdatesApi.Package();
                            aPackage.signature = AptoideUtils.Algorithms.computeSHA1sumFromBytes(anInstalledPackage.signatures[0].toByteArray()).toUpperCase(Locale.ENGLISH);
                            aPackage.vercode = anInstalledPackage.versionCode;
                            aPackage.packageName = anInstalledPackage.packageName;
                            database.insertInstalled(aPackage);
                        } catch (Exception e) {
                            Logger.printException(e);
                        }
                    }
                }
            }

            try {

                List<UpdatesApi.Package> updates = database.getUpdates(MAX_UPDATES);
                if (updates.isEmpty()) {
                    executor.shutdown();
                    Logger.d("AptoideUpdates", "database.getUpdates(50) is Empty. Stopping service and executor is " + executor.isShutdown());
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("showUpdatesNotification", true)) {
                        showUpdatesNotification();
                    }
//                    broadcastFinishEvent(0);
                    stopSelf();
                    return;
                }

                UpdatesApi api = new UpdatesApi();
                initUpdatesApi(api);

                Cursor servers = database.getStoresCursor();
                for (servers.moveToFirst(); !servers.isAfterLast(); servers.moveToNext()) {

                    String name = servers.getString(servers.getColumnIndex("name"));
                    api.store_names.add(name);

                    // the username of the store, not the Login
                    String username = servers.getString(servers.getColumnIndex("username"));
                    if (!TextUtils.isEmpty(username)) {
                        String password = servers.getString(servers.getColumnIndex("password"));
                        if (api.stores_auth == null) {
                            api.stores_auth = new ArrayList<>();
                        }

                        UpdatesApi.StoreAuth storeAuth = new UpdatesApi.StoreAuth();

                        storeAuth.store_name = name;
                        storeAuth.store_user = username;
                        storeAuth.store_pass_sha1 = password;

                        api.stores_auth.add(storeAuth);
                    }
                }


                //}
                servers.close();
                if (!api.store_names.isEmpty() && AptoideUtils.NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                    api.apks_data.addAll(updates);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setConverter(new JacksonConverter(mapper)).setEndpoint("http://").build();
                    Logger.d("AptoideUpdates", "Getting updates");
                    UpdatesResponse webUpdates = adapter.create(Webservices.class).getUpdates(api);

                    if (webUpdates != null && webUpdates.data != null && webUpdates.data.list != null) {
                        Logger.d("AptoideUpdates", "Got updates: " + webUpdates.data.list.size());
                        List<UpdatesResponse.UpdateApk> list = webUpdates.data.list;
                        responseList.addAll(list);
                    }
                }
                for (UpdatesApi.Package aPackage : updates) {
                    database.resetPackage(aPackage.packageName);
                    for (UpdatesResponse.UpdateApk aPackage2 : responseList) {
                        if (aPackage2.packageName.equals(aPackage.packageName)) {
                            database.updatePackage(aPackage2);
                        }
                    }
                }
                handleAutoUpdate(database.getAvailableUpdates(getApplicationContext()));
                retries = 0;
            } catch (Exception e) {
                Logger.printException(e);
                Logger.d("AptoideUpdates", "Exception retries: " + retries);
                if (retries == 7) {
                    executor.shutdown();
                    stopSelf();
                    Logger.d("AptoideUpdates", "Service exceeded retries. Shutting down. ");
                    retries = 0;
                }
                retries++;
            }
            broadcastFinishEvent();
            Logger.d("AptoideUpdates", "Stopped");
        }

        private void initUpdatesApi(UpdatesApi api) {
            api.q = AptoideUtils.HWSpecifications.filters(Aptoide.getContext());
            api.cpuid = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getString("APTOIDE_CLIENT_UUID", UpdatesApi.DEFAULT_CPUID);
            api.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            api.aaid = getAdvertisementId();
        }

        private String getAdvertisementId() {
            try {
                return AdvertisingIdClient.getAdvertisingIdInfo(Aptoide.getContext()).getId();
            } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                Logger.printException(e);
            }
            return null;
        }
    }

    //TODO: notification builder
    private void showUpdatesNotification() {
        int updates = 0;
        Cursor data = null;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            data = new AptoideDatabase(Aptoide.getDb()).getUpdates();
            updates = data.getCount();
        } finally {
            if (data != null)
                data.close();
        }
        if (updates > 0 && updates != defaultSharedPreferences.getInt("updates", 0)) {
            NotificationManager managerNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(Aptoide.getConfiguration().getMarketName().equals("Aptoide")) {
                int icon = R.drawable.ic_stat_aptoide_notification;
                //}
                Context context = getApplicationContext();
                CharSequence tickerText = AptoideUtils.StringUtils.getFormattedString(context, R.string.has_updates, Aptoide.getConfiguration().getMarketName());
                CharSequence contentTitle = Aptoide.getConfiguration().getMarketName();
                CharSequence contentText = AptoideUtils.StringUtils.getFormattedString(context, R.string.new_updates, updates);
                if (updates == 1) {
                    contentText = AptoideUtils.StringUtils.getFormattedString(context, R.string.one_new_update, updates);
                }
                Intent notificationIntent = new Intent();
                notificationIntent.setClassName(getPackageName(), MainActivity.class.getName());
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                notificationIntent.setAction("");
                notificationIntent.putExtra("new_updates", true);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setContentIntent(contentIntent)
                        .setTicker(tickerText)
                        .build();
                notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                managerNotification.notify(546, notification);
                defaultSharedPreferences.edit().putInt("updates", data.getCount()).apply();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        executor = null;
        Logger.d("AptoideUpdates", "OnDestroy");
    }

    private void handleAutoUpdate(final List<UpdatesResponse.UpdateApk> updates) {
        final Context context = getApplicationContext();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("auto_update", false) || updates.isEmpty()) {
            return;
        }

        final Intent intent = new Intent(context, DownloadService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                final DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
                final DownloadService downloadService = binder.getService();
                for (final UpdatesResponse.UpdateApk updateApk : updates) {
                    if (updateApk.icon != null && updateApk.icon.contains("_icon")) {
                        String[] splittedUrl = updateApk.icon.split("\\.(?=[^\\.]+$)");
                        String iconSize = IconSizeUtils.generateSizeString(context);
                        updateApk.icon = splittedUrl[0] + "_" + iconSize + "." + splittedUrl[1];
                    }
                    downloadService.downloadFromV7(updateApk, false);
                }
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) { }
        }, BIND_AUTO_CREATE);
    }

    private void broadcastFinishEvent() {
        Cursor data = new AptoideDatabase(Aptoide.getDb()).getUpdates();
        int size = data.getCount();
        data.close();
        BusProvider.getInstance().post(new OttoEvents.GetUpdatesFinishedEvent(size));
    }

}

