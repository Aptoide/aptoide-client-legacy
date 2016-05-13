package com.aptoide.amethyst.webservices.timeline;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import com.aptoide.amethyst.webservices.timeline.json.ApkInstallComments;
import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 14-10-2015.
 */
public class GetUserApkInstallCommentsRequest extends RetrofitSpiceRequest<ApkInstallComments, GetUserApkInstallCommentsRequest.GetUserApkInstallComments> {

    private long postID;
    private int limit;
    private int offset;
    public void setPostID(long id) { this.postID = id; }
    public void setPostLimit(int limit) { this.limit = limit; }
    public void setPostOffSet(int offset) { this.offset = offset; }
    public GetUserApkInstallCommentsRequest() {    super(ApkInstallComments.class, GetUserApkInstallComments.class);    }

    public interface GetUserApkInstallComments{
        @POST(WebserviceOptions.WebServicesLink+"3/getUserApkInstallComments")
        @FormUrlEncoded
        public ApkInstallComments run(@FieldMap HashMap<String, String> args);
    }

    @Override
    public ApkInstallComments loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("id", String.valueOf(postID));
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
