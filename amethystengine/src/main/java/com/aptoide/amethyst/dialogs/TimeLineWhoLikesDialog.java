package com.aptoide.amethyst.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aptoide.amethyst.R;

import java.util.List;


import com.aptoide.amethyst.adapter.timeline.TimeLineFriendsListAdapter;
import com.aptoide.amethyst.webservices.timeline.TimeLineManager;
import com.aptoide.amethyst.webservices.timeline.json.Friend;

/**
 * Created by fabio on 14-10-2015.
 */
public class TimeLineWhoLikesDialog extends DialogFragment {

    public static final String POSTID = "ID";
    public static final String LIKES = "LIKES";


    public void setCallback(TimeLineManager callback) {
        this.callback = callback;
    }


    private TimeLineManager callback;
    private long id;
    private ListView lv;
    private int likesNumber;

    public void setFriends(List<Friend> entry){
        lv.setAdapter(new TimeLineFriendsListAdapter(getActivity(), entry));
        lv.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle( DialogFragment.STYLE_NORMAL, R.style.TimelineCommentsDialog );
        final Context c = getActivity();

        final View dialogView = LayoutInflater.from(c).inflate(R.layout.dialog_wholikes, null);
        likesNumber = Integer.valueOf(getArguments().getString(LIKES));
        id=getArguments().getLong(POSTID);
        lv = (ListView) dialogView.findViewById(R.id.TimeLineListView);
        final TextView likes = (TextView) dialogView.findViewById(R.id.likes);

        if(likesNumber == 1) {
            likes.setText(likesNumber + " " + getString(R.string.timeline_like));
        }else{
            likes.setText(likesNumber + " " + getString(R.string.likes));
        }

        return new AlertDialog.Builder(c)
                .setView(dialogView)
                .create();
    }

    @Override
    public void onDestroyView() {
        lv = null;
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        //TimeLineManager parentFragment = (TimeLineManager) getParentFragment();
        //parentFragment.openCommentsDialog(id,getArguments().getInt(TimeLineCommentsDialog.POSITION));


    }

    @Override
    public void onResume() {
        super.onResume();
        TimeLineManager parentFragment = (TimeLineManager) getParentFragment();
        parentFragment.getWhoLiked(id);
    }
}
