package com.aptoide.amethyst;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AptoideThemePicker {

    public void setAptoideTheme(Context activity) {


        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);


        if(sPref.getString("theme", "light").equals("dark")){
            sPref.edit().putString("theme", "default_dark").commit();
            activity.setTheme(R.style.AptoideThemeDefaultDark);
        }else{
            sPref.edit().putString("theme", "default").commit();
            activity.setTheme(R.style.AptoideThemeDefault);
        }




    }

    public int getAptoideTheme(Context activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        if(sPref.getString("theme", "light").equals("dark")){
            return R.style.AptoideThemeDefaultDark;
        }else{
            return R.style.AptoideThemeDefault;
        }
    }
}
