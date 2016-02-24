package com.aptoide.amethyst.openiab;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.aptoide.amethyst.openiab.webservices.IabPurchaseStatusRequest;
import com.aptoide.amethyst.openiab.webservices.PaidAppPurchaseStatusRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.retry.RetryPolicy;

import com.aptoide.amethyst.openiab.webservices.BasePurchaseStatusRequest;

import retrofit.RestAdapter;

/**
 * Created by rmateus on 02-06-2014.
 */
public class PayPalReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        final BasePurchaseStatusRequest purchaseStatus;


        if(intent.getStringExtra("paymentType").equals("iab")){
            purchaseStatus = new IabPurchaseStatusRequest();
        }else{
            purchaseStatus = new PaidAppPurchaseStatusRequest();
        }




        final int apiVersion = intent.getIntExtra("apiVersion", 3);
        purchaseStatus.setApiVersion(String.valueOf(apiVersion));
        final String token = intent.getStringExtra("token");
        purchaseStatus.setToken(token);

        final int aptoideProductId = intent.getIntExtra("aptoideProductId", 0);
        purchaseStatus.setProductId(aptoideProductId);
        purchaseStatus.setPayType(1);
        final String paymentId = intent.getStringExtra("paymentId");
        purchaseStatus.setPayKey(paymentId);
        final double tax = intent.getDoubleExtra("tax", 0.0);
        purchaseStatus.setTaxRate(tax);
        final double price = intent.getDoubleExtra("price", 0.0);
        purchaseStatus.setPrice(price);
        final String developerPayload = intent.getStringExtra("developerPayload");
        purchaseStatus.setDeveloperPayload(developerPayload);
        final String currency = intent.getStringExtra("currency");
        purchaseStatus.setCurrency(currency);

        final String simcc = intent.getStringExtra("simcc");
        if(simcc!=null)purchaseStatus.setSimcc(simcc);


        purchaseStatus.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getRetryCount() {
                return 0;
            }

            @Override
            public void retry(SpiceException e) {

            }

            @Override
            public long getDelayBeforeRetry() {
                return 0;
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //purchaseStatus.setHttpRequestFactory(AndroidHttp.newCompatibleTransport().createRequestFactory());

                    purchaseStatus.setService(new RestAdapter.Builder().setEndpoint("http://").build().create(BasePurchaseStatusRequest.Webservice.class));
                    purchaseStatus.loadDataFromNetwork();
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Intent i = new Intent("PAYPAL_PAYMENT");
                    i.putExtra("token", token);
                    i.putExtra("apiVersion", apiVersion);
                    i.putExtra("aptoideProductId", aptoideProductId);
                    i.putExtra("developerPayload", developerPayload);
                    i.putExtra("tax", tax);
                    i.putExtra("price", price);
                    i.putExtra("currency", currency);
                    i.putExtra("paymentId", paymentId);
                    if(simcc!=null)i.putExtra("simcc", simcc);

                    PendingIntent intent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

                    manager.cancel(intent);

                    intent.cancel();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }
}
