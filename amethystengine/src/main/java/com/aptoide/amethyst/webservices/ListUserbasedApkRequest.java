package com.aptoide.amethyst.webservices;

import android.content.Context;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 15-10-2015.
 */
public class ListUserbasedApkRequest extends RetrofitSpiceRequest<ListRecomended, ListUserbasedApkRequest.Webservice> {


    String baseUrl = WebserviceOptions.WebServicesLink + "3/listUserBasedApks";


    public interface Webservice{
        @POST("/webservices.aptoide.com/webservices/3/listUserBasedApks")
        @FormUrlEncoded
        ListRecomended getRecommended(@FieldMap HashMap<String, String> args);
    }


    private String token;
    private int limit;


    public ListUserbasedApkRequest(Context context) {
        super(ListRecomended.class, Webservice.class);

    }


    @Override
    public ListRecomended loadDataFromNetwork() throws Exception {
        ArrayList<WebserviceOptions> options = new ArrayList<WebserviceOptions>();

//        GenericUrl url = new GenericUrl(baseUrl);

        HashMap<String, String > parameters = new HashMap<String, String>();

        parameters.put("mode", "json");

        options.add(new WebserviceOptions("q", AptoideUtils.HWSpecifications.filters(Aptoide.getContext())));

        if(limit>0)options.add(new WebserviceOptions("limit", String.valueOf(limit)));
        options.add(new WebserviceOptions("lang", AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext())));


        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(WebserviceOptions option: options){
            sb.append(option);
            sb.append(";");
        }
        sb.append(")");

        parameters.put("options", sb.toString());
        token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);

//        HttpContent content = new UrlEncodedContent(parameters);
//
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
//
//        request.setUnsuccessfulResponseHandler(new OAuthRefreshAccessTokenHandler(parameters, getHttpRequestFactory()));
//
//        Log.d("Aptoide-ApkUserBased", url.toString());
//        request.setParser(new JacksonFactory().createJsonObjectParser());
//
//        return request.execute().parseAs( getResultType() );

        ListRecomended response = null;

        try{
            response = getService().getRecommended(parameters);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }

        return response;

    }



    public void setLimit(int limit) {
        this.limit = limit;
    }



}

