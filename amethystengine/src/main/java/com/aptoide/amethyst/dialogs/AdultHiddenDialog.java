package com.aptoide.amethyst.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.CheckBox;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;

/**
 * Created by rmateus on 30-12-2014.
 */
public class AdultHiddenDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CheckBox checkBox = (CheckBox) getDialog().findViewById(R.id.dontshow_checkbox);
                AptoideUtils.getSharedPreferences().edit().putBoolean(Constants.SHOW_ADULT_HIDDEN, !checkBox.isChecked()).apply();
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        new AdultDialog().show(getFragmentManager(), Constants.ADULT_DIALOG);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(LayoutInflater.from(getActivity()).inflate(R.layout.hidden_adult, null))
                .setPositiveButton(R.string.yes, onClickListener)
                .setNegativeButton(R.string.no, onClickListener);
        return builder.create();
    }
}
