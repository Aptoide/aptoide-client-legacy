package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class CommentHeaderViewHolder extends BaseViewHolder {

    public TextView title;
    public LinearLayout writeComment;

    public CommentHeaderViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        title = (TextView) itemView.findViewById(R.id.title);
        writeComment = (LinearLayout) itemView.findViewById(R.id.write_comment);
    }
}