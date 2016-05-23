/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 29/04/2016.
 */

package com.aptoide.amethyst.social;

import android.content.Context;

import com.aptoide.amethyst.webservices.timeline.ListUserContactsFriendsRequest;
import com.aptoide.amethyst.webservices.timeline.json.ListUserFriendsJson;
import com.octo.android.robospice.SpiceManager;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Repository for logged user friends also users of Aptoide.
 */
public class AptoideFriendRepository {

	private final Context context;
	private final SpiceManager spiceManager;

	public AptoideFriendRepository(Context context, SpiceManager spiceManager) {
		this.context = context;
		this.spiceManager = spiceManager;
	}

	public Observable<AptoideFriends> getFriends(List<SimpleContact> contacts, int limit, int
			offset) {
		return Observable.create(new OnSubscribeExecuteSpiceRequest<>(context,
				spiceManager, new ListUserContactsFriendsRequest(contacts, limit, offset)))
				.map(new ConvertToAptoideFriends());
	}

	private static class ConvertToAptoideFriends implements Func1<ListUserFriendsJson, AptoideFriends> {
		@Override
		public AptoideFriends call(ListUserFriendsJson listUserFriendsJson) {
			return listUserFriendsJson.getAptoideFriends();
		}
	}
}