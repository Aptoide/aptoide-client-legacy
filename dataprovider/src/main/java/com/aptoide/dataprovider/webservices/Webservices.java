package com.aptoide.dataprovider.webservices;

import com.aptoide.dataprovider.exceptions.TicketException;
import com.aptoide.dataprovider.webservices.models.Api;
import com.aptoide.dataprovider.webservices.models.BulkResponse;
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


    String ws2 = "/ws2.aptoide.com/api/6";
    String ws = "/webservices.aptoide.com/webservices";


    @POST(ws2 + "/bulkRequest/api_list/getStore,listApps/")
    BulkResponse bulkGetStoreListAppsRequest(@Body Api user) throws TicketException;

    @POST(ws2 + "/bulkRequest/api_list/getStore,listApps,listStores/")
    BulkResponse bulkTopRequest(@Body Api user) throws TicketException;



    @POST("/ws2.aptoide.com/api/6/listAppsUpdates")
    UpdatesResponse getUpdates(@Body UpdatesApi api );


}
