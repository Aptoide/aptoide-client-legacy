/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 29/04/2016.
 */

package com.aptoide.amethyst.social;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SocialManagerTest {

	private AptoideFriendRepository repositoryMock;
	private SocialManager socialManager;
	private ContactsProvider contactsProviderMock;

	@Before
	public void setUp() throws Exception {
		repositoryMock = mock(AptoideFriendRepository.class);
		contactsProviderMock = mock(ContactsProvider.class);
		socialManager = new SocialManager(repositoryMock, contactsProviderMock);
	}

	@Test
	public void getContactsAptoideFriends() throws Exception {

		final List<SimpleContact> contactList = Arrays.asList(new SimpleContact("marcelo.benites@aptoide.com", "+5551555555555"),
				new SimpleContact("frederico@aptoide.com", "+55515555444"));
		when(contactsProviderMock.getDeviceContacts()).thenReturn(contactList);
		final AptoideFriends expectedFriends = new AptoideFriends(null, null);
		when(repositoryMock.getFriends(contactList)).thenReturn(expectedFriends);

		assertEquals(expectedFriends, socialManager.getContacsAptoideFriends());
		verify(contactsProviderMock).getDeviceContacts();
		verify(repositoryMock).getFriends(contactList);
	}
}