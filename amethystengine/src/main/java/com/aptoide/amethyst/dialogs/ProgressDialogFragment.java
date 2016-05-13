package com.aptoide.amethyst.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.aptoide.amethyst.R;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 29-10-2013
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public class ProgressDialogFragment extends DialogFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnCancelListener){
            onCancelListener = ((OnCancelListener) activity);
        }
    }

    public interface OnCancelListener{
        void onCancel();
    }

    OnCancelListener onCancelListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.please_wait));
        pd.setCanceledOnTouchOutside(false);
        return pd;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(onCancelListener!=null)onCancelListener.onCancel();
    }

}
