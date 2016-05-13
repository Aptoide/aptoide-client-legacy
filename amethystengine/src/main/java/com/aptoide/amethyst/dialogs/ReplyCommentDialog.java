package com.aptoide.amethyst.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.ui.callbacks.AddCommentCallback;
import com.aptoide.dataprovider.webservices.models.Constants;

/**
 * Created by jcosta on 01-07-2014.
 */
@SuppressLint("ValidFragment")
public class ReplyCommentDialog extends DialogFragment {

    private AddCommentCallback addCommentCallback;

    public ReplyCommentDialog(AddCommentCallback addCommentCallback) {
        this.addCommentCallback = addCommentCallback;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int commentId = getArguments().getInt(Constants.COMMENT_ID_KEY);
        String replyingTo = getArguments().getString(Constants.REPLAYING_TO_KEY);
        if (TextUtils.isEmpty(replyingTo)) {
            replyingTo = getActivity().getResources().getString(R.string.write_your_comment);
        } else {
            replyingTo = getString(R.string.reply_to, replyingTo);
        }

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_reply_comment, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(replyingTo)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String replyText = ((EditText) v.findViewById(R.id.edit_reply)).getText().toString();
                        String commentIdString = null;
                        if (commentId > 0) {
                            commentIdString = Integer.toString(commentId);
                        }

                        if (addCommentCallback != null) {
                            addCommentCallback.addComment(replyText, commentIdString);
//                            FlurryAgent.logEvent("App_View_Replied_Comment");
                        }
                    }
                }).create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        addCommentCallback = null;
    }

}
