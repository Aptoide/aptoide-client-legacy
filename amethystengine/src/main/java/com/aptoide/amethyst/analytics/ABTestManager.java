/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

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

	private Sixpack sixpack;
	private final ExecutorService executorService;
	private Set<ABTest> tests;

	public static ABTestManager getInstance() {
		if (instance == null) {
			instance = new ABTestManager(new SixpackBuilder()
					.setSixpackUrl(HttpUrl.parse("http://10.0.2.2:5000/sixpack/"))
					.setClientId("1234")
					.setLogLevel(LogLevel.VERBOSE)
					.build(), Executors.newCachedThreadPool());
		}
		return instance;
	}

	private ABTestManager(Sixpack sixpack, ExecutorService executorService) {
		this.sixpack = sixpack;
		this.executorService = executorService;
		this.tests = new HashSet<>();
	}

	public void initialize() {
		registerTests();
		prefetchTests();
	}

	@SuppressWarnings("unchecked")
	private void registerTests() {
		tests.add(new ABTest(executorService, sixpack.experiment()
				.withName(APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE)
				.withAlternatives(
					new Alternative("true"),
					new Alternative("false"))
				.build(), new BooleanAlternativeConverter()));
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