package com.aptoide.amethyst.webservices;

import android.text.TextUtils;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by asantos on 24-09-2014.
 */
public class ChangeUserSettingsRequest extends RetrofitSpiceRequest<GenericResponseV2, ChangeUserSettingsRequest.ChangeUserSettings> {
    public static final String TIMELINEACTIVE = "active";
    public static final String TIMELINEINACTIVE = "inactive ";
    private ArrayList<String> list;

    public ChangeUserSettingsRequest() {
        super(GenericResponseV2.class, ChangeUserSettings.class);
        list = new ArrayList<>();
    }

    public void addTimeLineSetting(String value) {
        list.add("timeline=" + value);
    }

    public void changeMatureSwitchSetting(boolean active) {
        list.add("matureswitch=" + (active ? TIMELINEACTIVE : TIMELINEINACTIVE));
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("mode", "json");
        parameters.put("settings", TextUtils.join(",", list));

        String token = SecurePreferences.getInstance().getString("access_token", "empty");
        parameters.put("access_token", token);

        try {
            return getService().run(parameters);
        } catch (RetrofitError e) {
            OauthErrorHandler.handle(e);
        }

        return null;
    }


    public interface ChangeUserSettings {
        @POST(WebserviceOptions.WebServicesLink + "3/changeUserSettings")
        @FormUrlEncoded
        GenericResponseV2 run(@FieldMap HashMap<String, String> args);
    }

}
