package com.aptoide.amethyst.adapter.store;

import android.app.Activity;
import android.content.res.Resources;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.dialogs.ReplyCommentDialog;
import com.aptoide.amethyst.ui.callbacks.AddCommentCallback;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.models.displayables.CommentItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.NoCommentPlaceHolderRow;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.callbacks.AddCommentVoteCallback;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.ProgressBarRowViewHolder;
import com.aptoide.amethyst.viewholders.main.CommentHeaderViewHolder;
import com.aptoide.amethyst.viewholders.main.EmptyViewHolder;
import com.aptoide.amethyst.viewholders.store.CommentViewHolder;

/**
 * Created by hsousa on 21/07/15.
 */
public class CommentsStoreAdapter extends BaseAdapter implements SpannableRecyclerAdapter {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private final int colorResId;
    private final Activity activity;
    private final boolean appView;
    private AddCommentCallback addCommentCallback;

    public CommentsStoreAdapter(List<Displayable> displayableList, Activity activity, int colorResId, boolean appView, AddCommentCallback addCommentCallback) {
        super(displayableList);
        this.activity = activity;
        this.colorResId = colorResId;
        this.appView = appView;
        this.addCommentCallback = addCommentCallback;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);

        BaseViewHolder holder;
        if (viewType == R.layout.row_appview_comments_header) {
            holder = new CommentHeaderViewHolder(view, viewType);
        } else if (viewType == R.layout.comment_row) {
            holder = new CommentViewHolder(view, viewType, activity, colorResId);
        } else if (viewType == R.layout.row_appview_create_comment) {
            holder = new EmptyViewHolder(view, viewType);
        } else if (viewType == R.layout.row_progress_bar) {
            return new ProgressBarRowViewHolder(view, viewType);
        } else {
            throw new IllegalStateException("CommentsStoreAdapter with unknown viewtype");
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {

        if (viewHolder.viewType == R.layout.row_appview_comments_header) {
            HeaderRow row = (HeaderRow) displayableList.get(position);
            CommentHeaderViewHolder holder = (CommentHeaderViewHolder) viewHolder;
            holder.title.setText(row.getLabel());
            holder.writeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AptoideUtils.AccountUtils.isLoggedInOrAsk(activity)) {

                        ReplyCommentDialog replyDialog = AptoideDialog.replyCommentDialog(-1, null, addCommentCallback);
                        replyDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "replyCommentDialog");

//                                FlurryAgent.logEvent("App_View_Clicked_On_Reply_Comment");
                    }
                }
            });
        } else if (viewHolder.viewType == R.layout.comment_row) {
            CommentViewHolder item = (CommentViewHolder) viewHolder;
            final CommentItem commentItem = (CommentItem) displayableList.get(position);
            if (appView) {
                item.appname.setVisibility(View.GONE);
                item.replyComment.setVisibility(View.VISIBLE);
                item.verticalSeparator.setVisibility(View.VISIBLE);
                item.replyComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AptoideUtils.AccountUtils.isLoggedInOrAsk(activity)) {
                            ReplyCommentDialog replyDialog = AptoideDialog.replyCommentDialog(commentItem.id.intValue(), commentItem.username,
                                    addCommentCallback);
                            replyDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "replyCommentDialog");
                        }
                    }
                });
            } else {
                item.replyComment.setVisibility(View.GONE);
                item.appname.setText(commentItem.appname);
            }
            String dateString = "";
            final Context context = viewHolder.itemView.getContext();
            try {
                dateString = AptoideUtils.DateTimeUtils.getInstance(context).getTimeDiffString(context, dateFormatter.parse(commentItem.timestamp).getTime());
            } catch (ParseException e) {
                Logger.printException(e);
            }
            item.timestamp.setText(dateString);
            item.username.setText(commentItem.username);
            item.text.setText(commentItem.text);
            item.appname.setTextColor(colorResId);
            item.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(activity, view, commentItem.id.intValue(), commentItem.username, false);
                }
            });
            if (commentItem.votes != null && commentItem.votes.intValue() != 0) {
                item.votes.setVisibility(View.VISIBLE);
                item.votes.setText(AptoideUtils.StringUtils.getFormattedString(context, R.string.votes, commentItem.votes));
            }
            viewHolder.itemView.setOnClickListener(new CommentItemOnClickListener(commentItem));
            Glide.with(context).load(commentItem.useravatar).transform(new CircleTransform(context)).into(item.useravatar);
            if (position == displayableList.size() - 1) {
                item.verticalSeparator.setVisibility(View.GONE);
            }
            Resources r = item.itemView.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, commentItem.commentLevel * 30, r.getDisplayMetrics());
            LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            relativeParams.setMargins(px, 0, 0, 0);
            item.cardLayout.setLayoutParams(relativeParams);
            item.cardLayout.requestLayout();
        }
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

                int i = item.getItemId();
                if (i == R.id.menu_vote_up) {
                    ((AddCommentVoteCallback) activity).voteComment(commentId, AddApkCommentVoteRequest.CommentVote.up);
//                    FlurryAgent.logEvent("App_View_Voted_Up");
                    return true;
                } else if (i == R.id.menu_vote_down) {
                    ((AddCommentVoteCallback) activity).voteComment(commentId, AddApkCommentVoteRequest.CommentVote.down);
//                    FlurryAgent.logEvent("App_View_Voted_Down");
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

    @Override
    public int getItemViewType(int position) {

        if (displayableList.get(position) instanceof HeaderRow) {
            return R.layout.row_appview_comments_header;
        } else if (displayableList.get(position) instanceof CommentItem) {
            return R.layout.comment_row;
        } else if (displayableList.get(position) instanceof NoCommentPlaceHolderRow) {
            return R.layout.row_appview_create_comment;
        } else if (displayableList.get(position) instanceof ProgressBarRow) {
            return R.layout.row_progress_bar;
        } else {
            throw new IllegalStateException("InvalidType");
        }
    }


    @Override
    public int getSpanSize(int position) {
        return displayableList.get(position).getSpanSize();
    }

    @Override
    public int getItemCount() {
        return displayableList.size();
    }

}
