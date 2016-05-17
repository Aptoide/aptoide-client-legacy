/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 13/05/2016.
 */

package com.aptoide.amethyst.model;

import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;

import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.PASSED;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.UNKNOWN;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.WARN;

/**
 * Balloon that is displayed when an application is trusted.
 */
public class TrustedBalloon {

	private final GetAppMeta.File.Malware malware;
	private final int maxStartUpDisplays;

	/**
	 * Creates a new trusted balloon.
	 * @param malware information about application.
	 * @param maxStartUpDisplays number of times ballon is going to be displayed automatically.
	 */
	public TrustedBalloon(GetAppMeta.File.Malware malware, int maxStartUpDisplays) {
		this.malware = malware;
		this.maxStartUpDisplays = maxStartUpDisplays;
	}

	/**
	 * Returns whether balloon should be displayed or not.
	 */
	public boolean shouldDisplay() {
		return malware != null
				&& !UNKNOWN.equals(malware.rank);
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
		return malware.reason.thirdpartyValidated != null
				&& GetAppMeta.File.Malware.GOOGLE_PLAY.equalsIgnoreCase(malware.reason.thirdpartyValidated.store);
	}
}