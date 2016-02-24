package com.aptoide.amethyst.webservices.timeline;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import com.aptoide.amethyst.webservices.timeline.json.ListUserFriendsJson;
import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 14-10-2015.
 */
public class ListUserFriendsRequest extends RetrofitSpiceRequest<ListUserFriendsJson, ListUserFriendsRequest.ListUserFriends> {
    private int limit;
    private int offset;

    public interface ListUserFriends {
        @POST(WebserviceOptions.WebServicesLink+"3/listUserFriends")
        @FormUrlEncoded
        public ListUserFriendsJson run(@FieldMap HashMap<String, String> args);
    }

    public ListUserFriendsRequest() {
        super(ListUserFriendsJson.class, ListUserFriends.class);
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public ListUserFriendsJson loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("limit", String.valueOf(limit));
        parameters.put("offset", String.valueOf(offset));

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);


        try{
            return getService().run(parameters);
        }catch (RetrofitError e){
            OauthErrorHandler.handle(e);
        }

        return null;
    }
}

