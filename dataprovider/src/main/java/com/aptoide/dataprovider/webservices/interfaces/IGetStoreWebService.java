package com.aptoide.dataprovider.webservices.interfaces;

import com.aptoide.dataprovider.webservices.models.Api;
import com.aptoide.dataprovider.webservices.models.BulkResponse;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by hsousa on 14/08/15.
 */
public interface IGetStoreWebService {

    @POST("/ws2.aptoide.com/api/6/getStore")
    BulkResponse.GetStore checkServer(@Body Api.GetStore body);
}