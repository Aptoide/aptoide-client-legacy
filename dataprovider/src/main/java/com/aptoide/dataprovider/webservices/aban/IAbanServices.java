package com.aptoide.dataprovider.webservices.aban;

import com.aptoide.dataprovider.webservices.models.Api;
import com.aptoide.dataprovider.webservices.models.BulkResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by diogoloureiro on 18/08/16.
 */
public interface  IAbanServices {

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @GET("/hamrahang.aban.io:8003/aban-platform/api/customer/generate/{mobilePhone}")
    AbanGeneratePassCode.Respose generatePassCode(@Path("mobilePhone") String mobilePhone, @Query("serviceId") int serviceId);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("/hamrahang.aban.io:8003/aban-platform/api/login")
    AbanLogin.Respose verifyLogin(@Body AbanLogin.Request loginBody);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @GET("/hamrahang.aban.io:8003/aban-platform/api/customer/validate/{mobilePhone}")
    AbanVerifyToken.Respose verifyToken(@Path("mobilePhone") String mobilePhone, @Header("X-Auth-Token") String authToken);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @GET("/hamrahang.aban.io:8003/aban-platform/api/13/generic/send")
    AbanRecoverPass.Respose recoverPassword(@Query("msisdn") String mobilePhone, @Query("message") String message);
}
