/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 04/05/2016.
 */

package com.aptoide.amethyst.social;

import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ContactsProviderIntegrationTest {

	private ContactsProvider contactsProvider;

	@Before
	public void setUp() throws Exception {
		contactsProvider = new ContactsProvider(InstrumentationRegistry.getInstrumentation()
				.getTargetContext().getContentResolver());
	}

	@Test
	public void getDeviceContacts() throws Exception {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			try {
				contactsProvider.getDeviceContacts();
				fail("Marshmallow should throw a SecurityException since no permission was " +
						"requested.");
			} catch (SecurityException ignored) {}
		} else {
			assertNotNull(contactsProvider.getDeviceContacts());
		}
	}
}