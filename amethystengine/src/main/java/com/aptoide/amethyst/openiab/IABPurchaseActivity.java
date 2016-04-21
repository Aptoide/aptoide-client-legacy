package com.aptoide.amethyst.openiab;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.models.PaymentServices;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.paypal.android.sdk.payments.ProofOfPayment;

import org.json.JSONObject;
import org.onepf.oms.BillingBinder;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


import com.aptoide.amethyst.openiab.webservices.BasePurchaseStatusRequest;
import com.aptoide.amethyst.openiab.webservices.IabPurchaseStatusRequest;
import com.aptoide.amethyst.openiab.webservices.IabPurchasesRequest;
import com.aptoide.amethyst.openiab.webservices.IabSkuDetailsRequest;
import com.aptoide.amethyst.openiab.webservices.PayProductRequestBase;
import com.aptoide.amethyst.openiab.webservices.PaypalPurchaseAuthorizationRequest;
import com.aptoide.amethyst.openiab.webservices.json.IabPurchaseStatusJson;
import com.aptoide.amethyst.openiab.webservices.json.IabPurchasesJson;
import com.aptoide.amethyst.openiab.webservices.json.IabSkuDetailsJson;
import com.aptoide.amethyst.dialogs.ProgressDialogFragment;

public class IABPurchaseActivity extends BasePurchaseActivity {
    // Response result codes
    public static final int RESULT_OK = 0;
    public static final int RESULT_USER_CANCELED = 1;
    public static final int RESULT_BILLING_UNAVAILABLE = 3;
    public static final int RESULT_ITEM_UNAVAILABLE = 4;
    public static final int RESULT_DEVELOPER_ERROR = 5;
    public static final int RESULT_ERROR = 6;
    public static final int RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int RESULT_ITEM_NOT_OWNED = 8;

    // Keys for the responses
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String DETAILS_LIST = "DETAILS_LIST";
    public static final String BUY_INTENT = "BUY_INTENT";

    public static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String INAPP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

    // Item types
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";


