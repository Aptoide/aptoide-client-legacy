package fortumo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.aptoide.amethyst.BuildConfig;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.models.PaymentServices;

import mp.MpUtils;
import mp.PaymentActivity;
import mp.PaymentRequest;

public class FortumoPaymentActivity extends PaymentActivity {
    public static final String RESULT_EXTRA_ISCONSUMABLE_BOOL = "IEIC";
    public static final String RESULT_EXTRA_PAYCODE_STRING = "IEPC";
    public static final String RESULT_EXTRA_PRODID_INT = "IEPID";

    public static final String ISCONSUMABLECHAR = "C";
    public static final String NONCONSUMABLECHAR = "N";
    //public static final String EXTRA_USER = "EUSER";
    //public static final String EXTRA_PACKAGE = "EPAGE";
    //public static final String EXTRA_REPO = "EREPO";
    public static final String EXTRA_ISCONSUMABLE = "ISCONSUMABLE";
    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_PAYMENTSERVICE = "EPS";

    private MyReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(receiver=new MyReceiver(), new IntentFilter("mp.info.PAYMENT_STATUS_CHANGED"));

        receiver.setCallBack(new MyReceiver.CallBack() {
            @Override
            public void onReceive(boolean wasPaid, boolean isconsumable, String payCode, int prodID) {
                if(wasPaid) {
                    Log.d("pois", "wasPaid");
                    Intent i = new Intent();
                    i.putExtra(RESULT_EXTRA_ISCONSUMABLE_BOOL,isconsumable);
                    i.putExtra(RESULT_EXTRA_PAYCODE_STRING,payCode);
                    i.putExtra(RESULT_EXTRA_PRODID_INT,prodID);
                    finish(RESULT_OK,i);
                }
                else finish(RESULT_CANCELED, null);
            }
        });

        setContentView(R.layout.activity_fortumopayment);
        PaymentServices ps = getIntent().getParcelableExtra(EXTRA_PAYMENTSERVICE);
        MpUtils.enablePaymentBroadcast(this, BuildConfig.APPLICATION_ID + ".cm.aptoide.ptdev.PAYMENT_BROADCAST_PERMISSION");

        PaymentRequest.PaymentRequestBuilder builder = new PaymentRequest.PaymentRequestBuilder();
        final int UserID = SecurePreferences.getInstance().getInt("User_ID",0);
        boolean isconsumable=getIntent().getBooleanExtra(EXTRA_ISCONSUMABLE, true);
        String productName = String.valueOf(getIntent().getIntExtra(EXTRA_ID,0)) +
                '_' +
                String.valueOf(UserID) +
                '_' +
                (isconsumable ? ISCONSUMABLECHAR:NONCONSUMABLECHAR);
        Log.d("pois", "setProductName: "+productName);

        builder.setService(ps.service_id, ps.inapp_secret);
        builder.setDisplayString(ps.getName());      // shown on user receipt
        builder.setProductName(productName);  // non-consumable purchases are restored using this value
        builder.setType(isconsumable?MpUtils.PRODUCT_TYPE_CONSUMABLE : MpUtils.PRODUCT_TYPE_NON_CONSUMABLE); // non-consumable items can be later restored
        builder.setIcon(R.drawable.icon_brand_aptoide);

        PaymentRequest pr = builder.build();
        makePayment(pr);
        Log.d("pois", "makePayment feito!");
    }

    private void finish(int result, Intent i){
        Log.d("pois", "FortumoPaymentActivity setResult and finish");
        setResult(result,i);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null) unregisterReceiver(receiver);
    }

    public static class MyReceiver extends BroadcastReceiver {

        private static String TAG = "pois";

        public void setCallBack(CallBack callBack) {
            this.callBack = callBack;
        }

        public interface CallBack{
            public void onReceive(boolean wasPaid,boolean isconsumable,String payCode,int prodID);
        }

        private CallBack callBack;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("pois", "MyReceiver onReceive");
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
            String payCode = extras.getString("payment_code");
            int prodID = Integer.valueOf(product_name.split("_")[0]);
            if(callBack!=null){
                callBack.onReceive(billingStatus == MpUtils.MESSAGE_STATUS_BILLED || billingStatus == MpUtils.MESSAGE_STATUS_PENDING, isconsumable, payCode,
                        prodID);
            }
        }
    }
}
