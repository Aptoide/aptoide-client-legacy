package com.aptoide.amethyst.webservices.timeline;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import com.aptoide.amethyst.webservices.timeline.json.ListapklikesJson;
import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 14-10-2015.
 */
public class ListUserapklikesRequest extends RetrofitSpiceRequest<ListapklikesJson, ListUserapklikesRequest.ListUserapklikes> {

    private int limit;
    private long postID;

    public interface ListUserapklikes {
        @POST(WebserviceOptions.WebServicesLink+"3/listUserApkInstallLikes")
        @FormUrlEncoded
        public ListapklikesJson run(@FieldMap HashMap<String, String> args);
    }
    public ListUserapklikesRequest() {
        super(ListapklikesJson.class, ListUserapklikes.class);
    }
    public void setPostID(long id){		this.postID = id;	}
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public ListapklikesJson loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("post_id", String.valueOf(postID));
        parameters.put("limit", String.valueOf(limit));

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