package com.aptoide.amethyst.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.stores.Login;



/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 29-11-2013
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class PasswordDialog extends DialogFragment {

    public static final String FRAGMENT_TAG = "passwordDialog";
    private static final String ARG_STORE_ID = "storeId";

    public static PasswordDialog newInstance(final long storeId) {
        final PasswordDialog fragment = new PasswordDialog();
        final Bundle args = new Bundle();
        args.putLong(ARG_STORE_ID, storeId);
        fragment.setArguments(args);
        return fragment;
    }

    private long storeId;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args == null) {
            return;
        }
        storeId = args.getLong(ARG_STORE_ID, -1);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_pvt_store, null);
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(getString(R.string.subscribe_pvt_store))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String username = ((EditText) v.findViewById(R.id.edit_store_username)).getText().toString();
                        String password = ((EditText) v.findViewById(R.id.edit_store_password)).getText().toString();

                        final Fragment targetFragment = getTargetFragment();
                        if (targetFragment != null) {
                            Intent i = new Intent();
                            i.putExtra("username", username);
                            i.putExtra("password", password);
                            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                        } else {
                            final Login login = new Login();
                            login.setUsername(username);
                            login.setPassword(password);
                            try {
                                final String sha = AptoideUtils.Algorithms.computeSHA1sum(password.trim());
                                login.setPasswordSha1(sha);
                            } catch (Exception ignore) { }
                            final OttoEvents.StoreAuthorizationEvent event = new OttoEvents
                                    .StoreAuthorizationEvent(storeId, login);
                            BusProvider.getInstance().post(event);
                        }
//                        FlurryAgent.logEvent("Added_Private_Store");

                    }
                }).create();


        return builder;
    }
}
