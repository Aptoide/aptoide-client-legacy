/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 13/05/2016.
 */

package com.aptoide.amethyst.model;

import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;

import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.UNKNOWN;

/**
 * Balloon that displays application security information.
 */
public class AppSecurityBalloon {

	private final String applicationName;
	private final String rank;
	private final String thirdPartyValidatedStore;
	private final int maxStartUpDisplays;

	/**
	 * Creates a new app security balloon.
	 * @param applicationName application name.
	 * @param rank malware rank {@link com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware#TRUSTED}, {@link com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware#WARNING} or {@link com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware#UNKNOWN}.
	 * @param thirdPartyValidatedStore malware third party validated (e.g. {@link GetAppMeta.File.Malware#GOOGLE_PLAY}).
	 * @param maxStartUpDisplays number of times ballon is going to be displayed automatically.
	 */
	public AppSecurityBalloon(String applicationName, String rank, String thirdPartyValidatedStore, int
			maxStartUpDisplays) {
		this.applicationName = applicationName;
		this.rank = rank;
		this.thirdPartyValidatedStore = thirdPartyValidatedStore;
		this.maxStartUpDisplays = maxStartUpDisplays;
	}

	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Returns whether balloon should be displayed or not.
	 */
	public boolean shouldDisplay() {
		return rank != null
				&& !UNKNOWN.equals(rank);
	}

	/**
	 * Returns whether balloon should be displayed automatically. After a successfully displaying
	 * the balloon {@link #didDisplayAutomatically()} should be called.
	 */
	public boolean shouldDisplayAutomatically() {
		return shouldDisplay()
				&& Preferences.getInt(Preferences.BALLOON_SECURITY_NUMBER_OF_DISPLAYS_INT, 0) <
				maxStartUpDisplays;
	}

	/**
	 * Should be called after balloon is displayed automatically.
	 */
	public void didDisplayAutomatically() {
		Preferences.putIntAndCommit(Preferences
				.BALLOON_SECURITY_NUMBER_OF_DISPLAYS_INT, Preferences.getInt
				(Preferences.BALLOON_SECURITY_NUMBER_OF_DISPLAYS_INT, 0) + 1);
	}

	/**
	 * Returns whether balloon has Google Play logo or not.
	 */
	public boolean hasGooglePlayLogo() {
		return GetAppMeta.File.Malware.GOOGLE_PLAY.equalsIgnoreCase(thirdPartyValidatedStore);
	}
}