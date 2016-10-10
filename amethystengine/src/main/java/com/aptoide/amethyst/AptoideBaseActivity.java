package com.aptoide.amethyst;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.LifeCycleMonitor;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.*;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

/**
 * Created by rmateus on 01/06/15.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity {

    @Getter private boolean _resumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Aptoide.getConfiguration().getDefaultStore().equals("aban-app-store")){setLocale("fa");}
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.CREATE);
        Analytics.Lifecycle.Activity.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        Analytics.Lifecycle.Activity.onDestroy(this);
        super.onDestroy();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.DESTROY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.START);
        Analytics.Lifecycle.Activity.onStart(this);
    }

    @Override
    protected void onStop() {
        Analytics.Lifecycle.Activity.onStop(this);
        super.onStop();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.STOP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.RESUME);
        _resumed = true;
        Analytics.Lifecycle.Activity.onResume(this, getScreenName());
        AptoideUtils.CrashlyticsUtils.addScreenToHistory(getClass().getSimpleName());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Analytics.Lifecycle.Activity.onNewIntent(this, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Analytics.Lifecycle.Activity.onPause(this);
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.PAUSE);
        _resumed = false;
    }


    /*
     * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
     */
    protected abstract String getScreenName();

    /**
     * Force language on app
     * @param lang language the app should embebed
     */
    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
