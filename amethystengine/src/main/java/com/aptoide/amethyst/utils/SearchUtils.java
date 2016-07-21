package com.aptoide.amethyst.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.MainActivity;
import com.aptoide.amethyst.analytics.Analytics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xuying on 19-07-2016.
 */
public class SearchUtils {
    public static final String TAG = SearchUtils.class.getSimpleName();
    public static Set<String> setString() {
        Set<String> string = new HashSet<String>();
        string.add("hookup");
        string.add("girls");
        string.add("ass");
        string.add("sex");
        string.add("sexo");
        string.add("sexy");
        string.add("boob");
        string.add("booty");
        string.add("penis");
        string.add("anal");
        string.add("vaginal");
        string.add("erotic");
        string.add("deep throat");
        string.add("cunt");
        string.add("cock");
        string.add("xx");
        string.add("xxx");
        string.add("asian");
        string.add("japanese");
        string.add("breast");
        string.add("stripper");
        string.add("blowjob");
        string.add("fuck");
        string.add("nude");
        string.add("curvy");
        string.add("strip poker");
        string.add("kamasutra");
        string.add("posições sexuais");
        string.add("sexuais");
        string.add("porn");
        string.add("tits");
        string.add("lesbian");
        string.add("bondage");
        string.add("boner");
        string.add("masturbation");
        string.add("masturbating");
        string.add("suck");
        string.add("cum");
        string.add("banging");
        string.add("pussy");
        string.add("screw");
        string.add("hentai");
        string.add("squirt");
        string.add("shemale");
        string.add("orgasm");
        return string;
    }

    public static boolean contains(String storeName) {
        for (String str : setString()) {
            if (storeName.equals(str)) {
                Log.d(TAG,"Error");
                return true;
            }
        }
        Log.d(TAG,"Error");
        return false;
    }
}