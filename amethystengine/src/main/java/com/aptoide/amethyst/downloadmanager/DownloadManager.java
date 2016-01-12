package com.aptoide.amethyst.downloadmanager;

import com.aptoide.amethyst.downloadmanager.state.ActiveState;
import com.aptoide.amethyst.utils.Logger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The download manager keeps track of download objects and moves them around its lists.
 * [singleton]
 *
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class DownloadManager implements Serializable {

    /**
     * List of active download objects.
     */
    private ArrayList<DownloadInfoRunnable> mActiveList;

    /**
     * List of inactive download objects.
     */

    private ArrayList<DownloadInfoRunnable> mInactiveList;
    /**
     * List of pending download objects.
     */
    private ArrayList<DownloadInfoRunnable> mPendingList;

    /**
     * List of completed download objects.
     */
    private ArrayList<DownloadInfoRunnable> mCompletedList;
    /**
     * List of download objects with errors.
     */
    private ArrayList<DownloadInfoRunnable> mErrorList;

    /**
     * Construct a download manager. Initializes lists.
     */
    public DownloadManager() {
        mActiveList = new ArrayList<>();
        mInactiveList = new ArrayList<>();
        mPendingList = new ArrayList<>();
        mCompletedList = new ArrayList<>();
        mErrorList = new ArrayList<>();
    }

    /**
     * Check if there is room in the active list.
     *
     * @return <tt>true</tt> if there is room, <tt>false</tt> otherwise.
     */
    private boolean activeListHasRoom() {
        int maxDownloads = 1;
        return mActiveList.size() < maxDownloads;
    }

    /**
     * Tries to add a download object to the active list.
     * Will not work if the max number of downloads is already reached.
     *
     * @param downloadInfoRunnable The download object to be added to the list.
     * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
     */
    public boolean addToActiveList(DownloadInfoRunnable downloadInfoRunnable) {
        return activeListHasRoom() && mActiveList.add(downloadInfoRunnable);
    }

    /**
     * Tries to add a download object to the inactive list.
     *
     * @param downloadInfoRunnable The download object to be added to the list.
     * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
     */
    public boolean addToInactiveList(DownloadInfoRunnable downloadInfoRunnable) {
        return mInactiveList.add(downloadInfoRunnable);
    }

    /**
     * Tries to add a download object to the pending list.
     *
     * @param downloadInfoRunnable The download object to be added to the list.
     * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
     */
    public boolean addToPendingList(DownloadInfoRunnable downloadInfoRunnable) {
        Logger.d("download-trace", "added to pendingList: " + downloadInfoRunnable.getDownload().getName());
        return mPendingList.add(downloadInfoRunnable);
    }

    /**
     * Tries to add a download object to the completed list.
     *
     * @param downloadInfoRunnable The download object to be added to the list.
     * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
     */
    public boolean addToCompletedList(DownloadInfoRunnable downloadInfoRunnable) {


        return mCompletedList.add(downloadInfoRunnable);
    }

    /**
     * Tries to add a download object to the error list.
     *
     * @param downloadInfoRunnable The download object to be added to the list.
     * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
     */
    public boolean addToErrorList(DownloadInfoRunnable downloadInfoRunnable) {
        return mErrorList.add(downloadInfoRunnable);
    }

    /**
     * Removes a download from the active list.
     *
     * @param downloadInfoRunnable The download object to be removed from the list.
     */
    public void removeFromActiveList(DownloadInfoRunnable downloadInfoRunnable) {
        mActiveList.remove(downloadInfoRunnable);
    }

    /**
     * Removes a download from the inactive list.
     *
     * @param downloadInfoRunnable The download object to be removed from the list.
     */
    public void removeFromInactiveList(DownloadInfoRunnable downloadInfoRunnable) {
        mInactiveList.remove(downloadInfoRunnable);
    }

    /**
     * Removes a download from the pending list.
     *
     * @param downloadInfoRunnable The download object to be removed from the list.
     */
    public void removeFromPendingList(DownloadInfoRunnable downloadInfoRunnable) {
        mPendingList.remove(downloadInfoRunnable);
    }

    /**
     * Removes a download from the completed list.
     *
     * @param downloadInfoRunnable The download object to be removed from the list.
     */
    public void removeFromCompletedList(DownloadInfoRunnable downloadInfoRunnable) {
        mCompletedList.remove(downloadInfoRunnable);
    }

    /**
     * Removes a download from the error list.
     *
     * @param downloadInfoRunnable The download object to be removed from the list.
     */
    public void removeFromErrorList(DownloadInfoRunnable downloadInfoRunnable) {
        mErrorList.remove(downloadInfoRunnable);
    }

    /**
     * Check if there are pending downloads and if so move the top one up to the active list.
     */
    public void updatePendingList() {
        // if an active download has stopped downloading, activate top pending download.
        while (mPendingList.size() > 0 && activeListHasRoom()) {
            DownloadInfoRunnable pending = mPendingList.get(0);
            pending.changeStatusState(new ActiveState(pending));
        }
    }

    /**
     * Remove a download from the download manager.
     * Actually only removes the URL from the manager's list of URLs.
     *
     * @param downloadInfoRunnable Which download object to remove.
     */
    public void removeDownload(DownloadInfoRunnable downloadInfoRunnable) {
        mCompletedList.remove(downloadInfoRunnable);
    }

    /**
     * *********** Getters *********
     */
    public ArrayList<DownloadInfoRunnable> getmErrorList() {
        return mErrorList;
    }

    public ArrayList<DownloadInfoRunnable> getmCompletedList() {
        return mCompletedList;
    }

    public ArrayList<DownloadInfoRunnable> getmPendingList() {
        return mPendingList;
    }

    public ArrayList<DownloadInfoRunnable> getmActiveList() {
        return mActiveList;
    }
}