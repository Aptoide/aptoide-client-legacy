package com.aptoide.amethyst.webservices;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.SharedPreferences;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.model.json.OAuth;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 24-11-2014.
 */
public class OauthErrorHandler {

    public interface OauthService {
        @POST("/3/oauth2Authentication")
        @FormUrlEncoded
        OAuth authenticate(@FieldMap HashMap<String, String> args);
    }

    public static void handle(RetrofitError error) {

        switch (error.getKind()) {

            case NETWORK:
            case CONVERSION:
            case UNEXPECTED:
                throw error;
            case HTTP:
                try {
                    if (error.getResponse().getStatus() == 401) {
                        AccountManager accountManager = AccountManager.get(Aptoide.getContext());
                        SharedPreferences preferences = SecurePreferences.getInstance();

                        if (accountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {


                            Account account = accountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType())[0];
                            String refreshToken = "";
                            try {
                                refreshToken = accountManager.blockingGetAuthToken(account, AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false);
                            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                Logger.printException(e);
                            }

                            HashMap<String, String> parameters = new HashMap<String, String>();
                            parameters.put("grant_type", "refresh_token");
                            parameters.put("client_id", "Aptoide");
                            parameters.put("refresh_token", refreshToken);

                            OAuth oAuth = new RestAdapter.Builder().setConverter(createConverter()).setEndpoint("http://webservices.aptoide.com/webservices").build().create(OauthService.class).authenticate(parameters);

                            preferences.edit().putString("access_token", oAuth.getAccess_token()).apply();

                        } else {

//                            Crashlytics.logException(new Throwable("No account to authenticate, resolving", new Exception(error.getUrl())));

                            preferences.edit().remove(Constants.ACCESS_TOKEN).apply();
                        }
                    } else {
//                        Crashlytics.logException(new Throwable("Non 401 error", error));
                    }

                } catch (Exception e) {
//                    Crashlytics.logException(new Throwable("Exception on authentication", e));
                    throw e;
                }

                break;
        }

        throw error;
    }


    public static Converter createConverter() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return new JacksonConverter(objectMapper);
    }
}
