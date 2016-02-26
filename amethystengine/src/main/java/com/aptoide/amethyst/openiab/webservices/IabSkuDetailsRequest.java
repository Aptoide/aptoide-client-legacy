package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.amethyst.openiab.webservices.json.IabSkuDetailsJson;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public class IabSkuDetailsRequest extends RetrofitSpiceRequest<IabSkuDetailsJson, IabSkuDetailsRequest.Webservice> {

    private String apiVersion;
    private String token;
    private String mnc;
    private String mcc;

    private String simcc;

    private String packageName;
    private List<String> skuList = new ArrayList<String>();
    private String oemid;

    public IabSkuDetailsRequest() {
        super(IabSkuDetailsJson.class, Webservice.class);
    }




    public interface Webservice{
        @POST("/webservices.aptoide.com/webservices/3/processInAppBilling")
        @FormUrlEncoded
        IabSkuDetailsJson processInAppBilling(@FieldMap HashMap<String, String> args);
    }



    @Override
    public IabSkuDetailsJson loadDataFromNetwork() throws Exception {

        StringBuilder skus = new StringBuilder();
        for(String sku : skuList){
            skus.append(sku);
            skus.append(",");
        }

        ArrayList<WebserviceOptions> options = new ArrayList<WebserviceOptions>();
        options.add(new WebserviceOptions("package", packageName));
        options.add(new WebserviceOptions("token", token));
        if(mnc!=null)options.add(new WebserviceOptions("mnc", mnc));
        if(mcc!=null)options.add(new WebserviceOptions("mcc", mcc));
        options.add(new WebserviceOptions("oemid", oemid));
        if(simcc!=null)options.add(new WebserviceOptions("simcc", simcc.toUpperCase(Locale.ENGLISH)));
        //options.add(new WebserviceOptions("simcc", "MN"));


        options.add(new WebserviceOptions("skulist", skus.toString()));



        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(WebserviceOptions option: options){
            sb.append(option);
            sb.append(";");
        }
        sb.append(")");


        //String baseUrl = "http://dev.aptoide.com/webservices/processInAppBilling/iabskudetails/"+apiVersion+"/options="+sb.toString();
        String baseUrl = WebserviceOptions.WebServicesLink +"3/processInAppBilling";

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("mode","json");
        parameters.put("skulist",skus.toString());
        parameters.put("package",packageName);
        parameters.put("apiversion",apiVersion);
        parameters.put("reqtype","iabskudetails");

        token = SecurePreferences.getInstance().getString("access_token", null);

        parameters.put("access_token",token);

        if(mcc!=null)parameters.put("mcc",mcc);
        if(mnc!=null)parameters.put("mnc",mnc);
        if(simcc!=null)parameters.put("simcc", simcc.toUpperCase(Locale.ENGLISH));

//        HttpContent content = new UrlEncodedContent(parameters);
//
//
//        GenericUrl url = new GenericUrl(baseUrl);
//
//        Log.e("Aptoide-InappBillingRequest", baseUrl);
//        setHttpRequestFactory(AndroidHttp.newCompatibleTransport().createRequestFactory());
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
//
//        request.setParser(new JacksonFactory().createJsonObjectParser());
//        request.setUnsuccessfulResponseHandler(new OAuthRefreshAccessTokenHandler(parameters, getHttpRequestFactory()));
//
//        return request.execute().parseAs(getResultType());

        IabSkuDetailsJson response = null;

        try{
            response = getService().processInAppBilling(parameters);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }

        return response;

    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public void addToSkuList(String sku) {
        skuList.add(sku);
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public void setSimcc(String simcc) {
        this.simcc = simcc;
    }

    public void setOemid(String oemid) {
        this.oemid = oemid;
    }

}