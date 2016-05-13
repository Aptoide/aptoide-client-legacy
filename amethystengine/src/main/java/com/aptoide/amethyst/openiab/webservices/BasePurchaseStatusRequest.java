package com.aptoide.amethyst.openiab.webservices;

import android.text.TextUtils;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import com.aptoide.amethyst.openiab.webservices.json.IabPurchaseStatusJson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public abstract class BasePurchaseStatusRequest extends BaseRequest<IabPurchaseStatusJson, BasePurchaseStatusRequest.Webservice> {
    private int orderId;
    private int productId;
    private int payType;
    private double taxRate;
    private double price;
    private String currency;
    private String payKey;
    private String developerPayload;
    private String simcc;
    private String repo;

    public void setPayreqtype(String payreqtype) {
        this.payreqtype = payreqtype;
    }

    private String payreqtype;

    public interface Webservice{
        @POST("/webservices.aptoide.com/webservices/3/processInAppBilling")
        @FormUrlEncoded
        IabPurchaseStatusJson processInAppBilling(@FieldMap HashMap<String, String> args);

        @POST("/webservices.aptoide.com/webservices/3/checkProductPayment")
        @FormUrlEncoded
        IabPurchaseStatusJson checkProductPayment(@FieldMap HashMap<String, String> args);
    }


    public BasePurchaseStatusRequest() {
        super(IabPurchaseStatusJson.class, Webservice.class);
        payreqtype=null;
    }

//    protected abstract GenericUrl getURL();

    @Override
    public IabPurchaseStatusJson loadDataFromNetwork() throws Exception {

        ArrayList<WebserviceOptions> options = new ArrayList<WebserviceOptions>();
        options.add(new WebserviceOptions("token", token));

//        if(!rest){
//            options.add(new WebserviceOptions("orderid", token));
//        }else{
//            if(developerPayload!=null && !developerPayload.isEmpty()) options.add(new WebserviceOptions("developerPayload", developerPayload));
//            options.add(new WebserviceOptions("paykey", payKey));
//            options.add(new WebserviceOptions("productID", String.valueOf(productId)));
//            options.add(new WebserviceOptions("payType", String.valueOf(payType)));
//            options.add(new WebserviceOptions("taxRate", String.valueOf(taxRate)));
//            options.add(new WebserviceOptions("price", String.valueOf(price)));
//            options.add(new WebserviceOptions("currency", currency));
//            options.add(new WebserviceOptions("reqType", "rest"));
//            if(simcc!=null)options.add(new WebserviceOptions("simcc", simcc));
//
//
//        }
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("(");
//        for(WebserviceOptions option: options){
//            sb.append(option);
//            sb.append(";");
//        }
//        sb.append(")");


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("mode","json");
        parameters.put("apiversion", String.valueOf(apiVersion));
        parameters.put("reqtype",getReqType());
        parameters.put("paykey", payKey);
        if(payreqtype==null)
            payreqtype = "rest";
        parameters.put("payreqtype",payreqtype);
        parameters.put("paytype", String.valueOf(payType));

        parameters.put("repo", repo);
        parameters.put("taxrate", String.valueOf(taxRate));
        parameters.put("productid", String.valueOf(productId));
        parameters.put("price", String.valueOf(price));

        token = SecurePreferences.getInstance().getString("access_token", null);

        parameters.put("access_token",token);
        parameters.put("currency",currency);
        parameters.put("simcc",simcc);

        if(developerPayload!=null && !TextUtils.isEmpty(developerPayload)) parameters.put("developerPayload", developerPayload);



//        HttpContent content = new UrlEncodedContent(parameters);
//
//
//        GenericUrl url = getURL();
//
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
//        request.setUnsuccessfulResponseHandler(new OAuthRefreshAccessTokenHandler(parameters, getHttpRequestFactory()));
//
//        request.setParser(new JacksonFactory().createJsonObjectParser());
//
//        HttpResponse response;
//        try{
//            response = request.execute();
//        } catch (EOFException e ){
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.put("Connection", "close");
//            request.setHeaders(httpHeaders);
//            response = request.execute();
//        }
//
//        return response.parseAs(getResultType());

        IabPurchaseStatusJson response = null;

        try{

            response = executeRequest(getService(), parameters);

        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }

        return response;

    }

    abstract IabPurchaseStatusJson executeRequest(Webservice webervice, HashMap<String, String> parameters);

    abstract String getReqType();

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPayType() {
        return payType;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }

    public String getPayKey() {
        return payKey;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public void setSimcc(String simcc) {
        this.simcc = simcc;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

}
