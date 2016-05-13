package com.aptoide.amethyst;

import android.os.Environment;

import java.io.File;

/**
 * Created by rmateus on 08/06/15.
 */
public class Configuration {


    public final static String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String PATH_CACHE = SDCARD + File.separator + ".aptoide";
    public final static String PATH_CACHE_ICONS = PATH_CACHE + File.separator + "icons";

}
