/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 29/04/2016.
 */

package com.aptoide.amethyst.social;

import java.util.List;

import rx.Observable;

/**
 * Repository for logged user friends also users of Aptoide.
 */
public interface AptoideFriendRepository {


	Observable<AptoideFriends> getFriends(List<SimpleContact> contacts);

}
