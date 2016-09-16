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
        //Persian (fa)
        string.add("آب کیر");
        string.add("ارگاسم");
        string.add("برهنه");
        string.add("پورن");
        string.add("پورنو");
        string.add("تجاوز");
        string.add("تخمی");
        string.add("جق");
        string.add("جقی");
        string.add("جلق");
        string.add("جنده");
        string.add("چوچول");
        string.add("حشر");
        string.add("حشری");
        string.add("داف");
        string.add("دودول");
        string.add("ساک زدن");
        string.add("سکس");
        string.add("سکس کردن");
        string.add("سکسی");
        string.add("سوپر");
        string.add("شق کردن");
        string.add("شهوت");
        string.add("شهوتی");
        string.add("شونبول");
        string.add("فیلم سوپر");
        string.add("کس");
        string.add("کس دادن");
        string.add("کس کردن");
        string.add("کسکش");
        string.add("کوس");
        string.add("کون");
        string.add("کون دادن");
        string.add("کون کردن");
        string.add("کونکش");
        string.add("کونی");
        string.add("کیر");
        string.add("کیری");
        string.add("لاپا");
        string.add("لاپایی");
        string.add("لاشی");
        string.add("لخت");
        string.add("لش");
        string.add("منی");
        string.add("هرزه");
        //Arabic (ar)
        string.add("سكس");
        string.add("طيز");
        string.add("شرج");
        string.add("لعق");
        string.add("لحس");
        string.add("مص");
        string.add("تمص");
        string.add("بيضان");
        string.add("ثدي");
        string.add("بز");
        string.add("بزاز");
        string.add("حلمة");
        string.add("مفلقسة");
        string.add("بظر");
        string.add("كس");
        string.add("فرج");
        string.add("شهوة");
        string.add("شاذ");
        string.add("مبادل");
        string.add("عاهرة");
        string.add("جماع");
        string.add("قضيب");
        string.add("زب");
        string.add("لوطي");
        string.add("لواط");
        string.add("سحاق");
        string.add("سحاقية");
        string.add("اغتصاب");
        string.add("خنثي");
        string.add("احتلام");
        string.add("نيك");
        string.add("متناك");
        string.add("متناكة");
        string.add("شرموطة");
        string.add("عرص");
        string.add("خول");
        string.add("قحبة");
        string.add("لبوة");
        return string;
    }

    public static boolean contains(String s) {
        for (String str : setString()) {
            if (s.equals(str)) {
                Log.d(TAG,"Error");
                return true;
            }
        }
        Log.d(TAG,"Error");
        return false;
    }
}