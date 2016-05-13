package com.aptoide.amethyst.viewholders.store;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.models.displayables.CommentItem;
import com.aptoide.models.displayables.Displayable;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.callbacks.AddCommentVoteCallback;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 21/07/15.
 */
public class CommentViewHolder extends BaseViewHolder {

    private final Activity activity;
    private final int colorResId;
    public ImageView useravatar;
    public TextView username;
    public TextView timestamp;
    public TextView replyComment;
    public TextView text;
    public TextView appname;
    public ImageButton overflow;
    public TextView votes;
    public View verticalSeparator;
    public RelativeLayout cardLayout;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public CommentViewHolder(View itemView, int viewType, Activity activity, int colorResId) {
        super(itemView, viewType);
        this.activity = activity;
        this.colorResId = colorResId;
    }

    @Override
    public void populateView(Displayable displayable) {
//        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        final CommentItem commentItem = (CommentItem) displayable;
        String dateString = "";
        final Context context = itemView.getContext();
        try {
            dateString = AptoideUtils.DateTimeUtils.getInstance(context).getTimeDiffString(context, dateFormatter.parse(commentItem.timestamp).getTime());
        } catch (ParseException e) {
            Logger.printException(e);
        }

        itemView.setOnClickListener(new BaseAdapter.CommentItemOnClickListener(commentItem));
        timestamp.setText(dateString);
        replyComment.setVisibility(View.GONE);
        username.setText(commentItem.username);
        text.setText(commentItem.text);
        appname.setText(commentItem.appname);
        appname.setTextColor(colorResId);
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(activity, view, commentItem.id.intValue(), commentItem.username, false);
            }
        });
        if (commentItem.votes != null && commentItem.votes.intValue() != 0) {
            votes.setVisibility(View.VISIBLE);
            votes.setText(AptoideUtils.StringUtils.getFormattedString(context, R.string.votes, commentItem.votes));
        }

        Resources r = itemView.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, commentItem.commentLevel * 30, r.getDisplayMetrics());
        LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.setMargins(px, 0, 0, 0);
        cardLayout.setLayoutParams(relativeParams);
        cardLayout.requestLayout();

        Glide.with(context).load(commentItem.useravatar).transform(new CircleTransform(context)).into(useravatar);
    }

    @Override
    protected void bindViews(View itemView) {
        useravatar = (ImageView )itemView.findViewById(R.id.useravatar);
        username = (TextView )itemView.findViewById(R.id.username);
        timestamp = (TextView )itemView.findViewById(R.id.timestamp);
        replyComment = (TextView )itemView.findViewById(R.id.reply_comment);
        text = (TextView )itemView.findViewById(R.id.comment_text);
        appname = (TextView )itemView.findViewById(R.id.app_name);
        overflow = (ImageButton )itemView.findViewById(R.id.overflow_vote_menu);
        votes = (TextView )itemView.findViewById(R.id.votes);
        verticalSeparator = (View )itemView.findViewById(R.id.vertical_separator);
        cardLayout = (RelativeLayout )itemView.findViewById(R.id.comments_card_layout);
    }

    public void showPopup(final Activity activity, View view, final int commentId, String author, boolean showReply) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!AptoideUtils.AccountUtils.isLoggedInOrAsk(activity)) return false;
                if (!(activity instanceof AddCommentVoteCallback)) {
                    throw new IllegalStateException("activity is not an instanceof AddCommentVoteCallback");
                }

                final AddCommentVoteCallback voteCallback = (AddCommentVoteCallback) activity;
                int i = item.getItemId();
                if (i == R.id.menu_vote_up) {
                    voteCallback.voteComment(commentId, AddApkCommentVoteRequest.CommentVote.up);
                    return true;
                } else if (i == R.id.menu_vote_down) {
                    voteCallback.voteComment(commentId, AddApkCommentVoteRequest.CommentVote.down);
                    return true;
                }
                return false;
            }
        });
        popup.inflate(R.menu.menu_comments);
        popup.show();
        if (!showReply) {
            popup.getMenu().findItem(R.id.menu_reply).setVisible(false);
        }
    }
}