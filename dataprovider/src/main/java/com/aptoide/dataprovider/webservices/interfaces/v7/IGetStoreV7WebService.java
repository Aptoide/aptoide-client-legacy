package com.aptoide.dataprovider.webservices.interfaces.v7;

import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets;
import com.aptoide.dataprovider.webservices.models.v7.ListViewItems;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by hsousa on 08/09/15.
 */
public interface IGetStoreV7WebService {

    @POST("/ws2.aptoide.com/api/7/getStore")
    GetStore getStore(@Body Apiv7GetStore api);

    @POST("/{url}")
    GetStoreWidgets postStoreWidget(@Path(value = "url", encode = false) String path, @Body Apiv7GetStore api);

    @POST("/{url}")
    ListViewItems postViewItems(@Path(value = "url", encode = false) String path, @Body Apiv7GetStore api);

}
