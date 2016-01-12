package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.json.GetUserRepoSubscriptionJson;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 16-02-2015.
 */
public class GetUserRepoSubscriptions extends RetrofitSpiceRequest<GetUserRepoSubscriptionJson, GetUserRepoSubscriptions.GetUserRepoSubscriptionWebservice> {

    public GetUserRepoSubscriptions() {
        super(GetUserRepoSubscriptionJson.class, GetUserRepoSubscriptionWebservice.class);
    }

    @Override
    public GetUserRepoSubscriptionJson loadDataFromNetwork() throws Exception {

        HashMap<String, String> parameters = new HashMap<>();
        GetUserRepoSubscriptionJson response = null;

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);
        parameters.put("mode", "json");

        try {

            response = getService().getUserRepos(parameters);

        } catch (RetrofitError error) {
            OauthErrorHandler.handle(error);
        }

        return response;

    }

    public interface GetUserRepoSubscriptionWebservice {

        @POST("/webservices.aptoide.com/webservices/3/getUserRepoSubscription")
        @FormUrlEncoded
        GetUserRepoSubscriptionJson getUserRepos(@FieldMap HashMap<String, String> args);

    }

}
