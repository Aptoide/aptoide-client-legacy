package com.aptoide.amethyst.analytics;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.aptoide.amethyst.BuildConfig;
import com.leanplum.Leanplum;
import com.leanplum.LeanplumActivityHelper;
import com.leanplum.LeanplumResources;
import com.leanplum.annotations.Parser;
import com.optimizely.Optimizely;

public class ABTestingManager {


    public static void initialize(Application application) {
        if (BuildConfig.LEANPLUM_CONFIGURED) {
            Leanplum.setApplicationContext(application);
            Parser.parseVariables(application);
            LeanplumActivityHelper.enableLifecycleCallbacks(application);
        }
    }

    public static void startSession(Context context) {
        if (BuildConfig.LEANPLUM_CONFIGURED) {
            if (BuildConfig.DEBUG) {
                Leanplum.enableVerboseLoggingInDevelopmentMode();
            }
            Leanplum.setAppIdForDevelopmentMode(BuildConfig.LEANPLUM_PROJECT_ID, BuildConfig.LEANPLUM_KEY);
            Leanplum.start(context);
        }

        if (BuildConfig.OPTIMIZELY_CONFIGURED) {
            if (BuildConfig.DEBUG) {
                Optimizely.setVerboseLogging(true);
                Optimizely.setEditGestureEnabled(false);
                Optimizely.enableEditor();
            }
            Optimizely.startOptimizelyWithAPIToken(BuildConfig.OPTIMIZELY_KEY, (Application) context.getApplicationContext());
        }
    }

    public static Resources getResources(Resources resources) {
        if (BuildConfig.LEANPLUM_CONFIGURED) {
            return new LeanplumResources(resources);
        } else {
            return resources;
        }
    }

}
