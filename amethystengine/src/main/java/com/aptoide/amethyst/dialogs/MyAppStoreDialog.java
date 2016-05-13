package com.aptoide.amethyst.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.aptoide.amethyst.R;

/**
 *
 * todo
 * Created by hsousa on 09/09/15.
 */
public class MyAppStoreDialog extends DialogFragment {

    private MyAppsAddStoreInterface myAppsAddStoreInterface;

    public MyAppStoreDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String repoName = getArguments().getString("repoName");
        DialogInterface.OnClickListener okListener = myAppsAddStoreInterface != null
                ? myAppsAddStoreInterface.getOnMyAppAddStoreListener(repoName)
                : null;
        if (myAppsAddStoreInterface == null) {
            dismissAllowingStateLoss();
        }

        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.subscribe_store))
                .setIcon(android.R.drawable.ic_menu_more)
                .setCancelable(false)
                .setMessage((getString(R.string.subscribe_newrepo_alrt) + repoName + " ?"))
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        return builder;
    }

    public void setMyAppsAddStoreInterface(MyAppsAddStoreInterface myAppsAddStoreInterface) {
        this.myAppsAddStoreInterface = myAppsAddStoreInterface;
    }

    public interface MyAppsAddStoreInterface {

        DialogInterface.OnClickListener getOnMyAppAddStoreListener(String repo);
    }
}