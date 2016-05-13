package com.aptoide.amethyst.downloadmanager.state;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;

import java.io.Serializable;

/**
 * The active state represents the status of a download object in the process of downloading.
 *
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class ActiveState extends StatusState implements Serializable {

    /**
     * Construct an active state.
     *
     * @param downloadInfoRunnable The downloadInfoRunnable associated with this state.
     */
    public ActiveState(DownloadInfoRunnable downloadInfoRunnable) {
        super(downloadInfoRunnable);
    }

    @Override
    public void changeFrom() {
        manager.removeFromActiveList(mDownloadInfoRunnable);
    }

    @Override
    public boolean changeTo() {
        if (manager.addToActiveList(mDownloadInfoRunnable)) {
            // Set the status state before starting new thread because the while loop in the run method
            // depends on the status state being active.
//            Toast.makeText(ApplicationAptoide.getContext(), ApplicationAptoide.getContext().getString(R.string.starting_download), Toast.LENGTH_LONG).show();
            mDownloadInfoRunnable.setStatusState(this);
            Thread t = new Thread(mDownloadInfoRunnable);
            t.start();
            return true;
        }

        mDownloadInfoRunnable.changeStatusState(new PendingState(mDownloadInfoRunnable));
        return false;
    }

    @Override
    public EnumState getEnumState() {
        return EnumState.ACTIVE;
    }

    @Override
    public void download() {
        //do nothing, already active
    }
}