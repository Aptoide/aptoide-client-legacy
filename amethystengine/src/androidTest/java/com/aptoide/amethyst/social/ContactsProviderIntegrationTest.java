/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 16/05/2016.
 */

package com.aptoide.amethyst.social;

import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.observers.TestSubscriber;

@RunWith(AndroidJUnit4.class)
public class ContactsProviderIntegrationTest {


	private ContactsProvider contactsProvider;

	@Before
	public void setUp() throws Exception {
		contactsProvider = new ContactsProvider(InstrumentationRegistry.getInstrumentation()
				.getTargetContext()
				.getContentResolver());
	}

	@Test
	public void getDeviceContacts() throws Exception {

		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final TestSubscriber<List<SimpleContact>> testSubscriber = new TestSubscriber<List<SimpleContact>>() {
			@Override
			public void onCompleted() {
				super.onCompleted();
				countDownLatch.countDown();
			}

			@Override
			public void onNext(List<SimpleContact> contacts) {
				super.onNext(contacts);
				countDownLatch.countDown();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				countDownLatch.countDown();
			}
		};
		contactsProvider.getDeviceContacts().subscribe(testSubscriber);
		countDownLatch.await();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			testSubscriber.assertError(SecurityException.class);
		} else {
			testSubscriber.assertValueCount(1);
			testSubscriber.assertNoErrors();
			testSubscriber.assertNotCompleted();
		}

		testSubscriber.unsubscribe();
		testSubscriber.assertUnsubscribed();
	}
}