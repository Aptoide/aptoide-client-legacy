package com.aptoide.amethyst.preferences;

import android.preference.PreferenceManager;

import com.aptoide.amethyst.Aptoide;

/**
 * Created by hsousa on 08-07-2015.
 */
public class AptoidePreferences {

    public static final String SHARE_TIMELINE_DOWNLOAD_BOOL = "STLD";
    public static final String TIMELINE_ACEPTED_BOOL = "TLA";
    public static final String REPOS_SYNCED = "REPOS_SYNCED";

    public static final boolean getBoolean(String key, boolean defValue){
        return PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(key,defValue);
    }
    public static final void putBooleanAndCommit(String key, boolean Value){
        PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putBoolean(key, Value).commit();
    }
}
