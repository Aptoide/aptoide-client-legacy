/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 29/04/2016.
 */

package com.aptoide.amethyst.social;

import java.util.List;

/**
 * Repository for logged user friends also users of Aptoide.
 */
public interface AptoideFriendRepository {


	AptoideFriends getFriends(List<SimpleContact> contacts);

}
