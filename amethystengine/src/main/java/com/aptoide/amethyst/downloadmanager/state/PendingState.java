package com.aptoide.amethyst.downloadmanager.state;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;

import java.io.Serializable;

/**
 * The pending state represents the status of a download object waiting to download.
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class PendingState extends StatusState implements Serializable {

    /**
	 * Construct a pending state.
	 * @param downloadInfoRunnable The downloadInfoRunnable associated with this state.
	 */
	public PendingState(DownloadInfoRunnable downloadInfoRunnable) {
		super(downloadInfoRunnable);

    }

	@Override
	public void download() {
		//do nothing, in pending mode already.
	}

	@Override
	public void changeFrom() {
        manager.removeFromPendingList(mDownloadInfoRunnable);
	}

	@Override
	public boolean changeTo() {
		if (manager.addToPendingList(mDownloadInfoRunnable)) {
			mDownloadInfoRunnable.setStatusState(this);
			return true;
		}

		return false;
	}

    @Override
    public EnumState getEnumState() {
        return EnumState.PENDING;
    }

}
