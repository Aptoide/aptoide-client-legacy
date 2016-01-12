package cm.aptoide.pt.viewholders.store;

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

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.models.CommentItem;
import com.aptoide.models.Displayable;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;
import cm.aptoide.pt.callbacks.AddCommentVoteCallback;
import cm.aptoide.pt.ui.widget.CircleTransform;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 21/07/15.
 */
public class CommentViewHolder extends BaseViewHolder {

    private final Activity activity;
    private final int colorResId;
    @Bind(R.id.useravatar)                      public ImageView useravatar;
    @Bind(R.id.username)                        public TextView username;
    @Bind(R.id.timestamp)                       public TextView timestamp;
    @Bind(R.id.reply_comment)                   public TextView replyComment;
    @Bind(R.id.comment_text)                    public TextView text;
    @Bind(R.id.app_name)                        public TextView appname;
    @Bind(R.id.overflow_vote_menu)              public ImageButton overflow;
    @Bind(R.id.votes)                           public TextView votes;
    @Bind(R.id.vertical_separator)              public View verticalSeparator;
    @Bind(R.id.comments_card_layout)            public RelativeLayout cardLayout;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public CommentViewHolder(View itemView, int viewType, Activity activity, int colorResId) {
        super(itemView, viewType);
        this.activity = activity;
        this.colorResId = colorResId;
        ButterKnife.bind(this, itemView);
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