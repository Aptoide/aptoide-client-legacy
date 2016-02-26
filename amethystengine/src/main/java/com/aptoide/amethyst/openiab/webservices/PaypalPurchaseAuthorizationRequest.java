package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.aptoide.amethyst.openiab.webservices.json.IabSimpleResponseJson;
import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public class PaypalPurchaseAuthorizationRequest extends RetrofitSpiceRequest<IabSimpleResponseJson, PaypalPurchaseAuthorizationRequest.Webservice> {

    private String token;
    private String authToken;

    public interface Webservice{
        @POST("/webservices.aptoide.com/webservices/3/productPurchaseAuthorization")
        @FormUrlEncoded
        IabSimpleResponseJson productPurchaseAuthorization(@FieldMap HashMap<String, String> args);
    }


    public PaypalPurchaseAuthorizationRequest() {
        super(IabSimpleResponseJson.class, Webservice.class);
    }

    @Override
    public IabSimpleResponseJson loadDataFromNetwork() throws Exception {


        List<WebserviceOptions> options = new ArrayList<WebserviceOptions>();
        options.add(new WebserviceOptions("reqType", "rest"));

        if(authToken!=null){
            options.add(new WebserviceOptions("authToken", authToken));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(WebserviceOptions option: options){
            sb.append(option);
            sb.append(";");
        }
        sb.append(")");

        //String baseUrl = "http://dev.aptoide.com/webservices/productPurchaseAuthorization/"+token+"/1/options="+sb.toString();
        String baseUrl = WebserviceOptions.WebServicesLink + "3/productPurchaseAuthorization";

//        GenericUrl url = new GenericUrl(baseUrl);

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("mode","json");
        parameters.put("reqType","rest");
        parameters.put("payType","1");
        parameters.put("authToken",authToken);

        token = SecurePreferences.getInstance().getString("access_token", null);

        parameters.put("access_token",token);

//        HttpContent content = new UrlEncodedContent(parameters);
//
//        Log.e("Aptoide-InappBillingRequest", baseUrl);
//        //setHttpRequestFactory(AndroidHttp.newCompatibleTransport().createRequestFactory());
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
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

        IabSimpleResponseJson response = null;

        try{
            response = getService().productPurchaseAuthorization(parameters);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }

        return response;

    }

  

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

}
