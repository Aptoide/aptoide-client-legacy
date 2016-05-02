/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 02/05/2016.
 */

package com.aptoide.amethyst.social;

import java.util.List;

public interface ContactsProvider {

  List<SimpleContact> getDeviceContacts();
}
