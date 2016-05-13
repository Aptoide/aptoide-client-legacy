/*******************************************************************************
 * Copyright (c) 2015 hsousa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;

/**
 * This Activity shows more Editors Choice
 */
public class MoreListViewItemsBrickActivity extends MoreActivity {


    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = MoreListViewItemsBrickFragment.newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    public static class MoreListViewItemsBrickFragment extends MoreListViewItemsActivity.MoreListViewItemsFragment {

        public static Fragment newInstance() {
            return new MoreListViewItemsBrickFragment();
        }


        @Override
        protected String getBaseContext() {
            return "GetMoreListAppsBrick";
        }

        @Override
        public void setLayoutManager(RecyclerView recyclerView) {
            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),AptoideUtils.UI.getEditorChoiceBucketSize()));
        }

        @Override
        protected String getLayoutMode() {
            return Constants.LAYOUT_BRICK;
        }

    }
}