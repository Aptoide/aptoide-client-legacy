/*******************************************************************************
 * Copyright (c) 2015 hsousa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.FeedBackActivity;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.persistence.DurationInMillis;

import cm.aptoide.pt.R;

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
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        }

        @Override
        protected String getLayoutMode() {
            return Constants.LAYOUT_BRICK;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_SendFeedBack) {
            FeedBackActivity.screenshot(this);
            startActivity(new Intent(this, FeedBackActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_feedback, menu);

        return super.onCreateOptionsMenu(menu);
    }
}