package com.aptoide.amethyst;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.LifeCycleMonitor;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

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
        if(Aptoide.getConfiguration().getDefaultStore().equals("zain-market")){
            TelephonyManager manager = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
            String carrierID = manager.getNetworkOperator();
            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    //TODO SubscriptionManager
            }
            else {*/
                if (carrierID.equals("41820") || carrierID.equals("41830") || carrierID.equalsIgnoreCase("ZAIN IQ")) {
                    Log.d("zain", "zain: you can enter the store");
                }
                else {
                    //TODO ir para a mensagem deles/activity
                }
            //}
        }
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
}
