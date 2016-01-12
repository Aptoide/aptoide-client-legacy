package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.LoginActivity;
import com.aptoide.amethyst.model.json.OAuth;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.octo.android.robospice.retry.RetryPolicy;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 03-07-2014.
 */
public class OAuth2AuthenticationRequest extends RetrofitSpiceRequest<OAuth, OAuth2AuthenticationRequest.Webservice> {

    private String username;
    private String password;
    private LoginActivity.Mode mode;
    private String nameForGoogle;

    public OAuth2AuthenticationRequest(){
        super(OAuth.class, Webservice.class);
    }


    public interface Webservice{
        @POST(WebserviceOptions.WebServicesLink+"3/oauth2Authentication")
        @FormUrlEncoded
        OAuth oauth2Authentication(@FieldMap HashMap<String, String> args);
    }


    @Override
    public OAuth loadDataFromNetwork() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("grant_type", "password");
        parameters.put("client_id", "Aptoide");
        parameters.put("mode", "json");

        switch (mode){
            case APTOIDE:
                parameters.put("username", username);
                parameters.put("password", password);
                break;
            case GOOGLE:
                parameters.put("authMode", "google");
                parameters.put("oauthUserName", nameForGoogle);
                parameters.put("oauthToken", password);
                break;
            case FACEBOOK:
                parameters.put("authMode", "facebook");
                parameters.put("oauthToken", password);
                break;
        }

        if(Aptoide.getConfiguration().getExtraId().length()>0){
            parameters.put("oem_id", Aptoide.getConfiguration().getExtraId());
        }

//        HttpContent content = new UrlEncodedContent(parameters);
//        GenericUrl url = new GenericUrl(WebserviceOptions.WebServicesLink+"3/oauth2Authentication");
//        HttpRequest oauth2RefresRequest = getHttpRequestFactory().buildPostRequest(url, content);
//        oauth2RefresRequest.setParser(new JacksonFactory().createJsonObjectParser());
//
//
//        oauth2RefresRequest.setUnsuccessfulResponseHandler(new OAuthAccessTokenHandler());
//
//        HttpResponse response;
//
//        try{
//            response = oauth2RefresRequest.execute();
//        }catch (InvalidGrantException e){
//                    setRetryPolicy(noRetry);
//                throw new InvalidGrantSpiceException(e.getError_description());
//        }catch (IOException e){
//            if("No authentication challenges found".equals(e.getMessage())){
//                setRetryPolicy(noRetry);
//                throw new InvalidGrantSpiceException("Invalid username and password combination");
//            }else{
//                throw e;
//            }
//        }


//        return response.parseAs(OAuth.class);

        OAuth response = null;

        try{
            response = getService().oauth2Authentication(parameters);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }

        return response;

    }

    RetryPolicy noRetry = new RetryPolicy() {
        @Override
        public int getRetryCount() {
            return 0;
        }

        @Override
        public void retry(SpiceException e) {

        }

        @Override
        public long getDelayBeforeRetry() {
            return 0;
        }
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMode(LoginActivity.Mode mode) {
        this.mode = mode;
    }

    public void setNameForGoogle(String nameForGoogle) {
        this.nameForGoogle = nameForGoogle;
    }
}
