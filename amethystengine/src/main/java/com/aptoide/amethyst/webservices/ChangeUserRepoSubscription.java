package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 02-03-2015.
 */
public class ChangeUserRepoSubscription extends RetrofitSpiceRequest<GenericResponseV2, ChangeUserRepoSubscription.ChangeUserRepoSubscriptionWebservice> {


    private ArrayList<RepoSubscription> repos;
    private RepoSubscription repoSubscription;


    public static class RepoSubscription {

        private String name;
        private boolean subscribed;

        public RepoSubscription(String name, boolean subscribed) {
            this.name = name;
            this.subscribed = subscribed;
        }

    }


    public void setRepoSubscription(RepoSubscription repoSubscription){
        this.repoSubscription = repoSubscription;
    }

    public interface ChangeUserRepoSubscriptionWebservice{
        @POST(WebserviceOptions.WebServicesLink+"3/changeUserRepoSubscription")
        @FormUrlEncoded
        GenericResponseV2 run(@FieldMap HashMap<String, String> args);
    }

    public ChangeUserRepoSubscription() {
        super( GenericResponseV2.class, ChangeUserRepoSubscriptionWebservice.class );
        repos = new ArrayList<>();
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {

        HashMap<String, String > parameters = new HashMap<>();
        parameters.put("mode" , "json");
        parameters.put("repo", repoSubscription.name);
        parameters.put("status", repoSubscription.subscribed ? "subscribed" : "unsubscribed");

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);

        try {
            return getService().run(parameters);
        } catch (RetrofitError e) {
            OauthErrorHandler.handle(e);
            Logger.printException(e);
        }

        return null;
    }

}
