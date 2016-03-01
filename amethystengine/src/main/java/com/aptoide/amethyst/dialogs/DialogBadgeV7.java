package com.aptoide.amethyst.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware;

import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.PASSED;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.TRUSTED;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.UNKNOWN;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.WARN;
import static com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.WARNING;

/**
 * Created by hsousa on 18/11/15.
 */
public class DialogBadgeV7 extends DialogFragment {

    protected Malware malware;
    protected String appName;
    protected String status;


    public static DialogBadgeV7 newInstance(Malware malware, String appName, String status) {

        DialogBadgeV7 dialog = new DialogBadgeV7();
        dialog.malware = malware;
        dialog.appName = appName;
        dialog.status = status;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);
        }
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_badge, null);
        AlertDialog builder = new AlertDialog.Builder(getActivity()).setView(v).create();

        v.findViewById(R.id.dialog_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (malware != null && malware.rank != null) {

            switch (malware.rank) {
                case TRUSTED:
                    v.findViewById(R.id.trusted_header_layout).setVisibility(View.VISIBLE);
                    break;
                case WARNING:
                    v.findViewById(R.id.warning_header_layout).setVisibility(View.VISIBLE);
                    break;
                case UNKNOWN:
                    v.findViewById(R.id.unknown_header_layout).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.tr_unknown).setVisibility(View.VISIBLE);
                    // Doesn't need to do more logic, exit.
                    return builder;
            }
        }

        if (malware != null && malware.reason != null) {
            if (malware.reason.scanned != null && malware.reason.scanned.status != null && (PASSED.equals(malware.reason.scanned.status) || WARN.equals(malware.reason.scanned.status))) {

                if (malware.reason.scanned.avInfo != null) {
                    v.findViewById(R.id.tr_scanned).setVisibility(View.VISIBLE);
                }
            }

            if (malware.reason.thirdpartyValidated != null && GetAppMeta.File.Malware.GOOGLE_PLAY.equalsIgnoreCase(malware.reason.thirdpartyValidated.store)) {
                v.findViewById(R.id.tr_third_party).setVisibility(View.VISIBLE);
            }

            if (malware.reason.signatureValidated != null && malware.reason.signatureValidated.status != null) {

                switch (malware.reason.signatureValidated.status) {
                    case PASSED:
                        v.findViewById(R.id.tr_signature).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.tv_reason_signature_validation)).setText(getString(R.string.reason_signature));
                        break;
                    case "failed":
                        // still in study by the UX team
                        v.findViewById(R.id.tr_signature).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.iv_signature).setVisibility(View.INVISIBLE);
                        ((TextView) v.findViewById(R.id.tv_reason_signature_validation)).setText(getString(R.string.reason_failed));
                        break;
                    case "blacklisted":
                        // still in study by the UX team
//                        v.findViewById(R.id.malware.reason_signature_not_validated).setVisibility(View.VISIBLE);
//                        ((TextView) v.findViewById(R.id.malware.reason_signature_not_validated)).setText(getString(R.string.application_signature_blacklisted));
                        break;
                }
            }

            if (malware.reason.manualQA != null && malware.reason.manualQA.status != null && PASSED.equals(malware.reason.manualQA.status)) {
                v.findViewById(R.id.tr_manual).setVisibility(View.VISIBLE);
            }
        }

        return builder;
    }

}
