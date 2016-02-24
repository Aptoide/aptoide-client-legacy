package com.aptoide.amethyst.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;

/**
 * Created by fabio on 09-10-2015.
 */
public class AllowRootDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.root_access_dialog))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        FlurryAgent.logEvent("Dialog_Root_Allowed_Access");
                        PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putBoolean("allowRoot", true).commit();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putBoolean("allowRoot", false).commit();
                    }
                })
                .create();

        return builder;
    }

}