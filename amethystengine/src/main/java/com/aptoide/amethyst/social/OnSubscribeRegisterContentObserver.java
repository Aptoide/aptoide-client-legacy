/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 09/05/2016.
 */

package com.aptoide.amethyst.social;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class OnSubscribeRegisterContentObserver implements Observable.OnSubscribe<Void> {

	private ContentResolver contentResolver;
	private Uri uri;
	private final boolean notifyForDescendents;
	private final Handler schedulerHandler;

	public OnSubscribeRegisterContentObserver(ContentResolver contentResolver, Uri uri, boolean
			notifyForDescendents, Handler schedulerHandler) {
		this.contentResolver = contentResolver;
		this.uri = uri;
		this.notifyForDescendents = notifyForDescendents;
		this.schedulerHandler = schedulerHandler;
	}

	@Override
	public void call(final Subscriber<? super Void> subscriber) {

		final ContentObserver observer = new ContentObserver(schedulerHandler) {
			@Override
			public void onChange(boolean selfChange) {
				if (!subscriber.isUnsubscribed()) {
					if (!selfChange) {
						subscriber.onNext(null);
					}
				}
			}
		};
		contentResolver.registerContentObserver(uri, notifyForDescendents, observer);

		subscriber.add(Subscriptions.create(new Action0() {
			@Override
			public void call() {
				contentResolver.unregisterContentObserver(observer);
			}
		}));

		// Force first emission
		contentResolver.notifyChange(uri, null);
	}
}
