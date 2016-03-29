package com.aptoide.amethyst.downloadmanager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.downloadmanager.state.CompletedState;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.aptoide.amethyst.downloadmanager.model.DownloadModel;
import com.aptoide.amethyst.downloadmanager.state.ActiveState;
import com.aptoide.amethyst.downloadmanager.state.ErrorState;
import com.aptoide.amethyst.downloadmanager.state.InactiveState;
import com.aptoide.amethyst.downloadmanager.state.StatusState;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.Logger;

/**
 * Runnable responsible for informing the Notification Bar with the download Progress.
 * Class renamed from DownloadInfo.java (<= v6)
 *
 * User: rmateus
 * Date: 02-07-2013
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class DownloadInfoRunnable implements Runnable, Serializable {

    private static final int UPDATE_INTERVAL_MILLISECONDS = 1000;

    private double mSpeed;
    private long id;
    private long mDownloadedSize;
    private long mSize;
    private long mETA;
    private long mProgress = 0;
    private boolean isPaused = false;
    private boolean update;

    private transient NotificationCompat.Builder mBuilder;
    private String mDestination;
    private StatusState mStatusState;
    private Download download;
    private List<DownloadModel> mFilesToDownload;
    private List<DownloadThread> threads = new ArrayList<>();
    private DownloadExecutor downloadExecutor;
    private DownloadManager downloadManager;

    public DownloadInfoRunnable(DownloadManager manager, long id) {
        this.id = id;
        this.downloadManager = manager;
        this.mStatusState = new InactiveState(this);
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Timer timer = new Timer();

        try {

            for (DownloadModel downloadModel : mFilesToDownload) {
                DownloadThread thread = new DownloadThread(downloadModel, this);
                executor.submit(thread);
                threads.add(thread);
            }

            checkDirectorySize(Aptoide.getConfiguration().getPathCacheApks());

            mSize = getAllThreadSize();

            TimerTask task = new TimerTask() {

                public long mAvgSpeed;
                /** How much was downloaded last time. */
                private long iMLastDownloadedSize = mDownloadedSize;
                /** The nanoTime last time. */
                private long iMLastTime = System.currentTimeMillis();
                private long iMFirstTime = System.currentTimeMillis();

                @Override
                public void run() {
                    long mReaminingSize = getAllSizeRemaining();
                    mDownloadedSize = getAllDownloadedSize();
                    mProgress = getAllProgress();

                    long timeElapsedSinceLastTime = System.currentTimeMillis() - iMLastTime;
                    long timeElapsed = System.currentTimeMillis() - iMFirstTime;
                    iMLastTime = System.currentTimeMillis();
                    // Difference between last time and this time = how much was downloaded since last run.
                    long downloadedSinceLastTime = mDownloadedSize - iMLastDownloadedSize;
                    iMLastDownloadedSize = mDownloadedSize;
                    if (timeElapsedSinceLastTime > 0 && timeElapsed > 0) {
                        // Speed (bytes per second) = downloaded bytes / time in seconds (nanoseconds / 1000000000)
                        mAvgSpeed = (mDownloadedSize) * 1000 / timeElapsed;
                        mSpeed = downloadedSinceLastTime * 1000 / timeElapsedSinceLastTime;
                    }


                    if (mAvgSpeed > 0) {
                        // ETA (milliseconds) = remaining byte size / bytes per millisecond (bytes per second * 1000)
                        mETA = (mReaminingSize - mDownloadedSize) * 1000 / mAvgSpeed;
                    }
                    Log.d("DownloadManager", "ETA: " + mETA + " Speed: " + mSpeed / 1000 + " Size: " + DownloadUtils.formatBytes(mSize) + " Downloaded: " + DownloadUtils.formatBytes(mDownloadedSize) + " Status: " + mStatusState + " TotalDownloaded: " + DownloadUtils.formatBytes(mProgress) + " " + System.identityHashCode(DownloadInfoRunnable.this));

                    download.setSpeed(getSpeed());
                    download.setTimeLeft(mETA);
                    download.setProgress(getPercentDownloaded());
                    BusProvider.getInstance().post(new OttoEvents.DownloadInProgress(download));
                }
            };

            // Schedule above task for every (UPDATE_INTERVAL_MILLISECONDS) milliseconds.
            timer.schedule(task, 0, UPDATE_INTERVAL_MILLISECONDS);
            executor.shutdown();
            // Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs,
            // or the current thread is interrupted, whichever happens first.
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            timer.cancel();
            timer.purge();
            mSize = getAllThreadSize();
            mProgress = getAllProgress();

            Log.d("download-trace", "Downloads done " + mSize + " " + mProgress + " " + mStatusState.getEnumState().name());
            download.setSpeed(getSpeed());
            download.setProgress(getPercentDownloaded());

            if (mStatusState instanceof ActiveState) {
                changeStatusState(new CompletedState(this));
                autoExecute();
                Analytics.DownloadComplete.downloadComplete(download);
            }

        } catch (Exception e) {
            changeStatusState(new ErrorState(this, EnumDownloadFailReason.NO_REASON));
            e.printStackTrace();
        }

        BusProvider.getInstance().post(new OttoEvents.DownloadEvent(getId(), mStatusState));

        downloadManager.updatePendingList();
        threads.clear();
        mDownloadedSize = 0;
        mSpeed = 0;
        mETA = 0;

        Logger.d("download-trace", "Download Finish??" + download.getName());
    }

    private double getDirSize(File dir) {
        double size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();
            if (subFiles != null) {
                for (File file : subFiles) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += this.getDirSize(file);
                    }
                }
            }
        }
        return size;
    }

    private void checkDirectorySize(String dirPath) {

        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }

        double size = getDirSize(dir) / 1024 / 1024;
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());
        long maxFileCache;
        try {
            maxFileCache = Long.parseLong((sPref.getString("maxFileCache", "200")));
        } catch (Exception e) {
            maxFileCache = 50;
        }

        if (maxFileCache < 50) maxFileCache = 50;
        if (maxFileCache > 0 && size > maxFileCache) {
            File[] files = dir.listFiles();
            long latestTime = System.currentTimeMillis();
            long currentTime;
            File fileToDelete = null;
            for (File file : files) {
                currentTime = file.lastModified();

                if (currentTime < latestTime) {
                    latestTime = currentTime;
                    fileToDelete = file;
                }


            }
            if (fileToDelete != null) {
                Log.d("download-trace", "Deleting " + fileToDelete.getName());
                if (!fileToDelete.delete()) {
                    return;
                }
                checkDirectorySize(dirPath);
            }
        }
    }

    public EnumDownloadFailReason getFailReason() {
        return ((ErrorState) mStatusState).getErrorMessage();
    }

    public void autoExecute() {
        if (downloadExecutor != null) {
            for (DownloadModel file : mFilesToDownload) {
                if (file.isAutoExecute()) {
                    downloadExecutor.execute();
                }
            }
        }
    }

    private long getAllDownloadedSize() {
        long sum = 0;
        for (DownloadThread thread : threads) {
            sum = sum + thread.getmDownloadedSize();
        }
        return sum;
    }

    private long getAllProgress() {
        long sum = 0;
        for (DownloadThread thread : threads) {
            sum = sum + thread.getmProgress();
        }
        return sum;
    }

    private long getAllThreadSize() {
        long sum = 0;
        for (DownloadThread thread : threads) {
            sum = sum + thread.getmFullSize();
        }
        return sum;
    }

    public void remove(boolean isRemove) {
        Log.d("download-trace", "Download remove: " + download.getName());

        changeStatusState(new CompletedState(this));

        if (mFilesToDownload == null) return;

        if (isRemove) {
            for (DownloadModel model : mFilesToDownload) {
                new File(model.getDestination()).delete();
            }
        }

        mProgress = 0;
        mSize = 0;
        mFilesToDownload.clear();
        downloadManager.removeDownload(this);
        BusProvider.getInstance().post(new OttoEvents.DownloadEvent(getId(), mStatusState));
    }

    public void download() {
        Log.d("download-trace", "download-state: " + download.getDownloadState());
        Log.d("download-trace", "Download started at " + download.getProgress());
        mProgress = download.getProgress();
        BusProvider.getInstance().post(new OttoEvents.DownloadEvent(getId(), mStatusState));
        this.mStatusState.download();
    }

    public double getSpeed() {
        return mSpeed * 8;
    }

    public long getEta() {
        return mETA;
    }

    public void changeStatusState(StatusState state) {
        mStatusState.changeTo(state);
    }

    public long getId() {
        return id;
    }

    public long getAllSizeRemaining() {
        long sum = 0;
        for (DownloadThread thread : threads) {
            sum = sum + thread.getmRemainingSize();
//            Log.d("DownloadManagerThread", "Size: " + thread.getmRemainingSize());
        }
        return sum;
    }

    public boolean isPaused() {
        return isPaused;
    }


    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public boolean isPaid() {
        return download.isPaid();
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public DownloadExecutor getDownloadExecutor() {
        return downloadExecutor;
    }

    public void setDownloadExecutor(DownloadExecutor executor) {
        this.downloadExecutor = executor;
    }

    public NotificationCompat.Builder getmBuilder() {
        return mBuilder;
    }

    public void setmBuilder(NotificationCompat.Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
        this.download.setParent(this);
    }

    public void setFilesToDownload(List<DownloadModel> mFilesToDownload) {
        this.mFilesToDownload = mFilesToDownload;
    }

    public int getPercentDownloaded() {
        if (mSize == 0) {
            return 0;
        }

        return (int) ((mProgress) * 100 / mSize);
    }

    public List<DownloadModel> getmFilesToDownload() {
        return mFilesToDownload;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String mDestination) {
        this.mDestination = mDestination;
    }

    public StatusState getStatusState() {
        return this.mStatusState;
    }

    public void setStatusState(StatusState statusState) {
        this.mStatusState = statusState;
    }

}