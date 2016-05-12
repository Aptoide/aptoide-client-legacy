/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 11/05/2016.
 */

package com.aptoide.amethyst.social;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class OnSubscribeExecuteSpiceRequest<T> implements Observable.OnSubscribe<T> {

	private final Context context;
	private final SpiceManager spiceManager;
	private final SpiceRequest<T> spiceRequest;

	public OnSubscribeExecuteSpiceRequest(Context context, SpiceManager spiceManager,
										  SpiceRequest<T> spiceRequest) {
		this.context = context;
		this.spiceManager = spiceManager;
		this.spiceRequest = spiceRequest;
	}

	@Override
	public void call(final Subscriber<? super T> subscriber) {
		final RequestListener<T> listener = new RequestListener<T>() {
			@Override
			public void onRequestFailure(SpiceException spiceException) {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onError(spiceException);
				}
			}

			@Override
			public void onRequestSuccess(T t) {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onNext(t);
				}
			}
		};

		synchronized (spiceManager) {
			if (!spiceManager.isStarted()) {
				spiceManager.start(context);
			}
		}

		subscriber.add(Subscriptions.create(new Action0() {
			@Override
			public void call() {
				synchronized (spiceManager) {
					if (spiceManager.isStarted()) {
						spiceManager.shouldStop();
					}
				}
			}
		}));

		spiceManager.execute(spiceRequest, listener);
	}
}
