/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.dataprovider.webservices.interfaces.v7;

import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7ListSearchApps;
import com.aptoide.dataprovider.webservices.models.v7.ListSearchApps;

import retrofit.http.Body;
import retrofit.http.POST;

public interface IGetListSearchAppsV7WebService {

	@POST(Defaults.BASE_V7_URL + "/listSearchApps")
	ListSearchApps listSearchApps(@Body Apiv7ListSearchApps body);

}
