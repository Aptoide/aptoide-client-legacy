package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.amethyst.webservices.OauthErrorHandler;

import java.util.HashMap;

import com.aptoide.amethyst.openiab.webservices.json.IabAvailableJson;
import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class IabAvailableRequest extends BaseRequest<IabAvailableJson, IabAvailableRequest.Webservice> {
    public IabAvailableRequest() {
        super(IabAvailableJson.class, Webservice.class);
    }

    @Override
    public IabAvailableJson loadDataFromNetwork() throws Exception {
        //GenericUrl url = getURL();

        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("apiversion", apiVersion);
        parameters.put("reqtype", "iabavailable");
        parameters.put("mode", "json");
        parameters.put("package", packageName);


//        HttpContent content = new UrlEncodedContent(parameters);
//
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
//        request.setUnsuccessfulResponseHandler(new OAuthRefreshAccessTokenHandler(parameters, getHttpRequestFactory()));
//
//        request.setParser(new JacksonFactory().createJsonObjectParser());
//
//        return request.execute().parseAs(getResultType());

        IabAvailableJson response = null;

        try {
            response = getService().processInAppBilling(parameters);
        } catch (RetrofitError error) {
            OauthErrorHandler.handle(error);
        }

        return response;
    }

    public interface Webservice {
        @POST("/webservices.aptoide.com/webservices/3/processInAppBilling")
        @FormUrlEncoded
        IabAvailableJson processInAppBilling(@FieldMap HashMap<String, String> args);
    }

}
