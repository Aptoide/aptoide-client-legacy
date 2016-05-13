package com.aptoide.amethyst.subscription;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.MainActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Date;

/**
 * Created by ruicardoso on 08-03-2016.
 */
public class Subscription {
    private static final String TAG = Subscription.class.getSimpleName();

    public static final String SHOW_SKIP = "show_skip";

    private static final String STORE_OPEN = "store_open";

    /**
     * Returns if subscription screen should be shown on store open according to configuration and
     * current subscription status.
     *
     * @return true if should show screen, false if it doesn't need
     */
    public static boolean showOnStoreOpen(){
        return storeHasSubscription() && Aptoide.getContext().getResources().getString(R.string.subscription_trigger).equals(STORE_OPEN) && !activeSubscription();
    }

    public static boolean showOnDownload(boolean isPaidApp){
        return storeHasSubscription() && !activeSubscription() && !isPaidApp;
    }

    /**
     * Returns if current store has a subscription model
     * @return true if subscription model is active, false if it doesn't have a subscription model
     */
    public static boolean storeHasSubscription(){
        return Aptoide.getConfiguration().isSubscription();
    }

    /**
     * Checks if user subscription is currently active according to subscription date. Shouldn't be
     * used if a strict subscription check is needed (ex.: when downloading applications) because user
     * could have canceled subscription outside the store and subscription time isn't very accurate (the
     * exact moment of subscription activation isn't known)
     *
     * @return true if user subscription is currently active, false if user isn't subscribed or subscription
     * of user expired
     */
    public static boolean activeSubscription(){
        Log.i(TAG,"Subscription expires on: " + getSubscriptionExpiration());
        return (new Date().getTime()/1000l < getSubscriptionExpiration());
    }

    /**
     * Returns the calculated subscription expiration date for the user.
     * @return unixtime of expiration date.
     */
    private static Long getSubscriptionExpiration(){
        return PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getLong("subscription_expiration", 0);
    }

    /**
     * Returns user access token from secure preferences.
     *
     * @return user access token String. If user never subscribed, returns null
     */
    public static String getAccessToken(){
        return SecurePreferences.getInstance().getString("subscription_access_token",null);
    }

    /**
     * Verifies if given accessToken is valid using partner API
     * @param spiceManager - Spicemanager
     * @param accessToken - string containing user access token
     * @param listener - RequestListener for the server response containing the SubscriptionStatus with
     *                 the results
     */
    public static void subscriptionTokenVerificationRequest(SpiceManager spiceManager, String accessToken, RequestListener<SubscriptionStatus> listener){
        spiceManager.execute(
                new SubscriptionStatusRequest(accessToken),
                "string",
                DurationInMillis.ALWAYS_EXPIRED,
                listener);
    }

    /**
     * Shows subscription page if network is available
     * @param activity
     */
    public static void showSubscriptionPage(Activity activity){
        if(AptoideUtils.NetworkUtils.isNetworkAvailable(activity)){
            Intent intent = new Intent(activity, SubscriptionActivity.class);
            if(activity instanceof MainActivity){
                intent.putExtra(SHOW_SKIP,true);
            }
            activity.startActivityForResult(intent, SubscriptionActivity.SUBSCRIPTION_REQ_CODE);
        }
    }
}
