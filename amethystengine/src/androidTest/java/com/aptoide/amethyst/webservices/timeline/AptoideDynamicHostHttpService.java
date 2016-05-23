/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 16/05/2016.
 */

package com.aptoide.amethyst.webservices.timeline;

import com.aptoide.dataprovider.AptoideSpiceHttpService;

public class AptoideDynamicHostHttpService extends AptoideSpiceHttpService {

	public static String HOST;

	@Override
	protected String getServerUrl() {
		return HOST;
	}
}
