package com.aptoide.amethyst.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;

/**
 * Created by fabio on 17-11-2015.
 */
public class FlagApkDialog extends DialogFragment {
    public enum Uservote {
        good, license, fake, freeze, virus, novote;
    }

    public static final String USERVOTE_ARGUMENT_KEY = "uservote";

    ApkFlagCallback flagApkCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        flagApkCallback = (ApkFlagCallback) activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        flagApkCallback = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_flag_app, null);
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(null)
                .create();

        if(getArguments() != null && getArguments().containsKey(FlagApkDialog.USERVOTE_ARGUMENT_KEY)) {
            Logger.d("apkflag", "uservote: " + getArguments().getString(FlagApkDialog.USERVOTE_ARGUMENT_KEY));
            Uservote uservote = Uservote.valueOf(getArguments().getString(FlagApkDialog.USERVOTE_ARGUMENT_KEY));

            int uservoteButtonId = getButtonIdFromUservote(uservote);
            if(uservoteButtonId != -1) {
                ((RadioButton) view.findViewById(uservoteButtonId)).setChecked(true);
            }
        }

        view.findViewById(R.id.button_mark_flag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagApkCallback != null) {
                    int checkedButtonId = ((RadioGroup) view.findViewById(R.id.flag_group)).getCheckedRadioButtonId();

                    if (checkedButtonId != -1) {
                        Log.d("apkflag", "flag: " + getUservoteFromButtonId(checkedButtonId).name());
                        flagApkCallback.addApkFlagClick(getUservoteFromButtonId(checkedButtonId).name());
                        dismiss();
                    }
                }
            }
        });

        return builder;
    }

    private static int getButtonIdFromUservote(Uservote uservote) {
        switch (uservote) {
            case good:
                return R.id.button_good;
            case license:
                return R.id.button_license;
            case fake:
                return R.id.button_fake;
            case freeze:
                return R.id.button_freeze;
            case virus:
                return R.id.button_virus;
            default:
                return -1;
        }
    }

    private static Uservote getUservoteFromButtonId(int buttonId) {
        if (buttonId == R.id.button_good) {
            return Uservote.good;
        } else if (buttonId == R.id.button_license) {
            return Uservote.license;
        } else if (buttonId == R.id.button_fake) {
            return Uservote.fake;
        } else if (buttonId == R.id.button_freeze) {
            return Uservote.freeze;
        } else if (buttonId == R.id.button_virus) {
            return Uservote.virus;
        }
        return Uservote.novote;
    }
    public interface ApkFlagCallback {
        void addApkFlagClick(String flag);
    }
}
