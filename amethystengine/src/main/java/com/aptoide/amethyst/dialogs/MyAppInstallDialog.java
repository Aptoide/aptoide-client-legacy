package com.aptoide.amethyst.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;

/**
 * Created by rmateus on 15-01-2014.
 */
public class MyAppInstallDialog extends DialogFragment {

    private DialogInterface.OnClickListener okListener;
    private String appName;
    private DialogInterface.OnDismissListener dismissListener;

    public MyAppInstallDialog() {
    }

    public static MyAppInstallDialog newInstance(String appName, DialogInterface.OnClickListener okListener, DialogInterface.OnDismissListener dismissListener) {
        MyAppInstallDialog dialog = new MyAppInstallDialog();
        dialog.appName = appName;
        dialog.okListener = okListener;
        dialog.dismissListener = dismissListener;
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(Aptoide.getConfiguration().getMarketName())
                .setIcon(android.R.drawable.ic_menu_more)
                .setMessage(AptoideUtils.StringUtils.getFormattedString(getContext(), R.string.installapp_alrt, appName))
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        setCancelable(false);

        return builder;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (dismissListener != null && isAdded()) {
            dismissListener.onDismiss(dialog);
        }
        super.onDismiss(dialog);
    }
}
