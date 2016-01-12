package com.aptoide.amethyst.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

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
    }

    /**
     * Double-Locking singleton. It also inits it when first launched.
     * @param context
     * @return
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

    public void setAptoideClientUUID(String uuid) {
        preferences.edit()
                .putString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), uuid)
                .apply();
    }

    public String getAptoideClientUUID() {
        return preferences.getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), null);
    }
}