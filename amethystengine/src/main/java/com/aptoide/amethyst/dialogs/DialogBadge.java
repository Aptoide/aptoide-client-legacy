package com.aptoide.amethyst.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.Reason;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware.Reason.Scanned.AvInfo;

import java.util.Iterator;

/**
 * Created by tdeus on 1/16/14.
 */
public class DialogBadge extends DialogFragment {

    protected Reason reason;
    protected String appName;
    protected String status;


    public static DialogBadge newInstance(Reason reason, String appName, String status) {

        DialogBadge dialog = new DialogBadge();
        dialog.reason = reason;
        dialog.appName = appName;
        dialog.status = status;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_anti_malware, null);
//        String appName = getArguments().getString("appName");
//        String status = getArguments().getString("status");
//        GetApkInfoJson.Malware.Reason reason = ((AppViewActivity) getActivity()).getReason();
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(status.equals("TRUSTED") ? getString(R.string.app_trusted, appName) : getString(R.string.app_warning, appName))
//                .setTitle(status.equals("scanned") ? getString(R.string.app_trusted, appName) : getString(R.string.app_warning, appName))
//                .setIcon(status.equals("scanned") ? getResources().getDrawable(R.drawable.ic_trusted) : getResources().getDrawable(R.drawable.ic_warning))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        if (reason != null) {
            if (reason.scanned != null && reason.scanned.status != null && (reason.scanned.status.equals("passed") || reason.scanned.status.equals("warn"))) {

                if (reason.scanned.avInfo != null) {
                    StringBuilder av = new StringBuilder();
                    Iterator<AvInfo> iterator = reason.scanned.avInfo.iterator();
                    while (iterator.hasNext()) {
                        AvInfo avInfo = iterator.next();
                        av.append(avInfo.name);
                        if (iterator.hasNext()) {
                            av.append(", ");
                        }
                    }

                    v.findViewById(R.id.reason_scanned_description).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.reason_scanned).setVisibility(View.VISIBLE);
                    ((TextView) v.findViewById(R.id.reason_scanned_description)).setText(getString(R.string.scanned_with_av));
                    ((TextView) v.findViewById(R.id.reason_scanned)).setText(av.toString());
                }
            }

            if (reason.thirdpartyValidated != null) {
                v.findViewById(R.id.reason_thirdparty_validated_description).setVisibility(View.VISIBLE);
                v.findViewById(R.id.reason_thirdparty_validated).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.reason_thirdparty_validated_description)).setText(getString(R.string.compared_with_another_marketplace));
                ((TextView) v.findViewById(R.id.reason_thirdparty_validated)).setText(reason.thirdpartyValidated.store);
            }

            if (reason.signatureValidated != null && reason.signatureValidated.status != null) {

                v.findViewById(R.id.reason_signature_validation_description).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.reason_signature_validation_description)).setText(getString(R.string.application_signature_analysis));

                switch (reason.signatureValidated.status) {
                    case "passed":
                        v.findViewById(R.id.reason_signature_validated).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.reason_signature_validated)).setText(getString(R.string.application_signature_matched));
                        break;
                    case "failed":
                        v.findViewById(R.id.reason_signature_not_validated).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.reason_signature_not_validated)).setText(getString(R.string.application_signature_not_matched));
                        break;
                    case "blacklisted":
                        v.findViewById(R.id.reason_signature_not_validated).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.reason_signature_not_validated)).setText(getString(R.string.application_signature_blacklisted));
                        break;
                }
            }

            if (reason.manualQA != null && reason.manualQA.status != null && reason.manualQA.status.equals("passed")) {
                v.findViewById(R.id.reason_manual_qa_description).setVisibility(View.VISIBLE);
                v.findViewById(R.id.reason_manual_qa).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.reason_manual_qa_description)).setText(getString(R.string.scanned_manually_by_aptoide_team));
                ((TextView) v.findViewById(R.id.reason_manual_qa)).setText(getString(R.string.scanned_verified_by_tester));
            }

        }

        return builder;
    }

}
