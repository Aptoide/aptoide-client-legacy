package com.aptoide.amethyst.utils;

import android.app.Activity;

import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.events.OttoEvents.ActivityLifeCycleEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by trinkes on 3/18/16.
 */

public class LifeCycleMonitor {
    @Subscribe
    public void onActivityLifeCycleEvent(ActivityLifeCycleEvent event) {
        switch (event.getState()) {
            case CREATE:
                AptoideUtils.CrashlyticsUtils.updateNumberOfScreens(true);
                break;
            case DESTROY:
                AptoideUtils.CrashlyticsUtils.updateNumberOfScreens(false);
                break;
        }
    }

    public static void sendLiveCycleEvent(Activity activity, OttoEvents.ActivityLifeCycleEvent.LifeCycle state) {
        final OttoEvents.ActivityLifeCycleEvent event = new OttoEvents
                .ActivityLifeCycleEvent(activity, state);
        BusProvider.getInstance().post(event);
    }
}
