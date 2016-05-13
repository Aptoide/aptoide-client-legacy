package com.aptoide.amethyst.downloadmanager.state;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;
import com.aptoide.amethyst.downloadmanager.EnumDownloadFailReason;

/**
 * The error state represents the status of a download object when it has failed to download.
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class ErrorState extends StatusState {

	/** The error message of this state. */
	private EnumDownloadFailReason mErrorMessage;

	/**
	 * @return The error message of this state.
	 */
	public EnumDownloadFailReason getErrorMessage() {
		return mErrorMessage;
	}

	/**
	 * Construct an error state with a message.
	 * @param downloadInfoRunnable The downloadInfoRunnable associated with this state.
	 * @param errorMessage The error message of this state.
	 */
	public ErrorState(DownloadInfoRunnable downloadInfoRunnable, EnumDownloadFailReason errorMessage) {
		super(downloadInfoRunnable);
		mErrorMessage = errorMessage;
	}

	@Override
	public void download() {
		mDownloadInfoRunnable.changeStatusState(new ActiveState(mDownloadInfoRunnable));
	}

	@Override
	public void changeFrom() {
        manager.removeFromErrorList(mDownloadInfoRunnable);
	}

	@Override
	public boolean changeTo() {

        if (manager.addToErrorList(mDownloadInfoRunnable)) {
			mDownloadInfoRunnable.setStatusState(this);
			return true;
		}

		return false;
	}

    @Override
    public EnumState getEnumState() {
        return EnumState.ERROR;
    }
}