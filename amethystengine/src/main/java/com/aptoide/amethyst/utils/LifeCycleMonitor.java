package com.aptoide.amethyst.utils;

import com.aptoide.amethyst.events.OttoEvents.ActivityLifeCycleEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by trinkes on 3/18/16.
 */
class LifeCycleMonitor {
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
}
