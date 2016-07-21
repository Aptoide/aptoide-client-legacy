/*package fortumo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import mp.MpUtils;

/**
 * Created by asantos on 04-02-2015.
 */
/*

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import mp.MpUtils;

public abstract class PaymentStatusReceiver extends BroadcastReceiver {
    private static String TAG = "pois";
    //<action android:name="mp.info.PAYMENT_STATUS_CHANGED" />

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        final String product_name =  extras.getString("product_name");
        final boolean isconsumable = product_name.endsWith(FortumoPaymentActivity.ISCONSUMABLECHAR);
        Log.d(TAG, "- billing_status:  " + extras.getInt("billing_status"));
        Log.d(TAG, "- credit_amount:   " + extras.getString("credit_amount"));
        Log.d(TAG, "- credit_name:     " + extras.getString("credit_name"));
        Log.d(TAG, "- message_id:      " + extras.getString("message_id") );
        Log.d(TAG, "- payment_code:    " + extras.getString("payment_code"));
        Log.d(TAG, "- price_amount:    " + extras.getString("price_amount"));
        Log.d(TAG, "- price_currency:  " + extras.getString("price_currency"));
        Log.d(TAG, "- service_id:      " + extras.getString("service_id"));
        Log.d(TAG, "- user_id:         " + extras.getString("user_id"));
        Log.d(TAG, "- product_name:    " + product_name);

        Log.d(TAG, "- isconsumable:         " + isconsumable);

        int billingStatus = extras.getInt("billing_status");
        String paycode = extras.getString("payment_code");
        int prodID = Integer.valueOf(product_name.split("_")[0]);
        onReceiverDone(billingStatus == MpUtils.MESSAGE_STATUS_BILLED,isconsumable,paycode,prodID);
    }

    protected abstract void onReceiverDone(boolean wasPaid,boolean isconsumable,String payCode,int prodID);



}*/