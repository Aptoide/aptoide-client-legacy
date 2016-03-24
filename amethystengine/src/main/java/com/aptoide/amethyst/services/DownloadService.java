package com.aptoide.amethyst.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LongSparseArray;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.downloadmanager.DownloadExecutor;
import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;
import com.aptoide.amethyst.downloadmanager.DownloadManager;
import com.aptoide.amethyst.downloadmanager.DownloadUtils;
import com.aptoide.amethyst.downloadmanager.adapter.NotOngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.adapter.OngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.downloadmanager.model.DownloadModel;
import com.aptoide.amethyst.downloadmanager.model.FinishedApk;
import com.aptoide.amethyst.downloadmanager.state.ActiveState;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.json.GetApkInfoJson;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.Errors;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.aptoide.dataprovider.webservices.models.UpdatesResponse;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.ScheduledDownloadItem;
import com.aptoide.models.displayables.UpdateRow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import com.aptoide.amethyst.MainActivity;

import com.aptoide.amethyst.webservices.GetApkInfoRequestFromMd5;
import com.aptoide.amethyst.webservices.GetApkInfoRequestFromVercode;

/**
 * This whole class needs to be refactored
 */
public class DownloadService extends Service {

    private static final String OBB_DESTINATION = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/";
    private DownloadManager manager = new DownloadManager();
    private Timer timer;
    private boolean isStopped = true;
    //private NotificationCompat.Builder mBuilder;
    private LongSparseArray<DownloadInfoRunnable> downloads = new LongSparseArray<>();
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                String file = getCacheDir().getAbsolutePath();
                File fileToCheck = new File(file + "/downloadManager");

                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileToCheck));
                    manager = (DownloadManager) in.readObject();
                    in.close();

                    for (DownloadInfoRunnable info : manager.getmErrorList()) {
                        downloads.put(info.getId(), info);
                    }

                    for (DownloadInfoRunnable info : manager.getmActiveList()) {
                        downloads.put(info.getId(), info);
                    }

                    for (DownloadInfoRunnable info : manager.getmCompletedList()) {
                        downloads.put(info.getId(), info);
                    }

                    for (DownloadInfoRunnable info : manager.getmPendingList()) {
                        downloads.put(info.getId(), info);
                    }

                } catch (IOException | ClassNotFoundException e) {
                    Logger.printException(e);
                }
            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        try {
            String file = getCacheDir().getAbsolutePath();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file + "/downloadManager"));
            out.writeObject(manager);
            out.flush();
            out.close();
        } catch (IOException e) {
            Logger.printException(e);
        }
        super.onDestroy();
    }

    /**
     * Updates the download list and stops the service if it's empty. If not, updates the progress in the Notification bar
     */
    public void updateDownload() {
        ArrayList<DownloadInfoRunnable> ongoingDownloads = getOngoingDownloads();
        if (!ongoingDownloads.isEmpty()) {
            // updateProgress(mBuilder, ongoingDownloads);
            updateProgress();
        } else {
            timer.cancel();
            timer.purge();
            stopSelf();
            mBuilder = null;
            stopForeground(true);
            isStopped = true;
        }

    }

    public DownloadInfoRunnable getDownload(long id) {

        if (downloads.get(id) != null) {
            return downloads.get(id);
        } else {
            return new DownloadInfoRunnable(manager, id);
        }
    }

    private void startIfStopped() {
        if (isStopped) {
            isStopped = false;
            timer = new Timer();
            timer.schedule(getTask(), 0, 1000);
        }
    }

    public void startExistingDownload(long id) {
        startService(new Intent(getApplicationContext(), DownloadService.class));

        final NotificationCompat.Builder builder = setNotification(id);

        if (mBuilder == null) mBuilder = createDefaultNotification();
        startForeground(-3, mBuilder.build());

        startIfStopped();

        Logger.d("Aptoide-DownloadManager", "Starting existing download " + id);
        for (final DownloadInfoRunnable info : manager.getmCompletedList()) {
            if (info.getId() == id) {
                final PackageManager packageManager = getPackageManager();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DownloadModel model : info.getmFilesToDownload()) {
                            try {
                                PackageInfo packageInfo = packageManager.getPackageInfo(info.getDownload().getPackageName(), PackageManager.SIGNATURE_MATCH);
                                if (packageInfo!=null && packageInfo.versionName!=null && info.getDownload()!=null && info.getDownload().getVersion()!=null && packageInfo.versionName.equals(info.getDownload().getVersion())) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent LaunchIntent = packageManager.getLaunchIntentForPackage(info.getDownload().getPackageName());
                                            if (LaunchIntent != null) {
                                                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(LaunchIntent);
                                            }
                                        }
                                    });
                                } else {
                                    throw new PackageManager.NameNotFoundException();
                                }

                            } catch (PackageManager.NameNotFoundException e) {

                                try {
                                    String calculatedMd5 = AptoideUtils.Algorithms.md5Calc(new File(model.getDestination()));
                                    if (!calculatedMd5.equals(info.getDownload().getMd5())) {
                                        Logger.d("download-trace", "Failed Md5 for " + info.getDownload().getName() + " : " + info.getDestination() + "   calculated " + calculatedMd5 + " vs " + info.getDownload().getMd5());
                                        info.setmBuilder(builder);

                                        info.download();
                                        break;
                                    } else {
                                        info.autoExecute();
                                        Logger.d("download-trace", "Checked Md5 for " + info.getDownload().getName() + ", application download it's already completed!");
                                        break;
                                    }
                                } catch (Exception e1) {
                                    Logger.printException(e1);
                                }

                            }
                        }
                    }
                }).start();
                return;
            }
        }

        for (DownloadInfoRunnable info : manager.getmErrorList()) {
            if (info.getId() == id) {
                info.setmBuilder(builder);
                info.download();
                return;
            }
        }
    }

    public void startDownloadFromUrl(String remotePath, String md5, long id, Download download, String repoName) {
        ArrayList<DownloadModel> filesToDownload = new ArrayList<>();

        String path = Aptoide.getConfiguration().getPathCacheApks();

        DownloadModel downloadModel = new DownloadModel(remotePath, path + md5 + ".apk", md5, 0);
        downloadModel.setAutoExecute(true);
        filesToDownload.add(downloadModel);

        FinishedApk apk = new FinishedApk(download.getName(),
                download.getPackageName(),
                download.getVersion(), id,
                download.getIcon(),
                path + md5 + ".apk",
                new ArrayList<String>());
        apk.setRepoName(repoName);

        download(id, download, apk, filesToDownload);
    }

    /**
     * New installer method for v7
     * This whole class needs major refactoring.
     *  @param url
     * @param url_alt
     * @param md5sum
     * @param fileSize
     * @param name
     * @param packageName
     * @param versionName
     * @param icon
     * @param downloadOld
     */
    public void downloadFromV7WithObb(String url,
                                      String url_alt,
                                      String md5sum,
                                      long fileSize,
                                      String name,
                                      String packageName,
                                      String versionName,
                                      String icon,
                                      long appId,
                                      boolean paid,
                                      GetAppMeta.Obb obb,
                                      Download downloadOld, List<String> permissions) {

        UpdatesResponse.UpdateApk apk = createUpdateApkFromV7Params(url, url_alt, md5sum, fileSize, name, packageName, versionName, icon, appId);

        Download download = new Download();
        download.setId(apk.md5sum.hashCode());
        download.setName(apk.name);
        download.setPackageName(apk.packageName);
        download.setVersion(apk.versionName);
        download.setMd5(apk.md5sum);
        download.setPaid(paid);
        download.setIcon(apk.icon);
        download.setSize(fileSize);
        download.setCpiUrl(downloadOld.getCpiUrl());

        startDownload(download, apk, obb, permissions);
    }


    /**
     * Creates an UpdatesResponse.UpdateApk with the data coming from the UI element UpdateRow
     *
     * @param rows
     */
    public void installAppFromUpdateRow(List<UpdateRow> rows) {
        UpdatesResponse.UpdateApk apk;
        for (UpdateRow row : rows) {
            apk = new UpdatesResponse.UpdateApk();
            apk.name = row.appName;
            apk.packageName = row.packageName;
            apk.versionName = row.versionName;
            apk.md5sum = row.md5sum;
            apk.icon = row.icon;
            apk.apk = new UpdatesResponse.UpdateApk.Apk();
            apk.apk.path = row.path;
            apk.apk.path_alt = row.path_alt;
            apk.apk.filesize = row.fileSize;
            apk.size = row.fileSize;
            apk.id = row.id;

            downloadFromV7(apk, false);
        }
    }

    public void downloadFromV7(UpdatesResponse.UpdateApk apk, boolean paid) {

        Download download = new Download();
        download.setId(apk.md5sum.hashCode());
        download.setName(apk.name);
        download.setPackageName(apk.packageName);
        download.setVersion(apk.versionName);
        download.setMd5(apk.md5sum);
        download.setPaid(paid);
        download.setIcon(apk.icon);
        download.setSize(apk.size.longValue());

        startDownload(download, apk, null, new ArrayList<String>());
    }

    public void startDownloadFromJson(GetApkInfoJson json, long id, Download download) {
        ArrayList<DownloadModel> filesToDownload = new ArrayList<>();

        if (json.obb != null) {
            DownloadModel mainObbDownload = new DownloadModel(json.obb.main.path, OBB_DESTINATION + download.getPackageName() + "/" + json.obb.main.filename, json.obb.main.md5sum, json.obb.main.filesize.longValue());
            filesToDownload.add(mainObbDownload);
            if (json.obb.patch != null) {
                DownloadModel patchObbDownload = new DownloadModel(json.obb.patch.path, OBB_DESTINATION + download.getPackageName() + "/" + json.obb.patch.filename, json.obb.patch.md5sum, json.obb.patch.filesize.longValue());
                filesToDownload.add(patchObbDownload);
            }
        }

        String path = Aptoide.getConfiguration().getPathCacheApks();

        if (json.apk.md5sum != null) {
            download.setId(json.apk.md5sum.hashCode());
        }

        DownloadModel downloadModel = new DownloadModel(json.apk.path, path + json.apk.md5sum + ".apk", json.apk.md5sum, json.apk.size.longValue());
        downloadModel.setAutoExecute(true);
        downloadModel.setFallbackUrl(json.apk.altpath);
        filesToDownload.add(downloadModel);

        FinishedApk apk = new FinishedApk(download.getName(),
                download.getPackageName(),
                download.getVersion(), id,
                download.getIcon(),
                path + json.apk.md5sum + ".apk",
                new ArrayList<>(json.apk.permissions));
        apk.setId(json.apk.id.longValue());

        download(download.getId(), download, apk, filesToDownload);
    }

    /**
     * This needs a major refactor. Start by eliminating the need for UpdateApk object
     */
    public void startDownload(Download download, UpdatesResponse.UpdateApk updateApk, GetAppMeta.Obb obb, List<String> permissions) {
        ArrayList<DownloadModel> filesToDownload = new ArrayList<>();

        if (obb != null) {

            if (obb.main != null) {
                DownloadModel mainObbDownload = new DownloadModel(obb.main.path, OBB_DESTINATION + download.getPackageName() + "/" + obb.main.filename, obb.main.md5sum, obb.main.filesize.longValue());
                filesToDownload.add(mainObbDownload);
            }

            if (obb.patch != null) {
                DownloadModel patchObbDownload = new DownloadModel(obb.patch.path, OBB_DESTINATION + download.getPackageName() + "/" + obb.patch.filename, obb.patch.md5sum, obb.patch.filesize.longValue());
                filesToDownload.add(patchObbDownload);
            }
        }

        String path = Aptoide.getConfiguration().getPathCacheApks();

        DownloadModel downloadModel = new DownloadModel(
                updateApk.apk.path,
                path + download.getMd5() + ".apk",
                download.getMd5(),
                download.getSize());

        downloadModel.setAutoExecute(true);
        downloadModel.setFallbackUrl(updateApk.apk.path_alt);
        filesToDownload.add(downloadModel);

        FinishedApk apk = new FinishedApk(
                download.getName(),
                download.getPackageName(),
                download.getVersion(),
                download.getMd5().hashCode(),
                download.getIcon(),
                path + download.getMd5() + ".apk",
                permissions);

        apk.setId(updateApk.id.longValue());

        download(download.getId(), download, apk, filesToDownload);
    }

    public void setReferrer(long id, String referrer) {
        try {

            DownloadInfoRunnable downloadInfoRunnable = downloads.get(id);
            if (downloadInfoRunnable != null) {
                downloadInfoRunnable.getDownloadExecutor().getApk().setReferrer(referrer);
            } else {
                Logger.d("AptoideDownloadService", "Downloadinfo for referrer was null");
            }

        } catch (Exception e) {
            Logger.printException(e);
            Logger.d("AptoideDownloadService", "error setting referrer");

        }

    }

    /**
     *
     * @param id the id of the download (it's the hascode of the md5sum string)
     * @param download
     * @param apk
     * @param filesToDownload
     */
    private void download(long id, Download download, FinishedApk apk, ArrayList<DownloadModel> filesToDownload) {
        final Context context = getApplicationContext();
        if (!AptoideUtils.NetworkUtils.isGeneralDownloadPermitted(context)) {
            Toast.makeText(context, context.getString(R.string.data_usage_constraint), Toast.LENGTH_LONG).show();
            return;
        }

        DownloadInfoRunnable info = getDownload(id);

        if (download.getCpiUrl() != null) {
            apk.setCpiUrl(download.getCpiUrl());
        }

        if (download.getReferrer() != null) {
            apk.setReferrer(download.getReferrer());
        } else {
            Logger.d("AptoideDownloadService", "Creating download with no referrer");
        }

        info.setDownloadExecutor(new DownloadExecutor(apk));
        info.setDownload(download);
        info.setFilesToDownload(filesToDownload);

        boolean update;
        try {
            Aptoide.getContext().getPackageManager().getPackageInfo(download.getPackageName(), 0);
            update = true;
        } catch (PackageManager.NameNotFoundException e) {
            update = false;
        }

        info.setUpdate(update);
        downloads.put(info.getId(), info);
        NotificationCompat.Builder builder = setNotification(info.getId());
        info.setmBuilder(builder);
        info.download();

        if (mBuilder == null) mBuilder = createDefaultNotification();

        startForeground(-3, mBuilder.build());
        startService(new Intent(context, DownloadService.class));

        startIfStopped();
        Toast.makeText(context, context.getString(R.string.starting_download), Toast.LENGTH_LONG).show();
    }

    private TimerTask getTask() {
        return new TimerTask() {

            @Override
            public void run() {
                updateDownload();
                //Logger.d("Aptoide-DownloadService", "Updating progress bar");
            }

        };
    }

    public ArrayList<DownloadInfoRunnable> getOngoingDownloads() {

        ArrayList<DownloadInfoRunnable> ongoingDownloads = new ArrayList<>();

        ongoingDownloads.addAll(manager.getmActiveList());
        ongoingDownloads.addAll(manager.getmPendingList());


        return ongoingDownloads;
    }


    public ArrayList<Displayable> getAllActiveDownloads() {
        ArrayList<Displayable> allDownloads = new ArrayList<>();

        for (DownloadInfoRunnable info : getOngoingDownloads()) {
            allDownloads.add(new OngoingDownloadRow(info.getDownload(), AptoideUtils.UI.getBucketSize()));
        }

        return allDownloads;
    }

    public ArrayList<Displayable> getAllNonActiveDownloads() {
        ArrayList<Displayable> allNonActive = new ArrayList<>();

        ArrayList<DownloadInfoRunnable> allDownloads = new ArrayList<>();
        allDownloads.addAll(manager.getmErrorList());
        allDownloads.addAll(manager.getmCompletedList());

        for (DownloadInfoRunnable info : allDownloads) {
            allNonActive.add(new NotOngoingDownloadRow(info.getDownload(), AptoideUtils.UI.getBucketSize()));
        }

        return allNonActive;
    }

    public void stopDownload(long id) {

        DownloadInfoRunnable info = getDownload(id);
        info.remove(true);

    }


    public void resumeDownload(long downloadId) {
        startService(new Intent(getApplicationContext(), DownloadService.class));

        if (mBuilder == null) mBuilder = createDefaultNotification();
        startForeground(-3, mBuilder.build());

        //Logger.d("donwload-trace", "setmBuilder: resumeDownload");
        DownloadInfoRunnable info = getDownload(downloadId);
        NotificationCompat.Builder builder = setNotification(downloadId);
        info.setmBuilder(builder);

        info.download();

        startIfStopped();
    }

    public void removeNonActiveDownloads(boolean isChecked) {
        ArrayList<DownloadInfoRunnable> allDownloads = new ArrayList<>();
        allDownloads.addAll(manager.getmErrorList());
        allDownloads.addAll(manager.getmCompletedList());

        for (DownloadInfoRunnable downloadInfoRunnable : allDownloads) {
            downloadInfoRunnable.remove(isChecked);
        }
    }

    public void startScheduledDownload(@NonNull final ScheduledDownloadItem scheduledDownload) {
        final GetApkInfoRequestFromMd5 requestFromMd5 = new GetApkInfoRequestFromMd5(this);
        requestFromMd5.setRepoName(scheduledDownload.getRepo_name());
        requestFromMd5.setMd5Sum(scheduledDownload.getMd5());

        final SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
        if (!spiceManager.isStarted()){
            spiceManager.start(getApplicationContext());
        }
        spiceManager.execute(requestFromMd5, new RequestListener<GetApkInfoJson>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) { }

            @Override
            public void onRequestSuccess(final GetApkInfoJson getApkInfoJson) {
                if (getApkInfoJson == null) {
                    return;
                }
                final GetApkInfoJson.Apk apk = getApkInfoJson.apk;
                final Download download = new Download();
                download.setMd5(scheduledDownload.getMd5());
                download.setId(scheduledDownload.getMd5().hashCode());
                download.setName(scheduledDownload.getName());
                download.setVersion(apk.getVername());
                download.setIcon(apk.getIcon());
                download.setPackageName(apk.getPackageName());
                startDownloadFromJson(getApkInfoJson, apk.getId().longValue(), download);
            }
        });
    }

    public void startDownloadFromAppId(final long id) {
        startService(new Intent(getApplicationContext(), DownloadService.class));

        if (mBuilder == null) mBuilder = createDefaultNotification();
        startForeground(-3, mBuilder.build());

        final SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);
        if (!manager.isStarted()) manager.start(getApplicationContext());
        final String sizeString = IconSizeUtils.generateSizeString(getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {

                Cursor apkCursor = new AptoideDatabase(Aptoide.getDb()).getApkInfo(id);

                if (apkCursor.moveToFirst()) {

                    String repoName = apkCursor.getString(apkCursor.getColumnIndex("reponame"));
                    final String name = apkCursor.getString(apkCursor.getColumnIndex("name"));
                    String package_name = apkCursor.getString(apkCursor.getColumnIndex("package_name"));
                    final String versionName = apkCursor.getString(apkCursor.getColumnIndex("version_name"));
                    final int versionCode = apkCursor.getInt(apkCursor.getColumnIndex("version_code"));
                    final String md5sum = apkCursor.getString(apkCursor.getColumnIndex("md5"));
                    String icon = apkCursor.getString(apkCursor.getColumnIndex("icon"));
                    final String iconpath = apkCursor.getString(apkCursor.getColumnIndex("iconpath"));

                    GetApkInfoRequestFromVercode request = new GetApkInfoRequestFromVercode(getApplicationContext());

                    request.setRepoName(repoName);
                    request.setPackageName(package_name);
                    request.setVersionName(versionName);
                    request.setVercode(versionCode);

                    Download download = new Download();
                    download.setId(md5sum.hashCode());
                    download.setName(name);
                    download.setPackageName(package_name);
                    download.setVersion(versionName);
                    download.setMd5(md5sum);

                    if (icon.contains("_icon")) {
                        String[] splittedUrl = icon.split("\\.(?=[^\\.]+$)");
                        icon = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
                    }

                    download.setIcon(iconpath + icon);

                    manager.getFromCacheAndLoadFromNetworkIfExpired(request, repoName + md5sum, DurationInMillis.ONE_HOUR, new DownloadRequest(download.getId(), download));
                    apkCursor.close();

                }
            }
        }).start();
    }

    private NotificationCompat.Builder createDefaultNotification() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent onClick = new Intent();
        onClick.setClassName(getPackageName(), /*Aptoide.getConfiguration().*/getStartActivityClass().getName()); //TODO dependency injection
        onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        onClick.setAction(Intent.ACTION_VIEW);
        onClick.putExtra("fromDownloadNotification", true);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent onClickAction = PendingIntent.getActivity(getApplicationContext(), 0, onClick, PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setOngoing(true);
        mBuilder.setContentTitle(AptoideUtils.StringUtils.getFormattedString(this, R.string.aptoide_downloading, Aptoide.getConfiguration().getMarketName()))
                .setSmallIcon(R.drawable.stat_sys_download)
                .setProgress(0, 0, true)
                .setContentIntent(onClickAction);
        mBuilder.setProgress(100, 0, true);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(-3, mBuilder.build());

        return mBuilder;
    }

    private Class getStartActivityClass() {
        return MainActivity.class;
    }

    private NotificationCompat.Builder setNotification(final long id) {

        DownloadInfoRunnable info = getDownload(id);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        //TODO
        Intent onClick = new Intent();
        onClick.setClassName(getPackageName(), /*Aptoide.getConfiguration().*/getStartActivityClass().getName());
        onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        onClick.setAction(Intent.ACTION_VIEW);
        onClick.putExtra("fromDownloadNotification", true);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent onClickAction = PendingIntent.getActivity(getApplicationContext(), 0, onClick, PendingIntent.FLAG_UPDATE_CURRENT);

        int size = DownloadUtils.dpToPixels(getApplicationContext(), 36);

        Bitmap icon = null;

        try {
            icon = DownloadUtils.decodeSampledBitmapFromResource(ImageLoader.getInstance().getDiscCache().get(info.getDownload().getIcon()).getAbsolutePath(), size, size);
//            icon = GlideUtils.downloadOnlyFromCache(Aptoide.getContext(), info.getDownload().getIcon(), size, size);
        } catch (Exception e) {
            Logger.printException(e);
        }

        mBuilder.setOngoing(true);
        mBuilder.setContentTitle(AptoideUtils.StringUtils.getFormattedString(this, R.string.aptoide_downloading, Aptoide.getConfiguration().getMarketName()));
        mBuilder.setContentText(info.getDownload().getName());
        if (icon != null) mBuilder.setLargeIcon(icon);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setContentIntent(onClickAction);
        //Logger.d("download-trace", "ETA: " + info.getEta());
        if (info.getEta() > 0) {
            String remaining = DownloadUtils.formatEta(info.getEta(), "");
            mBuilder.setContentInfo("ETA: " + (!remaining.equals("") ? remaining : "0s"));
        }

        return mBuilder;
    }

    private void updateProgress() {
        Collection<DownloadInfoRunnable> list = getOngoingDownloads();
        list.addAll(manager.getmCompletedList());

        for (DownloadInfoRunnable info : list) {
            if (info.getStatusState() instanceof ActiveState) {
                try {
                    info.getmBuilder().setProgress(100, info.getPercentDownloaded(), info.getPercentDownloaded() == 0);
                    if (info.getEta() > 0) {
                        String remaining = DownloadUtils.formatEta(info.getEta(), "");
                        info.getmBuilder().setContentInfo("ETA: " + (!remaining.equals("") ? remaining : "0s"));
                    }

                    mBuilder = info.getmBuilder();
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(-3, mBuilder.build());
                } catch (Exception e) {
                    Logger.printException(e);
                }
                return;
            }
        }
    }

    public class DownloadRequest implements RequestListener<GetApkInfoJson> {

        private final long id;
        private final Download download;

        public DownloadRequest(long id, Download download) {

            this.id = id;
            this.download = download;
        }

        @Override
        public void onRequestFailure(SpiceException e) {
            stopSelf();
            stopForeground(true);
            isStopped = true;
        }

        @Override
        public void onRequestSuccess(GetApkInfoJson getApkInfoJson) {
            if (getApkInfoJson != null) {
                if (getApkInfoJson.status.equals("OK")) {
                    startDownloadFromJson(getApkInfoJson, id, download);
                } else {
                    final HashMap<String, Integer> errorsMapConversion = Errors.getErrorsMap();
                    Integer stringId;
                    String message;
                    for (ErrorResponse error : getApkInfoJson.errors) {
                        stringId = errorsMapConversion.get(error.code);
                        if (stringId != null) {
                            message = getString(stringId);
                        } else {
                            message = error.msg;
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public class LocalBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }
    }


    private UpdatesResponse.UpdateApk createUpdateApkFromV7Params(String url, String url_alt, String md5sum, long fileSize, String name, String packageName, String versionName, String icon, long appId) {
        UpdatesResponse.UpdateApk apk = new UpdatesResponse.UpdateApk();
        apk.name = name;
        apk.packageName = packageName;
        apk.versionName = versionName;
        apk.md5sum = md5sum;
        apk.icon = icon;
        apk.apk = new UpdatesResponse.UpdateApk.Apk();
        apk.apk.path = url;
        apk.apk.path_alt = url_alt;
        apk.apk.filesize = fileSize;
        apk.id = appId;

        return apk;
    }

}