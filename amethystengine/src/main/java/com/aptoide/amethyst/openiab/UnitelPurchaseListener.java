package com.aptoide.amethyst.openiab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.aptoide.amethyst.R;

/**
 * Created by rmateus on 28-05-2014.
 */

public class UnitelPurchaseListener implements View.OnClickListener {

    private FragmentManager manager;
    String price;
    String telecom;
    String titlet;
    private Callback callback;
    private int payType;
    private String imsi;
    private final String currency;
    private final String priceWithoutSign;

    public UnitelPurchaseListener(FragmentManager manager , String price, String telecom, String titlet, int payType, String imsi, String currency, String priceWithoutSign) {
        this.manager = manager;
        this.price = price;
        this.telecom = telecom;
        this.titlet = titlet;

        this.payType = payType;
        this.imsi = imsi;
        this.currency = currency;
        this.priceWithoutSign = priceWithoutSign;
    }

    @Override
    public void onClick(View v) {

        UnitelDialog unitelDialog = new UnitelDialog();

        Bundle bundle = new Bundle();
        bundle.putString("title", titlet);
        bundle.putString("telecom", telecom);
        bundle.putString("price", price);
        bundle.putString("priceWithoutSign", priceWithoutSign);
        bundle.putString("imsi", imsi);
        bundle.putString("currency", currency);
        bundle.putInt("payType", payType);

        unitelDialog.setArguments(bundle);

        unitelDialog.show(manager, "unitelDialog");


    }

    public static class UnitelDialog extends DialogFragment {

        Callback callback;

        String titlet;
        private String telecom;
        private String price;
        private String imsi;
        private String currency;
        private int payType;
        private String priceWithoutSign;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            callback = (Callback) activity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            titlet = getArguments().getString("title");
            telecom = getArguments().getString("telecom");
            price = getArguments().getString("price");
            priceWithoutSign = getArguments().getString("priceWithoutSign");

            imsi = getArguments().getString("imsi");
            currency = getArguments().getString("currency");
            payType = getArguments().getInt("payType");

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String text = getString(R.string.confirm_carrier_payment);
            String confirmPurchase = text.replace("{title}", titlet).replace("{value}", price).replace("{carrier}", telecom);

            return new AlertDialog.Builder(getActivity())
                    //.setTitle(R.string.confirm_carrier_payment_title)
                    .setMessage(confirmPurchase)
                    .setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onClick(payType, imsi, priceWithoutSign, currency);

                        }
                    })
                    .setNegativeButton(R.string.change_payment, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .create();
        }
    }


}
