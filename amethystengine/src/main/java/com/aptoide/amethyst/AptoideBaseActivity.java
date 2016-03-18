package com.aptoide.amethyst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;

import lombok.Getter;

/**
 * Created by rmateus on 01/06/15.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity {

    @Getter private boolean _resumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle.CREATE);
        Analytics.Lifecycle.Activity.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        Analytics.Lifecycle.Activity.onDestroy(this);
        super.onDestroy();
        sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle.DESTROY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle.START);
        Analytics.Lifecycle.Activity.onStart(this);
    }

    @Override
    protected void onStop() {
        Analytics.Lifecycle.Activity.onStop(this);
        super.onStop();
        sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle.STOP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle.RESUME);
        _resumed = true;
        Analytics.Lifecycle.Activity.onResume(this, getScreenName());
        AptoideUtils.CrashlyticsUtils.addScreenToHistory(getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        Analytics.Lifecycle.Activity.onPause(this);
        super.onPause();
        sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle.PAUSE);
        _resumed = false;
    }

    private static void sendLiveCycleEvent(OttoEvents.ActivityLifeCycleEvent.LifeCycle state) {
        final OttoEvents.ActivityLifeCycleEvent event = new OttoEvents
                .ActivityLifeCycleEvent(state);
        BusProvider.getInstance().post(event);
    }

    /*
     * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
     */
    protected abstract String getScreenName();
}
