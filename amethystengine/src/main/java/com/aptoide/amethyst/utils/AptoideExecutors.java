package com.aptoide.amethyst.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by neuro on 22-01-2016.
 */
public class AptoideExecutors {

	private static final Executor cachedThreadPool = Executors.newCachedThreadPool();

	public static Executor getCachedThreadPool() {
		return cachedThreadPool;
	}
}
