package com.aptoide.amethyst.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.aptoide.amethyst.R;

import java.util.List;


import com.aptoide.amethyst.adapter.timeline.TimelineCommentsAdapter;
import com.aptoide.amethyst.webservices.timeline.TimeLineManager;
import com.aptoide.amethyst.webservices.timeline.json.ApkInstallComments;

/**
 * Created by fabio on 14-10-2015.
 */
public class TimeLineCommentsDialog extends DialogFragment {

    public static final String POSTID = "ID";
    public static final String LIKES = "LIKES";
    public static final String POSITION = "position";
    private int position;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    public void setCallback(TimeLineManager callback) {
        this.callback = callback;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private TimeLineManager callback;
    private long id;
    private int likesNumber;
    private ListView lv;

    public void setComments(List<ApkInstallComments.Comment> entry){
        lv.setAdapter(new TimelineCommentsAdapter(getActivity(), entry));
        lv.setVisibility(View.VISIBLE);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        setStyle( DialogFragment.STYLE_NORMAL, R.style.TimelineCommentsDialog );
        final Context c = getActivity();
        final View dialogView = LayoutInflater.from(c).inflate(R.layout.dialog_timelinecomments, null);
        id=getArguments().getLong(POSTID);
        position = getArguments().getInt(POSITION);
        likesNumber = Integer.valueOf(getArguments().getString(LIKES));

        lv = (ListView) dialogView.findViewById(R.id.TimeLineListView);
        final TextView likes = (TextView) dialogView.findViewById(R.id.likes);

        if(likesNumber >= 1) {
            likes.setVisibility(View.VISIBLE);
            if(likesNumber == 1) {
                likes.setText(likesNumber + " " + getString(R.string.timeline_like));
            }else{
                likes.setText(likesNumber + " " + getString(R.string.likes));
            }
        }else{
            likes.setVisibility(View.GONE);
        }
        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeLineManager parentFragment = (TimeLineManager) getParentFragment();
                if (parentFragment != null) {
                    parentFragment.openWhoLikesDialog(id, likesNumber, position);
                }
            }
        });
        final ImageButton send_button = (ImageButton) dialogView.findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeLineManager parentFragment = (TimeLineManager) getParentFragment();

                if (parentFragment != null) {
                    String s = ((EditText) dialogView.findViewById(R.id.TimeLineCommentEditText)).getText().toString();
                    parentFragment.commentPost(id, s, position);
                }
            }
        });

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
    public void onResume() {
        super.onResume();
        TimeLineManager parentFragment = (TimeLineManager) getParentFragment();
        parentFragment.getComments(id);
    }
}