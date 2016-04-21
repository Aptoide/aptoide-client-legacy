package com.aptoide.amethyst.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.webservices.models.Constants;

/**
 * Created by rmateus on 07-03-2014.
 */
public class AdultDialog extends DialogFragment {

    public static final String MATUREPIN = "Maturepin";

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        BusProvider.getInstance().post(new OttoEvents.MatureEvent(false));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return buildAreYouAdultDialog(getActivity(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    BusProvider.getInstance().post(new OttoEvents.MatureEvent(true));
                    PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().putBoolean(Constants.MATURE_CHECK_BOX, true).apply();
                } else {
                    BusProvider.getInstance().post(new OttoEvents.MatureEvent(false));
                }
            }
        });
    }
    public static Dialog dialogRequestMaturepin(final Context context, final DialogInterface.OnClickListener positiveButtonlistener) {
        final View v = LayoutInflater.from(context).inflate(R.layout.dialog_requestpin, null);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int pin = SecurePreferences.getInstance().getInt(AdultDialog.MATUREPIN, -1);
                        String pintext = ((EditText) v.findViewById(R.id.pininput)).getText().toString();
                        if (pintext.length() > 0 && Integer.valueOf(pintext) == pin) {
//                            FlurryAgent.logEvent("Dialog_Adult_Content_Inserted_Pin");
                            positiveButtonlistener.onClick(dialog, which);
                        } else {
//                            FlurryAgent.logEvent("Dialog_Adult_Content_Inserted_Wrong_Pin");
                            Toast.makeText(context, context.getString(R.string.adult_pin_wrong), Toast.LENGTH_SHORT).show();
                            dialogRequestMaturepin(context, positiveButtonlistener).show();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        positiveButtonlistener.onClick(dialog, which);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(R.string.request_adult_pin)
                .setView(v)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .setNegativeButton(android.R.string.cancel, onClickListener);
        return builder.create();
    }

    private static Dialog dialogAsk21(final Context c, final DialogInterface.OnClickListener positiveButtonlistener) {
        return new AlertDialog.Builder(c)
                .setMessage(c.getString(R.string.are_you_adult))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        FlurryAgent.logEvent("Dialog_Adult_Content_Confirmed_More_Than_21_Years_Old");
                        positiveButtonlistener.onClick(dialog, which);
                        Analytics.AdultContent.unlock();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Analytics.AdultContent.lock();
                    }
                })
                .create();
    }

    public static Dialog buildAreYouAdultDialog(final Context c, final DialogInterface.OnClickListener positiveButtonlistener) {
        int pin = SecurePreferences.getInstance().getInt(MATUREPIN, -1);
        if (pin == -1) {
            return dialogAsk21(c, positiveButtonlistener);
        } else {
//            FlurryAgent.logEvent("Dialog_Adult_Content_Requested_Mature_Content_Pin");
            return dialogRequestMaturepin(c, positiveButtonlistener);
        }

    }

}
