package com.aptoide.amethyst.downloadmanager.state;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;

/**
 * The completed state represents the status of a download object when it has finished downloading.
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class CompletedState extends StatusState {

	/**
	 * Construct a completed state.
	 * @param downloadInfoRunnable The downloadInfoRunnable associated with this state.
	 */
	public CompletedState(DownloadInfoRunnable downloadInfoRunnable) {
		super(downloadInfoRunnable);
	}

	@Override
	public void download() {

        mDownloadInfoRunnable.changeStatusState(new ActiveState(mDownloadInfoRunnable));
	}

	@Override
	public void changeFrom() {

        manager.removeFromCompletedList(mDownloadInfoRunnable);
	}

	@Override
	public boolean changeTo() {
		if (manager.addToCompletedList(mDownloadInfoRunnable)) {
			mDownloadInfoRunnable.setStatusState(this);
			return true;
		}

		return false;
	}

    @Override
    public EnumState getEnumState() {
        return EnumState.COMPLETE;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
