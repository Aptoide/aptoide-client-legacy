package com.aptoide.amethyst.webservices;

import android.preference.PreferenceManager;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.json.TimelineListAPKsJson;
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
public class ListApksInstallsRequest extends RetrofitSpiceRequest<TimelineListAPKsJson, ListApksInstallsRequest.ListApksInstalls> {
    private String limit;
    private String offset_id;
    private boolean upwards = false;
    private long postId;


    public interface ListApksInstalls {
        @POST(WebserviceOptions.WebServicesLink + "3/listUserApkInstalls")
        @FormUrlEncoded
        public TimelineListAPKsJson run(@FieldMap HashMap<String, String> args);
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setOffset_id(String offset_id) {
        this.offset_id = offset_id;
    }

    public ListApksInstallsRequest() {
        super(TimelineListAPKsJson.class, ListApksInstalls.class);
    }

    public void setUpwardsDirection() {
        upwards = true;
    }

    @Override
    public TimelineListAPKsJson loadDataFromNetwork() throws Exception {
//        GenericUrl url= new GenericUrl(getUrl());

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("mode", "json");
        parameters.put("limit", limit);
        parameters.put("offset_id", offset_id);
        //parameters.put("offset_dir", upwards ? "up":"");
        if (postId > 0) {
            parameters.put("post_id", String.valueOf(postId));
        }

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);


        TimelineListAPKsJson response = null;

        try {
            response = getService().run(parameters);
            // we replaced jodatime from aptoide v6 since System.currentTimeMillis always returns UTC times
//            PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putLong("timelineTimestamp", DateTime.now(DateTimeZone.forID("UTC")).toDate().getTime() / 1000).commit();
            PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putLong("timelineTimestamp", System.currentTimeMillis() / 1000).commit();
        } catch (RetrofitError e) {
            OauthErrorHandler.handle(e);
        }


        return response;

    }

    public void setDownwardsDirection() {
        upwards = false;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}
