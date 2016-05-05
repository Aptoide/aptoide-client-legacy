package com.aptoide.amethyst.analytics;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.aptoide.amethyst.BuildConfig;
import com.aptoide.amethyst.utils.Logger;
import com.leanplum.Leanplum;
import com.leanplum.LeanplumActivityHelper;
import com.leanplum.LeanplumResources;
import com.leanplum.Var;
import com.leanplum.annotations.Parser;
import com.optimizely.Optimizely;
import com.optimizely.Variable.LiveVariable;

/**
 * Manages AB Testing. Class abstracts which tools are used in order to perform AB tests.
 */
public class ABTestingManager {

	public static final String APP_VIEW_BUTTON_TAP_EVENT = "AppViewButtonTapEvent";
	public static final String APP_VIEW_BUTTON_BACKGROUND_COLOR_VARIABLE = "AppViewButtonBackgroundColor";
	public static final String APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE =
            "AppViewShowSecurityOverlay";

	private static LiveVariable<Boolean> appViewShowSecurityOverlay = Optimizely.booleanForKey
			(APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE, false);
	private static Var<String> appViewButtonBackgroundColor = Var.define
            (APP_VIEW_BUTTON_BACKGROUND_COLOR_VARIABLE, "#E17117");

	/**
	 * Initialize AB testing with application. Usually called from {@link Application#onCreate()}.
	 *
	 * @param application which is going to initialize AB testing.
	 */
	public static void initialize(Application application) {
		if (BuildConfig.LEANPLUM_CONFIGURED) {
			Leanplum.setApplicationContext(application);
			Parser.parseVariables(application);
			LeanplumActivityHelper.enableLifecycleCallbacks(application);
		}
	}

	/**
	 * Starts a new session for AB testing. When this is called is important it will help to define
	 * what is the session length important for analytics metrics. Should be called from launcher
	 * {@link android.app.Activity}.
	 *
	 * @param context of the launcher {@link android.app.Activity}
	 */
	public static void startSession(Context context) {
		if (BuildConfig.LEANPLUM_CONFIGURED) {
			if (BuildConfig.DEBUG) {
				Leanplum.enableVerboseLoggingInDevelopmentMode();
			}
			Leanplum.setAppIdForDevelopmentMode(BuildConfig.LEANPLUM_PROJECT_ID, BuildConfig
                    .LEANPLUM_KEY);
			Leanplum.start(context);
		}

		if (BuildConfig.OPTIMIZELY_CONFIGURED) {
			Optimizely.setVerboseLogging(BuildConfig.DEBUG);
			Optimizely.setEditGestureEnabled(BuildConfig.DEBUG);
			Optimizely.startOptimizelyWithAPIToken(BuildConfig.OPTIMIZELY_KEY, (Application)
                    context
					.getApplicationContext());
		}
	}

	/**
	 * Adds AB testing resources to current application {@link Resources}. The result of this
     * method
	 * should be returned on {@link Application#getResources()}.
	 *
	 * @param resources from application.
	 * @return application resources plus AB testing resources.
	 */
	public static Resources getResources(Resources resources) {
		if (BuildConfig.LEANPLUM_CONFIGURED) {
			return new LeanplumResources(resources);
		} else {
			return resources;
		}
	}

	/**
	 * Returns a AB Testing variable related to a {@link Color}.
	 *
	 * @param key to identify the variable.
	 * @return result of parsing the variable using {@link Color#parseColor(String)} or black if
	 * string could not be parsed.
	 */
	public static int getColorVariable(String key) {
		switch (key) {
			case APP_VIEW_BUTTON_BACKGROUND_COLOR_VARIABLE:
				return parseColorOrDefault(appViewButtonBackgroundColor.stringValue(), Color
                        .BLACK);
			default:
				throw new IllegalArgumentException("Color variable not defined for AB Testing: " +
						key);
		}
	}

	/**
	 * Returns a AB Testing variable related to a {@link Boolean}.
	 *
	 * @param key to identify the variable.
	 * @return true or false
	 */
	public static boolean getBooleanVariable(String key) {
		switch (key) {
			case APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE:
				return appViewShowSecurityOverlay.get();
			default:
				throw new IllegalArgumentException("Boolean variable not defined for AB Testing: " +
						key);
		}
	}

	/**
	 * Track event associated with an AB Test.
	 *
	 * @param eventKey event identifier.
	 */
	public static void trackEvent(String eventKey) {
		Optimizely.trackEvent(eventKey);
		Leanplum.track(eventKey);
	}

	private static int parseColorOrDefault(String colorString, int defaultColorInt) {
		try {
			return Color.parseColor(colorString);
		} catch (IllegalArgumentException exception) {
			Logger.e("AB-TESTING", "Could not parse AB Testing color: " + colorString + "return " +
                    "default color " + defaultColorInt);
			return defaultColorInt;
		}
	}
}