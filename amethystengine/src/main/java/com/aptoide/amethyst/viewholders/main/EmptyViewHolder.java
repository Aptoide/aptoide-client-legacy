package com.aptoide.amethyst.viewholders.main;

import android.view.View;

import com.aptoide.models.displayables.Displayable;

import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 24/09/15.
 */
public class EmptyViewHolder extends BaseViewHolder {

    public EmptyViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {

    }
}
