package com.aptoide.amethyst.preferences;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.aptoide.amethyst.Aptoide;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by hsousa on 29-06-2015.
 */
public class ManagerPreferences {

    private static volatile ManagerPreferences instance;

    public final SharedPreferences preferences;

    private ManagerPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (getAptoideClientUUID() == null) {
            setAptoideClientUUID(UUID.randomUUID().toString());
        }

        if (getAptoideId() == null) {
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    setAptoideId(createAptoideId());
                }
            });
        }
    }

    /**
     * Double-Locking singleton. It also inits it when first launched.
     */
    public static ManagerPreferences getInstance(Context context) {
        if (instance == null) {
            synchronized (ManagerPreferences.class) {
                if (instance == null) {
                    instance = new ManagerPreferences(context);
                }
            }
        }
        return instance;
    }


    private String createAptoideId() {

        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(Aptoide.getContext()).getId();
        } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        String aptoideId = Settings.Secure.getString(Aptoide.getContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID);

        if (aptoideId == null || TextUtils.isEmpty(aptoideId)) {
            aptoideId = UUID.randomUUID().toString();
        }
        return aptoideId;
    }

    public void setAptoideClientUUID(String uuid) {
        preferences.edit()
                .putString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), uuid)
                .apply();
    }

    public String getAptoideClientUUID() {
        return preferences.getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), null);
    }

    public void setAptoideId(String uuid) {
        preferences.edit()
                .putString(EnumPreferences.APTOIDE_ID.name(), uuid)
                .apply();
    }

    public String getAptoideId() {
        return preferences.getString(EnumPreferences.APTOIDE_ID.name(), null);
    }

}