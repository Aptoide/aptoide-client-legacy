package com.aptoide.amethyst.webservices.timeline;

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
 * Created by fabio on 14-10-2015.
 */
public class AddUserApkInstallCommentRequest extends RetrofitSpiceRequest<GenericResponseV2, AddUserApkInstallCommentRequest.AddUserApkInstallComment> {
    private long postID;
    private String comment;

    public void setPostID(long id){		this.postID = id;	}
    public void setComment(String comment) {    this.comment = comment; }

    public AddUserApkInstallCommentRequest() {     super(GenericResponseV2.class, AddUserApkInstallComment.class);   }

    public interface AddUserApkInstallComment{
        @POST(WebserviceOptions.WebServicesLink+"3/addUserApkInstallComment")
        @FormUrlEncoded
        public GenericResponseV2 run(@FieldMap HashMap<String, String> args);
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("id", String.valueOf(postID));
        parameters.put("text", comment);

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
