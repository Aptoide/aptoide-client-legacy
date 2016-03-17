package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.Button;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 23/06/15.
 */
public class AddStoreViewHolder extends BaseViewHolder {

    public Button more;

    public AddStoreViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        more = (Button) itemView.findViewById(R.id.more);
    }
}

