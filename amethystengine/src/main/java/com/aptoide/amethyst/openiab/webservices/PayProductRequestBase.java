package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import com.aptoide.amethyst.openiab.webservices.json.IabPurchaseStatusJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by asantos on 15-09-2014.
 */
public abstract class PayProductRequestBase extends BaseRequest<IabPurchaseStatusJson, PayProductRequestBase.Webservice > {
    private String productId;
    private String oemId;
    private String repo;
    private String developerPayload;
    private String price;
    private String currency;


    public interface Webservice{
        @POST("/webservices.aptoide.com/webservices/3/payProduct")
        @FormUrlEncoded
        IabPurchaseStatusJson payProduct(@FieldMap HashMap<String, String> args);
    }

//    @Override
//    protected GenericUrl getURL() {
//        String baseUrl = WebserviceOptions.WebServicesLink + "3/payProduct";
//        return new GenericUrl(baseUrl);
//    }

    public PayProductRequestBase() {
        super(IabPurchaseStatusJson.class, Webservice.class);
    }

    protected abstract void optionsAddExtra(List<WebserviceOptions> options);
    protected abstract void parametersputExtra(Map<String, String> parameters);

    @Override
    public IabPurchaseStatusJson loadDataFromNetwork() throws Exception {

        ArrayList<WebserviceOptions> options = new ArrayList<WebserviceOptions>();
        options.add(new WebserviceOptions("oemid", oemId));
        options.add(new WebserviceOptions("developerPayload", developerPayload));
        optionsAddExtra(options);

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(WebserviceOptions option: options){
            sb.append(option);
            sb.append(";");
        }
        sb.append(")");

//        GenericUrl url = getURL();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("mode","json");
        parameters.put("developerPayload",developerPayload);
        parameters.put("productid",productId);
        parameters.put("apiversion",apiVersion);
        parameters.put("reqType","billing");
        parameters.put("repo",repo);
        parameters.put("price",price);
        parameters.put("currency",currency);
        parameters.put("oemid",oemId);
        parametersputExtra(parameters);

        token = SecurePreferences.getInstance().getString("access_token", null);


        parameters.put("access_token", token);

        IabPurchaseStatusJson response = null;

        try{
            response = getService().payProduct(parameters);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }

        return response;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOemId() {
        return oemId;
    }

    public void setOemId(String oemId) {
        this.oemId = oemId;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
