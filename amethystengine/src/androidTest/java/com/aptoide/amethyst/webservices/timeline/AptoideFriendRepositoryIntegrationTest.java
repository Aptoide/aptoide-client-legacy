/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 16/05/2016.
 */

package com.aptoide.amethyst.webservices.timeline;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.aptoide.amethyst.social.AptoideFriendRepository;
import com.aptoide.amethyst.social.AptoideFriends;
import com.aptoide.amethyst.social.SimpleContact;
import com.aptoide.amethyst.webservices.timeline.json.Friend;
import com.octo.android.robospice.SpiceManager;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AptoideFriendRepositoryIntegrationTest {

	private AptoideFriendRepository friendRepository;

	@Before
	public void setUp() throws Exception {
		friendRepository = new AptoideFriendRepository(InstrumentationRegistry.getInstrumentation()
				.getTargetContext(), new SpiceManager(AptoideDynamicHostHttpService.class));
	}

	@Test
	public void getContactsFriends() throws Exception {

		final MockWebServer server = new MockWebServer();
		server.enqueue(new MockResponse().setResponseCode(200).setBody("{  \n" +
				"   \"userfriends\":{  \n" +
				"      \"timeline_inactive\":[  \n" +
				"\n" +
				"      ],\n" +
				"      \"timeline_active\":[  \n" +
				"         {  \n" +
				"            \"username\":\"marcelo\",\n" +
				"            \"avatar\":\"http://marcelo.avatar/avatar.png\",\n" +
				"            \"email\":\"marcelo.benites@aptoide.com\"\n" +
				"         },\n" +
				"         {  \n" +
				"            \"username\":\"frederico\",\n" +
				"            \"avatar\":\"http://frederico.avatar/avatar.png\",\n" +
				"            \"email\":\"frederico@aptoide.com\"\n" +
				"         }\n" +
				"      ]\n" +
				"   }\n" +
				"}"));
		server.play();

		AptoideDynamicHostHttpService.HOST = server.getUrl("/").toString();
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final TestSubscriber<AptoideFriends> testSubscriber = new TestSubscriber<AptoideFriends>
				() {
			@Override
			public void onCompleted() {
				super.onCompleted();
				countDownLatch.countDown();
			}

			@Override
			public void onNext(AptoideFriends friends) {
				super.onNext(friends);
				countDownLatch.countDown();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				countDownLatch.countDown();
			}
		};

		final List<SimpleContact> contactList = Arrays.asList(getSimpleContact("marcelo" +
				".benites@aptoide.com", "+5551555555555"), getSimpleContact("frederico@aptoide" +
				".com", "+55515555444"));
		friendRepository.getFriends(contactList, 150, 0).subscribe(testSubscriber);
		countDownLatch.await();
		testSubscriber.assertValue(new AptoideFriends(Collections.<Friend>emptyList(), Arrays
				.asList(new Friend("marcelo", "http://marcelo.avatar/avatar.png", "marcelo" + "" +
						".benites@aptoide.com"), new Friend("frederico", "http://frederico" + "" +
						".avatar/avatar.png", "frederico@aptoide.com"))));
		testSubscriber.assertNoErrors();
		testSubscriber.unsubscribe();

		final RecordedRequest request = server.takeRequest();
		assertEquals("mode=json&offset=0&limit=150&email=marcelo.benites%40aptoide.com&email=frederico%40aptoide.com&phone=%2B5551555555555&phone=%2B55515555444", new String(request.getBody(), "UTF-8"));
		assertEquals("POST", request.getMethod());
		server.shutdown();
	}

	@NonNull
	private SimpleContact getSimpleContact(String email, String phoneNumber) {
		return new SimpleContact(Collections.singletonList(email), Collections.singletonList
				(phoneNumber));
	}
}
