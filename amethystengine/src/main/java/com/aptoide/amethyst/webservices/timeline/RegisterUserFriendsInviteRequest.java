package com.aptoide.amethyst.webservices.timeline;

import android.text.TextUtils;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 14-10-2015.
 */
public class RegisterUserFriendsInviteRequest  extends RetrofitSpiceRequest<GenericResponseV2, RegisterUserFriendsInviteRequest.RegisterUserFriendsInvite> {

    ArrayList<String> list;

    public void addEmail(String value) {
        list.add("f"+(list.size()+1)+"=" + value);
    }

    public interface RegisterUserFriendsInvite{
        @POST(WebserviceOptions.WebServicesLink+"3/registerUserFriendsInvite")
        @FormUrlEncoded
        public GenericResponseV2 run(@FieldMap HashMap<String, String> args);
    }

    public RegisterUserFriendsInviteRequest() {
        super(GenericResponseV2.class, RegisterUserFriendsInvite.class );
        list= new ArrayList<String>();
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("friends", TextUtils.join(";", list));

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);

        try{
            return getService().run(parameters);
        }catch (RetrofitError e){
            OauthErrorHandler.handle(e);
        }

        return getService().run(parameters);

    }
}

