package com.aptoide.amethyst.subscription;

import android.util.Log;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ruicardoso on 09-03-2016.
 */
public class SubscriptionStatusRequest extends RetrofitSpiceRequest<SubscriptionStatus,SubscriptionStatusRequest.IGetSubscriptionStatus> {
    private final static String TAG = SubscriptionStatusRequest.class.getSimpleName();

    /**
     * URL for partner token validation webservice
     */
    public final static String tokenValidationURL = "<INSERT VALIDATION WEBSERVICE HERE>";


    private String accessToken;

    public SubscriptionStatusRequest(String accessToken) {
        super(SubscriptionStatus.class, IGetSubscriptionStatus.class);
        this.accessToken = accessToken;
    }

    @Override
    public SubscriptionStatus loadDataFromNetwork() throws Exception {
        if(accessToken == null)
            return null;

        HashMap<String, String> args = new HashMap<>();
        args.put("accessToken",accessToken);
        try {
            return getService().subscriptionStatus(args);
        } catch (RetrofitError e) {
            Log.i(TAG, "Retrofit error: " + e.getBody() );
        }
        return null;
    }

    interface IGetSubscriptionStatus {
        @POST(tokenValidationURL)
        @FormUrlEncoded
        SubscriptionStatus subscriptionStatus(@FieldMap HashMap<String, String> args);
    }
}
