package com.aptoide.amethyst.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.ui.callbacks.AddCommentCallback;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta.File.Malware;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 25-10-2013
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
public class AptoideDialog {

//    public static DialogFragment badgeDialog(Malware malware, String appName, String status) {
//        return DialogBadge.newInstance(malware.reason, appName, status);
//    }

    public static DialogFragment badgeDialogV7(Malware malware, String appName, String status) {
        return DialogBadgeV7.newInstance(malware, appName, status);
    }


//    public static DialogFragment addStoreDialog(){
//        return new AddStoreDialog();
//    }

    public static DialogFragment allowRootDialog(){
        return new AllowRootDialog();
    }

    public static DialogFragment pleaseWaitDialog(){
        return new ProgressDialogFragment();
    }

//    public static DialogFragment passwordDialog(){ return new PasswordDialog(); }
//
//    public static DialogFragment wrongVersionXmlDialog(){
//        return new WrongXmlVersionDialog();
//    }
//
//    public static ErrorDialog errorDialog(){
//        return new ErrorDialog();
//    }

    public static ReplyCommentDialog replyCommentDialog(int commentId, String replyingTo, AddCommentCallback addCommentCallback) {
        ReplyCommentDialog fragment = new ReplyCommentDialog(addCommentCallback);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.COMMENT_ID_KEY, commentId);
        bundle.putString(Constants.REPLAYING_TO_KEY, replyingTo);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static DialogFragment myAppInstall(String appName, DialogInterface.OnClickListener okListener, DialogInterface.OnDismissListener dismissListener) {

//        DialogFragment fragment = MyAppInstallDialog.newInstance(appName, okListener, dismissListener);
//
//        Bundle bundle = new Bundle();
//
//        bundle.putString("appName", appName);
//        fragment.setArguments(bundle);

        return MyAppInstallDialog.newInstance(appName, okListener, dismissListener);
    }

    public static DialogFragment addMyAppStore(String repoName, MyAppStoreDialog.MyAppsAddStoreInterface myAppsAddStoreInterface) {

        MyAppStoreDialog fragment = new MyAppStoreDialog();
        fragment.setMyAppsAddStoreInterface(myAppsAddStoreInterface);

        Bundle bundle = new Bundle();
        bundle.putString("repoName", repoName);
        fragment.setArguments(bundle);

        return fragment;
    }

//    public static DialogFragment updateUsernameDialog() {
//        return new UsernameDialog();
//    }
//
//    public static FlagApkDialog flagAppDialog(String uservote) {
//        FlagApkDialog flagApkDialog = new FlagApkDialog();
//        if(uservote != null) {
//            Bundle bundle = new Bundle();
//            bundle.putString(FlagApkDialog.USERVOTE_ARGUMENT_KEY, uservote);
//            flagApkDialog.setArguments(bundle);
//        }
//        return flagApkDialog;
//    }


    public static final int MSG_BUTTON_YES = 0;
    public static final int MSG_BUTTON_NO = 1;

    public static AlertDialog msgBoxYesNo(Context context, String title, String msg, final DialogInterface.OnClickListener listener, boolean canceledOnTouchOutside) {
        AlertDialog msgAlert = new AlertDialog.Builder(context).create();
        msgAlert.setTitle(title);
        msgAlert.setCanceledOnTouchOutside(canceledOnTouchOutside);
        msgAlert.setMessage(msg);
        msgAlert.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, MSG_BUTTON_YES);
                    }
                });
        msgAlert.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, MSG_BUTTON_NO);
                    }
                });
        msgAlert.show();
        return msgAlert;
    }

    public static FlagApkDialog flagAppDialog(String uservote) {
        FlagApkDialog flagApkDialog = new FlagApkDialog();
        if(uservote != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FlagApkDialog.USERVOTE_ARGUMENT_KEY, uservote);
            flagApkDialog.setArguments(bundle);
        }
        return flagApkDialog;
    }

    public static void showDialogAllowingStateLoss(DialogFragment dialog, FragmentManager fragmentManager, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(dialog, tag);
        ft.commitAllowingStateLoss();
    }
}
