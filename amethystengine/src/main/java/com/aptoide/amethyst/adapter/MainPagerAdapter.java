package com.aptoide.amethyst.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;

import com.aptoide.amethyst.fragments.main.CommunityFragment;
import com.aptoide.amethyst.fragments.main.DownloadFragment;
import com.aptoide.amethyst.fragments.main.FragmentSocialTimeline;
import com.aptoide.amethyst.fragments.main.HomeFragment;
import com.aptoide.amethyst.fragments.main.StoresFragment;
import com.aptoide.amethyst.fragments.main.UpdatesFragment;

/**
 * Created by rmateus on 02/06/15.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static int NUM_ITEMS = 6;
    private SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return CommunityFragment.newInstance();
            case 2:
                return StoresFragment.newInstance();
            case 3:
                return UpdatesFragment.newInstance();
            case 4:
                return FragmentSocialTimeline.newInstance();
            case 5:
                return DownloadFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Aptoide.getContext().getString(R.string.home_title);
            case 1:
                return Aptoide.getContext().getString(R.string.community);
            case 2:
                return Aptoide.getContext().getString(R.string.stores);
            case 3:
                return Aptoide.getContext().getString(R.string.updates);
            case 4:
                return Aptoide.getContext().getString(R.string.social_timeline);
            case 5:
                return Aptoide.getContext().getString(R.string.downloads);
        }
        return null;
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentSparseArray.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentSparseArray.remove(position);
        super.destroyItem(container, position, object);
    }
}