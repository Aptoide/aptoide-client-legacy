/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 29/04/2016.
 */

package com.aptoide.amethyst.social;

/**
 * Manages social related features of Aptoide.
 */
public class SocialManager {

	private final AptoideFriendRepository aptoideRepository;
	private final ContactsProvider contactsProvider;

	public SocialManager(AptoideFriendRepository aptoideRepository, ContactsProvider contactsProvider) {
		this.aptoideRepository = aptoideRepository;
		this.contactsProvider = contactsProvider;
	}

	public AptoideFriends getContacsAptoideFriends() {
		return aptoideRepository.getFriends(contactsProvider.getDeviceContacts());
	}
}
