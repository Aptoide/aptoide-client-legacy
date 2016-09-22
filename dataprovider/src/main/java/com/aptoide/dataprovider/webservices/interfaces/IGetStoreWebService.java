package com.aptoide.dataprovider.webservices.interfaces;

import com.aptoide.dataprovider.webservices.models.Api;
import com.aptoide.dataprovider.webservices.models.BulkResponse;

import com.aptoide.dataprovider.webservices.models.Defaults;
import retrofit.http.Body;
import retrofit.http.POST;

import static com.aptoide.dataprovider.webservices.models.Defaults.BASE_V6_URL;

/**
 * Created by hsousa on 14/08/15.
 */
public interface IGetStoreWebService {

    @POST(BASE_V6_URL+"/getStore")
    BulkResponse.GetStore checkServer(@Body Api.GetStore body);
}