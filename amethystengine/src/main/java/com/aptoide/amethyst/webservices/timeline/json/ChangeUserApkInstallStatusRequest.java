package com.aptoide.amethyst.webservices.timeline.json;

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
public class ChangeUserApkInstallStatusRequest extends RetrofitSpiceRequest<GenericResponseV2, ChangeUserApkInstallStatusRequest.ChangeUserApkInstallStatus> {

    public static final String STATUSACTIVE = "active";
    public static final String STATUSHIDDEN = "hidden";

    private long postID;
    public void setPostId(long id){this.postID = id;}
    private String status;

    public interface ChangeUserApkInstallStatus{
        @POST(WebserviceOptions.WebServicesLink+"3/changeUserApkInstallStatus")
        @FormUrlEncoded
        public GenericResponseV2 run(@FieldMap HashMap<String, String> args );
    }

    public void setPostStatus(String status){this.status = status;}

    public ChangeUserApkInstallStatusRequest() {  super(GenericResponseV2.class, ChangeUserApkInstallStatus.class);  }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("id", String.valueOf(postID));
        parameters.put("status", status);

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);

        try{
            return getService().run(parameters);
        }catch (RetrofitError e){
            OauthErrorHandler.handle(e);
        }

        return null;    }
}

