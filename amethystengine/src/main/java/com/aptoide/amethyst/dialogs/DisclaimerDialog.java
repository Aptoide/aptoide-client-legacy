package com.aptoide.amethyst.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aptoide.amethyst.R;

/**
 * Created by xuying on 04-08-2016.
 */
public class DisclaimerDialog extends DialogFragment {

    public static DisclaimerDialog newInstance() {
        final DisclaimerDialog fragment = new DisclaimerDialog();
        final Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.disclaimer_dialog, null);
        v.findViewById(R.id.ok_button).setVisibility(View.VISIBLE);
        v.findViewById(R.id.header_disclaimer).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.header_disclaimer)).setText(R.string.header_disclaimer);
        v.findViewById(R.id.disclaimer).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.disclaimer)).setText(R.string.disclaimer);

        final AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setCancelable(false)
                .create();
        final Button diagButton = (Button) v.findViewById(R.id.ok_button);
        diagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        return builder;
    }
}