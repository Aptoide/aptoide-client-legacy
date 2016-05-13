package com.aptoide.amethyst.downloadmanager.state;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;

/**
 * The inactive state represents the status of a download object not doing anything.
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class InactiveState extends StatusState {

	/**
	 * Construct an inactive state.
	 * @param downloadInfoRunnable The downloadInfoRunnable associated with this state.
	 */
	public InactiveState(DownloadInfoRunnable downloadInfoRunnable) {
		super(downloadInfoRunnable);
	}

	@Override
	public void download() {
		mDownloadInfoRunnable.changeStatusState(new ActiveState(mDownloadInfoRunnable));
	}

	@Override
	public void changeFrom() {
        manager.removeFromInactiveList(mDownloadInfoRunnable);
	}

	@Override
	public boolean changeTo() {
		if (manager.addToInactiveList(mDownloadInfoRunnable)) {
			mDownloadInfoRunnable.setStatusState(this);
			return true;
		}

		return false;
	}

    @Override
    public EnumState getEnumState() {
        return EnumState.INACTIVE;  //To change body of implemented methods use File | Settings | File Templates.
    }

}