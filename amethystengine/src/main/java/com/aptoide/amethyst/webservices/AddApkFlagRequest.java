package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 17-11-2015.
 */
public class AddApkFlagRequest extends RetrofitSpiceRequest<GenericResponseV2, AddApkFlagRequest.Webservice> {
    public interface Webservice {
        @POST("/webservices.aptoide.com/webservices/3/addApkFlag")
        @FormUrlEncoded
        GenericResponseV2 addApkFlag(@FieldMap HashMap<String, String> args);
    }


    private String token;
    private String repo;
    private String md5sum;
    private String flag;

    public AddApkFlagRequest() {
        super(GenericResponseV2.class, Webservice.class);
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {

//        GenericUrl url = new GenericUrl(baseUrl);

        HashMap<String, String > parameters = new HashMap<String, String>();


        parameters.put("repo", repo);
        parameters.put("md5sum", md5sum);
        parameters.put("flag", flag);
        parameters.put("mode", "json");

        //HttpContent content = new UrlEncodedContent(parameters);

        //HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);

        token = SecurePreferences.getInstance().getString(Constants.ACCESS_TOKEN, "empty");

        parameters.put("access_token", token);

        GenericResponseV2 genericResponseV2 = null;


        try{
            genericResponseV2 = getService().addApkFlag(parameters);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }
        //request.setUnsuccessfulResponseHandler(new OAuthRefreshAccessTokenHandler(parameters, getHttpRequestFactory()));
        //request.setParser(new JacksonFactory().createJsonObjectParser());


        return genericResponseV2;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
