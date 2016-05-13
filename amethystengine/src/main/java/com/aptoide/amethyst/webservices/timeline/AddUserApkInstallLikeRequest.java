package com.aptoide.amethyst.webservices.timeline;

/**
 * Created by fabio on 14-10-2015.
 */


import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by asantos on 24-09-2014.
 */
public class AddUserApkInstallLikeRequest extends RetrofitSpiceRequest<GenericResponseV2, AddUserApkInstallLikeRequest.AddUserApkInstallLike> {
    public static final String LIKE = "like";
    public static final String DISLIKE = "dislike";
    public static final String UNLIKE = "unlike";
    private long postID;
    private String like;

    public void setPostId(long postID) {	this.postID = postID;	}
    public void setLike(String s) {	this.like = s;	}

    public AddUserApkInstallLikeRequest() { super(GenericResponseV2.class, AddUserApkInstallLike.class);   }


    public interface AddUserApkInstallLike {
        @POST(WebserviceOptions.WebServicesLink+"3/addUserApkInstallLike")
        @FormUrlEncoded
        public GenericResponseV2 run(@FieldMap HashMap<String, String> args);
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);
        parameters.put("id", String.valueOf(postID));
        parameters.put("like", like);

        try{
            return getService().run(parameters);
        }catch (RetrofitError e){
            OauthErrorHandler.handle(e);
        }

        return null;

    }

}