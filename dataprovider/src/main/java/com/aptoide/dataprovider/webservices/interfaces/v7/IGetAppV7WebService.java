package com.aptoide.dataprovider.webservices.interfaces.v7;

import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetApp;

import retrofit.http.Body;
import retrofit.http.POST;

import static com.aptoide.dataprovider.webservices.models.Defaults.BASE_V7_URL;

/**
 * Created by hsousa on 08/09/15.
 */
public interface IGetAppV7WebService {

    @POST(BASE_V7_URL + "/getApp")
    GetApp getApp(@Body Apiv7GetStore api);

    @POST(BASE_V7_URL + "/listAppsVersions")
    GetApp.Nodes.ListAppsVersions listAppsVersions(@Body Apiv7GetStore api);

}
