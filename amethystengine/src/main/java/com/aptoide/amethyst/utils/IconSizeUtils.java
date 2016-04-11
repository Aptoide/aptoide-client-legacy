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
    public static final int DEFAULT_SCREEN_DENSITY = -1;
    private static int baseLineScreenshotLand = 256;
    private static int baseLineScreenshotPort = 96;
    public static final HashMap<Integer,String> mStoreIconSizes;

    public static final int ICONS_SIZE_TYPE = 0;

    static {
        mStoreIconSizes = new HashMap<>();
        mStoreIconSizes.put(DisplayMetrics.DENSITY_XXXHIGH, "");
        mStoreIconSizes.put(DisplayMetrics.DENSITY_XXHIGH, "450x450");
        mStoreIconSizes.put(DisplayMetrics.DENSITY_XHIGH, "300x300");
        mStoreIconSizes.put(DisplayMetrics.DENSITY_HIGH , "225x225");
        mStoreIconSizes.put(DisplayMetrics.DENSITY_MEDIUM , "150x150");
        mStoreIconSizes.put(DisplayMetrics.DENSITY_LOW , "113x113");
    }
    public static final HashMap<Integer,String> mIconSizes;
    public static final int STORE_ICONS_SIZE_TYPE = 1;

    static {
        mIconSizes= new HashMap<>();
        mIconSizes.put(DisplayMetrics.DENSITY_XXXHIGH, "");
        mIconSizes.put(DisplayMetrics.DENSITY_XXHIGH, "288x288");
        mIconSizes.put(DisplayMetrics.DENSITY_XHIGH, "192x192");
        mIconSizes.put(DisplayMetrics.DENSITY_HIGH , "144x144");
        mIconSizes.put(DisplayMetrics.DENSITY_MEDIUM , "127x127");
        mIconSizes.put(DisplayMetrics.DENSITY_LOW , "96x96");
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

    public static String generateSizeStoreString() {
        String iconRes = mStoreIconSizes.get(Aptoide.getContext().getResources().getDisplayMetrics().densityDpi);
        return iconRes != null ? iconRes : getDefaultSize(STORE_ICONS_SIZE_TYPE);
    }

    public static String generateSizeString() {
        String iconRes = mIconSizes.get(Aptoide.getContext().getResources().getDisplayMetrics().densityDpi);
        return iconRes != null ? iconRes : getDefaultSize(ICONS_SIZE_TYPE);
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

    public static String getDefaultSize(int varType) {

            switch (varType) {
                case STORE_ICONS_SIZE_TYPE:
                    if (AptoideUtils.HWSpecifications.getDensityDpi() < DisplayMetrics.DENSITY_HIGH) {
                        return mStoreIconSizes.get(DisplayMetrics.DENSITY_LOW);
                    } else {
                        return mStoreIconSizes.get(DisplayMetrics.DENSITY_XXXHIGH);
                    }
                case ICONS_SIZE_TYPE:
                    if (AptoideUtils.HWSpecifications.getDensityDpi() < DisplayMetrics.DENSITY_HIGH) {
                        return mIconSizes.get(DisplayMetrics.DENSITY_LOW);
                    } else {
                        return mIconSizes.get(DisplayMetrics.DENSITY_XXXHIGH);
                    }
            }
        return null;
    }
}
