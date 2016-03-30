package com.aptoide.amethyst.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.aptoide.amethyst.Aptoide;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 03-12-2013
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class IconSizeUtils {

    static final private int baseLine = 96;
    static final private int baseLineAvatar = 150;
    static final private int baseLineXNotification = 320;
    static final private int baseLineYNotification = 180;
    public static final String XXXHDPI = "xxxhdpi";
    public static final String XXHDPI = "xxhdpi";
    public static final String XHDPI = "xhdpi";
    public static final String HDPI = "hdpi";
    public static final String MDPI = "mdpi";
    public static final String LDPI = "ldpi";
    public static final String DEFAULT_SCREEN_DENSITY = "default";
    private static int baseLineScreenshotLand = 256;
    private static int baseLineScreenshotPort = 96;
    public static final HashMap<String,String> mIconSizes;
    static {
        mIconSizes= new HashMap<>();
        mIconSizes.put(XXXHDPI, "384x384");
        mIconSizes.put(XXHDPI, "288x288");
        mIconSizes.put(XHDPI, "192x192");
        mIconSizes.put(HDPI , "144x144");
        mIconSizes.put(MDPI , "127x127");
        mIconSizes.put(LDPI , "96x96");
        mIconSizes.put(DEFAULT_SCREEN_DENSITY, "72x72");
    }


    public static String generateSizeStringNotification(){
        Context context = Aptoide.getContext();

        if(context == null){
            return "";
        }
        float densityMultiplier = densityMultiplier();

        int sizeX = (int) (baseLineXNotification * densityMultiplier);
        int sizeY = (int) (baseLineYNotification * densityMultiplier);

        //Log.d("Aptoide-IconSize", "Size is " + size);

        return sizeX+"x"+sizeY;
    }

    public static String generateSizeString() {
//        Context context = Aptoide.getContext();
//        if (context == null) {
//            return "";
//        }
//        float densityMultiplier = densityMultiplier();
//
//        int size = (int) (baseLine * densityMultiplier);
//
//        //Log.d("Aptoide-IconSize", "Size is " + size);
//
//        return size + "x" + size;
        return mIconSizes.get(getScreenDensity());
    }


    public static String generateSizeStringAvatar() {
        Context context = Aptoide.getContext();
        if (context == null) {
            return "";
        }
        float densityMultiplier = densityMultiplier();

        int size = Math.round(baseLineAvatar * densityMultiplier);

        //Log.d("Aptoide-IconSize", "Size is " + size);

        return size + "x" + size;
    }

    public static String generateSizeStringScreenshots(String orient) {
        Context context = Aptoide.getContext();
        if (context == null) {
            return "";
        }
        boolean isPortrait = orient != null && orient.equals("portrait");
        int dpi = AptoideUtils.HWSpecifications.getDensityDpi();
        return getThumbnailSize(dpi, isPortrait);
    }

    private static String getThumbnailSize(int density, boolean isPortrait){
        if(!isPortrait){
            if(density >= 640){
                return "1024x640";
            }else if(density >= 480){
                return "768x480";
            }else if(density >= 320){
                return "512x320";
            }else if(density >= 240){
                return "384x240";
            }else if(density >= 213){
                return "340x213";
            }else if(density >= 160){
                return "256x160";
            }else{
                return "192x120";
            }
        }else{
            if(density >= 640){
                return "384x640";
            }else if(density >= 480){
                return "288x480";
            }else if(density >= 320){
                return "192x320";
            }else if(density >= 240){
                return "144x240";
            }else if(density >= 213){
                return "127x213";
            }else if(density >= 160){
                return "96x160";
            }else{
                return "72x120";
            }
        }
    }

    private static Float densityMultiplier() {
        Context context = Aptoide.getContext();
        if (context == null) {
            return 0f;
        }

        float densityMultiplier = context.getResources().getDisplayMetrics().density;

        if (densityMultiplier <= 0.75f) {
            densityMultiplier = 0.75f;
        } else if (densityMultiplier <= 1) {
            densityMultiplier = 1f;
        } else if (densityMultiplier <= 1.333f) {
            densityMultiplier = 1.3312500f;
        } else if (densityMultiplier <= 1.5f) {
            densityMultiplier = 1.5f;
        } else if (densityMultiplier <= 2f) {
            densityMultiplier = 2f;
        } else if (densityMultiplier <= 3f) {
            densityMultiplier = 3f;
        } else {
            densityMultiplier = 4f;
        }
        return densityMultiplier;
    }

    public static String getScreenDensity() {

        Context context = Aptoide.getContext();
        int density = context.getResources().getDisplayMetrics().densityDpi;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                return LDPI;
            case DisplayMetrics.DENSITY_MEDIUM:
                return MDPI;
            case DisplayMetrics.DENSITY_HIGH:
                return HDPI;
            case DisplayMetrics.DENSITY_XHIGH:
                return XHDPI;
            case DisplayMetrics.DENSITY_XXHIGH:
                return XXHDPI;
            case DisplayMetrics.DENSITY_XXXHIGH:
                return XXXHDPI;

            default:
                return "default";
        }

    }
}
