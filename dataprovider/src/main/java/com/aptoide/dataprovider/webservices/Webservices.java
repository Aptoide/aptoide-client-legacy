package com.aptoide.dataprovider.webservices;

import com.aptoide.dataprovider.exceptions.TicketException;
import com.aptoide.dataprovider.webservices.models.Api;
import com.aptoide.dataprovider.webservices.models.BulkResponse;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.UpdatesApi;
import com.aptoide.dataprovider.webservices.models.UpdatesResponse;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 01/06/15.
 */
public interface Webservices {


    String ws = "/webservices.aptoide.com/webservices";


    @POST(Defaults.BASE_V6_URL + "/bulkRequest/api_list/getStore,listApps/")
    BulkResponse bulkGetStoreListAppsRequest(@Body Api user) throws TicketException;

    @POST(Defaults.BASE_V6_URL + "/bulkRequest/api_list/getStore,listApps,listStores/")
    BulkResponse bulkTopRequest(@Body Api user) throws TicketException;



    @POST(Defaults.BASE_V6_URL + "/listAppsUpdates")
    UpdatesResponse getUpdates(@Body UpdatesApi api );


}
