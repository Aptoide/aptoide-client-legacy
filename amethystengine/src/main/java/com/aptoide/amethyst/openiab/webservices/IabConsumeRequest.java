package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;

import java.util.HashMap;

import com.aptoide.amethyst.openiab.webservices.json.IabConsumeJson;
import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class IabConsumeRequest extends BaseRequest<IabConsumeJson, IabConsumeRequest.Webservice> {
    private String purchaseToken;

    public IabConsumeRequest() {
        super(IabConsumeJson.class, Webservice.class);
    }

    @Override
    public IabConsumeJson loadDataFromNetwork() throws Exception {
        //GenericUrl url = getURL();

        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("apiversion", apiVersion);
        parameters.put("reqtype", "iabconsume");
        parameters.put("purchasetoken", purchaseToken);

        token = SecurePreferences.getInstance().getString("access_token", null);

        parameters.put("access_token", token);
        parameters.put("mode", "json");
//        HttpContent content = new UrlEncodedContent(parameters);
//
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url,  content);
//        request.setUnsuccessfulResponseHandler(new OAuthRefreshAccessTokenHandler(parameters, getHttpRequestFactory()));
//
//        request.setParser(new JacksonFactory().createJsonObjectParser());
//
//        HttpResponse response;
//        try{
//            response = request.execute();
//        } catch (EOFException e){
//
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.put("Connection", "close");
//            request.setHeaders(httpHeaders);
//            response = request.execute();
//        }
//
//        return response.parseAs(getResultType());


        IabConsumeJson response = null;

        try {
            response = getService().processInAppBilling(parameters);
        } catch (RetrofitError error) {
            OauthErrorHandler.handle(error);
        }

        return response;

    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public interface Webservice {
        @POST("/webservices.aptoide.com/webservices/3/processInAppBilling")
        @FormUrlEncoded
        IabConsumeJson processInAppBilling(@FieldMap HashMap<String, String> args);
    }
}
