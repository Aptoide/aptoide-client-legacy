package com.aptoide.amethyst.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.aptoide.amethyst.R;

import com.aptoide.amethyst.ui.ScheduledDownloadsActivity;


/**
 * Created by rmateus on 31-07-2014.
 */
public class ScheduledDownloadsDialog extends DialogFragment {

    public interface DialogCallback {
        public void onOkClick();
        public void onCancelClick();
    }

    private DialogCallback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (ScheduledDownloadsActivity)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final AlertDialog scheduleDownloadDialog = dialogBuilder.create();
        scheduleDownloadDialog.setTitle(getText(R.string.schDwnBtn));
        scheduleDownloadDialog.setIcon(android.R.drawable.ic_dialog_alert);
        scheduleDownloadDialog.setCancelable(false);
        scheduleDownloadDialog.setMessage(getText(R.string.schDown_install));
        scheduleDownloadDialog.setButton(Dialog.BUTTON_POSITIVE, getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (callback!=null) callback.onOkClick();
            }
        });
        scheduleDownloadDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(android.R.string.no), new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (callback!=null) callback.onCancelClick();
            }
        });


        return scheduleDownloadDialog;
    }
}