    private int apiVersion;
    private String type;
    private String sku;
    private String developerPayload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_app_purchase);


        final Intent intent = getIntent();
        developerPayload = intent.getStringExtra("developerPayload");
        apiVersion = intent.getIntExtra("apiVersion", 3);
        sku = intent.getStringExtra("sku");
        type = intent.getStringExtra("type");
        //sku = "com.aptoide.partners";

        //token =  "27286b943179065fcef3b6adcafe8680d8515e4652010602c45f6";

        final String user = intent.getStringExtra("user");

        if (user == null) {
            //
            AccountManager.get(this).addAccount(Aptoide.getConfiguration().getAccountType(), AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        String account = future.getResult().getString(AccountManager.KEY_ACCOUNT_NAME);

                        if(account!=null){
                            updateUI(account);
                        }else {
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }

            }, new Handler(Looper.getMainLooper()));
        } else {
            updateUI(user);
        }
        findViewById(R.id.buttonCancel).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AptoideinAppBilling", "Purchase Cancelled");
                intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_USER_CANCELED);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    private void updateUI(String user){
        //Intent billingIntent = new Intent(this, BillingService.class);
        //bindService(billingIntent, mConnection, BIND_AUTO_CREATE);
        ((TextView) findViewById(R.id.username)).setText(getString(R.string.account) + ": " + user);
        thingThatisNotAService();
    }

    @Override
    protected boolean makeExtraTestsOnPurchaseOk(IabPurchaseStatusJson iabPurchaseStatusJson) {
        return !sku.equals(iabPurchaseStatusJson.getPublisherResponse().getItem().get(0));
    }

    private Bundle getPurchases(int apiVersion, String packageName, String type) throws RemoteException {
        Log.d("AptoideBillingService", "[getPurchases]: " + packageName + " " + type);

        final Bundle result = new Bundle();

        if (apiVersion < 3 || !(ITEM_TYPE_INAPP.equals(type) || ITEM_TYPE_SUBS.equals(type))) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            return result;
        }

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType());

        if(accounts.length == 0) {

            Log.d("AptoideBillingService", "BillingUnavailable: user not logged in");
            result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST, new ArrayList<String>());
            result.putStringArrayList(INAPP_PURCHASE_DATA_LIST, new ArrayList<String>());
            result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST, new ArrayList<String>());
            result.putInt(RESPONSE_CODE, RESULT_OK);

            return result;
        }

        try {
            String token = accountManager.blockingGetAuthToken(accounts[0], "Full access", true);

            if(token != null) {
                final CountDownLatch latch = new CountDownLatch(1);
                IabPurchasesRequest request = new IabPurchasesRequest();
                request.setApiVersion(Integer.toString(apiVersion));
                request.setPackageName(packageName);
                request.setType(type);
                request.setToken(token);

                spiceManager.execute(request, packageName + "-getPurchases-"+type, DurationInMillis.ONE_SECOND*5, new RequestListener<IabPurchasesJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        result.putInt(RESPONSE_CODE, RESULT_ERROR);
                        latch.countDown();
                    }

                    @Override
                    public void onRequestSuccess(IabPurchasesJson response) {
                        if("OK".equals(response.getStatus())) {

                            ArrayList<String> purchaseItemList = (ArrayList<String>) response.getPublisher_response().getItemList();
                            ArrayList<String> purchaseSignatureList = (ArrayList<String>) response.getPublisher_response().getSignatureList();

                            ArrayList<String> purchaseDataList = new ArrayList<String>();
                            for(IabPurchasesJson.PublisherResponse.PurchaseDataObject purchase : response.getPublisher_response().getPurchaseDataList()) {
                                Log.d("AptoideBillingService", "Purchase: " + purchase.getJson());
                                purchaseDataList.add(purchase.getJson());
                            }

                            result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST, purchaseItemList);
                            result.putStringArrayList(INAPP_PURCHASE_DATA_LIST, purchaseDataList);
                            result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST, purchaseSignatureList);
                            if(response.getPublisher_response().getInapp_continuation_token() != null) {
                                result.putString(INAPP_CONTINUATION_TOKEN, response.getPublisher_response().getInapp_continuation_token());
                            }
                            result.putInt(RESPONSE_CODE, RESULT_OK);
                        } else {
                            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
                        }
                        latch.countDown();
                    }
                });

                latch.await();

            }

        } catch (Exception e) {
            e.printStackTrace();
            result.putInt(RESPONSE_CODE, RESULT_ERROR);
        }
        return result;
    }
    protected boolean TestifPurchased() {
        try {
            Bundle bundle = getPurchases(apiVersion, packageName, type);

            for(String json : bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")){

                if(sku.equals(json)){
                    return true;
                }

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e){
            finish();
            return false;
        }
        return false;
    }

    @Override
    protected void requestsetExtra(PayProductRequestBase pprb) {
        pprb.setApiVersion(String.valueOf(apiVersion));
        pprb.setDeveloperPayload(developerPayload);
    }


    private String getMccCode(String networkOperator) {
        return networkOperator == null ? "" : networkOperator.substring(0, mncPortionLength(networkOperator));

    }

    private String getMncCode(String networkOperator) {
        return networkOperator == null ? "" : networkOperator.substring(mncPortionLength(networkOperator));

    }

    private int mncPortionLength(String networkOperator) {
        return Math.min(3, networkOperator.length());
    }
    protected IabSkuDetailsRequest BuildIabSkuDetailsRequest(){
        return new IabSkuDetailsRequest();
    }
    private void thingThatisNotAService(){
        final Activity THIS = this;
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    repo = new AptoideDatabase(Aptoide.getDb()).getRollbackRepo(packageName);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                if(TestifPurchased()){
                    Intent intent = new Intent();
                    intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_ITEM_ALREADY_OWNED);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }

                //token = accountManager.blockingGetAuthToken(accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)[0], "Full access", true);

                //String token =  "27286b943179065fcef3b6adcafe8680d8515e4652010602c45f6";
                final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                IabSkuDetailsRequest request = BuildIabSkuDetailsRequest();

                request.setPackageName(packageName);
                request.setToken(token);
                //request.setOemid(((AptoideConfigurationPartners) Aptoide.getConfiguration()).PARTNERID);

                if (telephonyManager != null && telephonyManager.getSimState()==TelephonyManager.SIM_STATE_READY) {
                    request.setMcc(getMccCode(telephonyManager.getNetworkOperator()));
                    request.setMnc(getMncCode(telephonyManager.getNetworkOperator()));
                    simcc = telephonyManager.getSimCountryIso();
                    request.setSimcc(simcc);
                }
                ArrayList<String> item_list = new ArrayList<String>();
                item_list.add(sku);
                request.setApiVersion(Integer.toString(apiVersion));

                for (String itemId : item_list) {
                    request.addToSkuList(itemId);
                    Log.d("Aptoide-Binder", "Sku details request for " + itemId);
                }

                spiceManager.execute(request, packageName + "-getSkuDetails-" + request.getSkuList() + token, DurationInMillis.ONE_MINUTE, new RequestListener<IabSkuDetailsJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Intent intent = new Intent();
                        intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_ERROR);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onRequestSuccess(IabSkuDetailsJson response) {
                        ArrayList<String> detailsList = new ArrayList<String>();

                        if ("OK".equals(response.getStatus())) {

                            for (IabSkuDetailsJson.PurchaseDataObject details : response.getPublisher_response().getDetailss_list()) {

                                String s = "";
                                try {
                                    s = new ObjectMapper().writeValueAsString(details);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }

                                detailsList.add(s);
                                Log.d("AptoideBillingService", "Sku Details: " + s);
                            }


                            LinearLayout paymentMethodsLayout = (LinearLayout) findViewById(R.id.payment_methods);
                            paymentMethodsLayout.removeAllViews();
                            Button button;

                            if (response.getPayment_services()!=null && response.getPayment_services().isEmpty()) {
                                TextView noPaymentsFound = new TextView(THIS);
                                noPaymentsFound.setText(R.string.no_payments_available);
                                paymentMethodsLayout.addView(noPaymentsFound);
                            }else if(response.getPayment_services() !=null ){

                                for (PaymentServices service : response.getPayment_services()) {

                                    int serviceCode = servicesList.get(service.getShort_name());

                                    switch (serviceCode) {
                                        case PAYPAL_CODE:
                                            for (PaymentServices.PaymentType type : service.getTypes()) {

                                                if ("future".equals(type.getReqType())) {

                                                    button = (Button) LayoutInflater.from(THIS).inflate(R.layout.button_paypal, null);
                                                    button.setText(type.getLabel() + " - " + service.getPrice() + " " + service.getSign());
                                                    paymentMethodsLayout.addView(button);

                                                    PaypalPurchaseAuthorizationRequest request = new PaypalPurchaseAuthorizationRequest();
                                                    request.setToken(token);
                                                    HasAuthorization hasAuthorization = new HasAuthorization(button);
                                                    hasAuthorization.setCurrency(service.getCurrency());
                                                    hasAuthorization.setPrice(service.getPrice());
                                                    hasAuthorization.setTax(service.getTaxRate());
                                                    spiceManager.execute(request, "authorization-" + token, DurationInMillis.ONE_DAY, hasAuthorization);

                                                } else if ("single".equals(type.getReqType())) {

                                                    button = (Button) LayoutInflater.from(THIS).inflate(R.layout.button_visa, null);

                                                    button.setText(type.getLabel() + " - " + service.getPrice() + " " + service.getSign());
                                                    OnPaypalClick onPaypalClick = new OnPaypalClick();
                                                    onPaypalClick.setCurrency(service.getCurrency());
                                                    onPaypalClick.setPrice(service.getPrice());
                                                    onPaypalClick.setTax(service.getTaxRate());
                                                    onPaypalClick.setRepo(repo);


                                                    try {
                                                        JSONObject sku_details = new JSONObject(detailsList.get(0));
                                                        onPaypalClick.setDescription(sku_details.getString("title") + " - " + sku_details.getString("description"));

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        onPaypalClick.setDescription(getString(R.string.no_description));
                                                    }

                                                    button.setOnClickListener(onPaypalClick);
                                                    paymentMethodsLayout.addView(button);

                                                    if(response.getPayment_services().size() == 1 && service.getTypes().size() == 1){
                                                        onPaypalClick.onClick(null);
                                                    }

                                                }
                                            }
                                            break;
                                        case UNITEL_CODE:
                                            caseUNITEL(service,paymentMethodsLayout);
                                            break;
                                    }


                                }
                            }

                            if(!detailsList.isEmpty()) Log.d("AptoideSkudetailsForPurchase", "SkuDetails: " + detailsList.get(0));

                            //if(detailsJson != null) {
                            try {
                                findViewById(R.id.progress).setVisibility(View.GONE);
                                findViewById(R.id.content).setVisibility(View.VISIBLE);

                                JSONObject sku_details = new JSONObject(detailsList.get(0));
                                aptoideProductId = response.getMetadata().getId();
                                ((TextView) findViewById(R.id.title)).setText(sku_details.getString("title"));
                                ((TextView) findViewById(R.id.price)).setText(sku_details.getString("price"));
                                Glide.with(IABPurchaseActivity.this).load(response.getMetadata().getIcon()).into((ImageView) findViewById(R.id.icon));
                                ((TextView) findViewById(R.id.app_purchase_description)).setText(sku_details.getString("description"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Intent intent = new Intent();
                                intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_ERROR);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }

                });
            }
        }).start();
    }

    @Override
    protected void processPaymentConfirmation(final ProofOfPayment confirmation) {
        final BasePurchaseStatusRequest purchaseStatus = BuildPurchaseStatusRequest(confirmation);

        purchaseStatus.setDeveloperPayload(developerPayload);
        purchaseStatus.setApiVersion(String.valueOf(apiVersion));

        DialogFragment df = new ProgressDialogFragment();
        df.show(getSupportFragmentManager(), "pleaseWaitDialog");
        df.setCancelable(false);

        spiceManager.execute(purchaseStatus, new PurchaseRequestListener() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Toast.makeText(Aptoide.getContext(), R.string.error_occured_retry_later, Toast.LENGTH_LONG).show();

                Intent i = buildIntentForAlarm(confirmation, "iab");
                i.putExtra("apiVersion", apiVersion);
                i.putExtra("developerPayload", developerPayload);

                PendingIntent intent = PendingIntent.getBroadcast(getApplicationContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 60000, 60000, intent);

                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    protected BasePurchaseStatusRequest BuildPurchaseStatusRequest() {
        return new IabPurchaseStatusRequest();
    }
}
