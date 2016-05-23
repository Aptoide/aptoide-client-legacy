/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 29/04/2016.
 */

package com.aptoide.amethyst.social;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

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

	public Observable<AptoideFriends> getContacsAptoideFriends(final int limit, final int offset) {
		return contactsProvider.getDeviceContacts().flatMap(new Func1<List<SimpleContact>, Observable<AptoideFriends>>() {
			@Override
			public Observable<AptoideFriends> call(List<SimpleContact> contacts) {
				return aptoideRepository.getFriends(contacts, limit, offset);
			}
		});
	}
}