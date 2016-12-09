package com.aptoide.amethyst.adapter;

import com.aptoide.amethyst.fragments.store.CategoryFragment;
import com.aptoide.amethyst.fragments.store.HomeStoreFragment;
import com.aptoide.amethyst.fragments.store.LatestCommentsFragment;
import com.aptoide.amethyst.fragments.store.LatestReviewsFragment;
import com.aptoide.amethyst.utils.Translator;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreTabs.Tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_APK_COMMENTS_TAB;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_REVIEWS_TAB;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_STORE_TAB;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_STORE_WIDGETS_TAB;

/**
 * Created by rmateus on 23/06/15.
 */
public class StorePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Tab> tabs;
    private Bundle args;

    public StorePagerAdapter(FragmentManager fm, Bundle args, List<Tab> tabs) {
        super(fm);
        this.args = args;
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        Tab tab = tabs.get(position);

        switch (tab.event.name) {
            case GET_STORE_TAB:
                return HomeStoreFragment.newInstance(args);
            case GET_STORE_WIDGETS_TAB:
                return CategoryFragment.newInstance(args, tab.event.action);
            case GET_APK_COMMENTS_TAB:
                return LatestCommentsFragment.newInstance(args);
            case GET_REVIEWS_TAB:
                return LatestReviewsFragment.newInstance(args);
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Tab tab = tabs.get(position);
        return Translator.translate(tab.label);
    }
}
