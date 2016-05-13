package com.aptoide.amethyst.webservices.v3;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.v3.RateApp;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 16-11-2015.
 */
public class RateAppRequest extends RetrofitSpiceRequest<RateApp, RateAppRequest.IPostRateWebService> {
    String STAR_CONSTANT = "star";
    String APP_ID = "appid";
    long apkId;
    private static final String MODE = "mode";
    int star;

    public RateAppRequest(long apkId, float star) {
        super(RateApp.class, RateAppRequest.IPostRateWebService.class);
        this.apkId = apkId;
        this.star = (int) star;
    }


    @Override
    public RateApp loadDataFromNetwork() throws Exception {
        HashMap<String, String> args = new HashMap<>();
        args.put(APP_ID, String.valueOf(apkId));
        args.put(MODE, "json");
        args.put(STAR_CONSTANT, String.valueOf(star));
        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        args.put(Constants.ACCESS_TOKEN, token);
        try {
            return getService().rateApp(args);
        } catch (RetrofitError e) {
            OauthErrorHandler.handle(e);
        }
        return null;
    }

    interface IPostRateWebService {
        @POST("/webservices.aptoide.com/webservices/3/addApkStar")
        @FormUrlEncoded
        RateApp rateApp(@FieldMap HashMap<String, String> args);
    }
}
