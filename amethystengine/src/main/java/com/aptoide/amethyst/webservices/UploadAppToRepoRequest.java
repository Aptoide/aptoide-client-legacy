package com.aptoide.amethyst.webservices;

import android.content.SharedPreferences;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.json.UploadAppToRepoJson;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PartMap;

/**
 * Created by fabio on 22-10-2015.
 */
public class UploadAppToRepoRequest extends RetrofitSpiceRequest<UploadAppToRepoJson, UploadAppToRepoRequest.Webservice > {
    public UploadAppToRepoRequest() {
        super(UploadAppToRepoJson.class, Webservice.class);
    }

//    @Getter
//    @Setter
    public String md5Sum;
//    @Getter @Setter
    public String repo;


    @Override
    public UploadAppToRepoJson loadDataFromNetwork() throws Exception {

        HashMap<String, Object> arguments = new HashMap<>();


        SharedPreferences preferences = SecurePreferences.getInstance();

        String token = preferences.getString("devtoken", "empty");
        arguments.put("mode", "json");
        arguments.put("token", token);
        arguments.put("apk_md5sum", md5Sum);
        arguments.put("repo", repo);

        return getService().postApk(arguments);
    }

    public interface Webservice{
        @POST("/upload.webservices.aptoide.com/webservices/2/uploadAppToRepo")
        @Multipart
        UploadAppToRepoJson postApk(@PartMap HashMap<String, Object> args);
    }

}
