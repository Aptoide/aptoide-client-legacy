package com.aptoide.amethyst.analytics;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.aptoide.amethyst.utils.Logger;

/**
 * Manages AB Testing. Class abstracts which tools are used in order to perform AB tests.
 */
public class ABTestingManager {

	public static final String APP_VIEW_BUTTON_TAP_EVENT = "AppViewButtonTapEvent";
	public static final String APP_VIEW_BUTTON_BACKGROUND_COLOR_VARIABLE = "AppViewButtonBackgroundColor";
	public static final String APP_VIEW_SHOW_SECURITY_OVERLAY_BOOLEAN_VARIABLE =
            "AppViewShowSecurityOverlay";

	/**
	 * Initialize AB testing with application. Usually called from {@link Application#onCreate()}.
	 *
	 * @param application which is going to initialize AB testing.
	 */
	public static void initialize(Application application) {

	}

	/**
	 * Starts a new session for AB testing. Session length is important for analytics metrics.
	 * Should be called from launcher {@link android.app.Activity}.
	 * @param context of the launcher {@link android.app.Activity}
	 */
	public static void startSession(Context context) {

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
		return resources;
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
				return Color.WHITE;
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
				return true;
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