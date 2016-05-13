package com.aptoide.amethyst.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aptoide.models.displayables.Displayable;

/**
 * Created by rmateus on 02/06/15.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public int viewType;

    public BaseViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;
        bindViews(itemView);
    }

    public abstract void populateView(Displayable displayable);

    protected abstract void bindViews(View itemView);

}
