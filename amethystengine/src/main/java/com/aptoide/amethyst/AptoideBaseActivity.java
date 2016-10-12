package com.aptoide.amethyst;

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
        zainSimCardRuleApplies();
    }

    /**
     * ZAIN SIM card access Rules:
     *  has no SIM card slot:               can enter store
     *  has 1 SIM card slot:                if carrier Name = ZAIN IQ
     *                                          can enter store
     *                                      else
     *                                          can't enter store
     *  has >=2 SIM card slot(API >= 22):     if one of them is ZAIN IQ
     *                                          can enter store
     *                                      else
     *                                          can't enter store
     *
     *  has >=2 SIM card slot (API < 22):     = has 1 SIM card slot
     * @return true if can't enter the store, false if can enter the store
     */
    public boolean zainSimCardRuleApplies(){
        if(Aptoide.getConfiguration().getDefaultStore().equals("zainsouk")) {
            String zainDialogTag = "ZainAlertDialog";
            String[] zainCarrierName = {"ZAIN IQ"};
            String[] zaincarrierID = {"41820","41830"};
            TelephonyManager manager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
            if (manager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(this).getActiveSubscriptionInfoList();
                    if(activeSubscriptionInfoList!=null && !activeSubscriptionInfoList.isEmpty())
                        for (SubscriptionInfo s : activeSubscriptionInfoList)
                            if (s.getCarrierName().toString().equalsIgnoreCase(zainCarrierName[0])) {
                                DialogFragment pd = (DialogFragment) getSupportFragmentManager().findFragmentByTag(zainDialogTag);
                                if (pd != null) pd.dismiss();
                                return false;
                            }
                    getSupportFragmentManager().beginTransaction().add(new ZainSimDialogFragment(true), zainDialogTag).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    return true;
                }
                else {
                    String carrierID = manager.getNetworkOperator();
                    String carrierName = manager.getNetworkOperatorName();
                    if (!carrierID.equals(zaincarrierID[0]) && !carrierID.equals(zaincarrierID[1]) && !carrierName.equalsIgnoreCase(zainCarrierName[0])) {
                        getSupportFragmentManager().beginTransaction().add(new ZainSimDialogFragment(false), zainDialogTag).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        return true;
                    } else {
                        DialogFragment pd = (DialogFragment) getSupportFragmentManager().findFragmentByTag(zainDialogTag);
                        if (pd != null) pd.dismiss();
                    }
                }
            }
        }
        return false;
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

        //dismiss dialog form Zain
        DialogFragment pd = (DialogFragment) getSupportFragmentManager().findFragmentByTag("ZainAlertDialog");
        if (pd != null) pd.dismiss();
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
        Locale myLocale = new Locale(lang,lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * Zain Dialog
     * @author diogoloureiro
     */
    private class ZainSimDialogFragment extends DialogFragment {

        private boolean isApi22;

        ZainSimDialogFragment(boolean isApi22){
                this.isApi22=isApi22;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(Aptoide.getConfiguration().getMarketName());
            if(!isApi22)
                dialog.setContentView(R.layout.zain_sim_card_dialog);
            else
                dialog.setContentView(R.layout.zain_sim_card_dialog_api22);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                    }
                    return true;
                }
            });
            return dialog;
        }

    }
}
