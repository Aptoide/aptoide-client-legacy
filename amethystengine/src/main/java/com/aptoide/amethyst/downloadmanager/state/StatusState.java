package com.aptoide.amethyst.downloadmanager.state;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;
import com.aptoide.amethyst.downloadmanager.DownloadManager;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;

import java.io.Serializable;


/**
 * A StatusState is a state in which a {@link DownloadInfoRunnable} can be and helps to perform some status specific actions.
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public abstract class StatusState implements Serializable{

	/** The download object this state is associated with. */
	protected DownloadInfoRunnable mDownloadInfoRunnable;
    protected DownloadManager manager;

    /**
	 * Construct a status state.
	 * @param downloadObject The downloadObject associated with this state.
	 */
	protected StatusState(DownloadInfoRunnable downloadObject) {
        mDownloadInfoRunnable = downloadObject;
        this.manager = downloadObject.getDownloadManager();
    }

	/**
	 * Try to start downloading.
	 */
	public abstract void download();

	/**
	 * Try to change a download object's state from this status state to another.
	 * @param state The status state to change to.
	 */
	public void changeTo(StatusState state) {
		if (state.changeTo()) {
			changeFrom();
            BusProvider.getInstance().post(new OttoEvents.DownloadEvent(mDownloadInfoRunnable.getId(), this));
            mDownloadInfoRunnable = null;
		}
        manager.updatePendingList();
    }

	/**
	 * Change from this state.
	 */
	public abstract void changeFrom();

	/**
	 * Change to this state.
	 * @return <tt>true</tt> the change was successful, <tt>false</tt> otherwise.
	 */
	public abstract boolean changeTo();

    public abstract EnumState getEnumState();
}
