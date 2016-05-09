/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

import com.aptoide.amethyst.BuildConfig;
import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.Sixpack;
import com.seatgeek.sixpack.SixpackBuilder;
import com.seatgeek.sixpack.log.LogLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;

public class ABTestManager {

	public static final String APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE =
			"app-view-show-security-overlay";
	private static ABTestManager instance;

	private SixpackBuilder sixpackBuilder;
	private final ExecutorService executorService;
	private final Set<ABTest<?>> tests;
	private Sixpack sixpack;

	public static ABTestManager getInstance() {
		if (instance == null) {
			instance = new ABTestManager(new SixpackBuilder(), Executors.newCachedThreadPool());
		}
		return instance;
	}

	private ABTestManager(SixpackBuilder sixpackBuilder, ExecutorService executorService) {
		this.sixpackBuilder = sixpackBuilder;
		this.executorService = executorService;
		this.tests = new HashSet<>();
	}

	public void initialize(String clientId) {
		initializeSixpack(clientId);
		registerTests();
		prefetchTests();
	}

	private void initializeSixpack(String clientId) {
		sixpack = sixpackBuilder.setSixpackUrl(HttpUrl.parse("http://10.0.2.2:5000/sixpack/"))
				.setClientId(clientId)
				.setLogLevel(BuildConfig.DEBUG ? LogLevel.VERBOSE : LogLevel.NONE)
				.build();
	}

	@SuppressWarnings("unchecked")
	private void registerTests() {
		tests.add(new ABTest(executorService, sixpack.experiment()
				.withName(APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE)
				.withAlternatives(new Alternative("false"), new Alternative("true"))
				.build(), new BooleanAlternativeParser()));
	}

	private void prefetchTests() {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				for (ABTest test: tests) {
					test.prefetch();
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> ABTest<T> get(String name) {
		for (ABTest test: tests) {
			if (test.getName().equals(name)){
				return (ABTest<T>) test;
			}
		}
		throw new IllegalArgumentException("No AB test for name: " + name);
	}
}