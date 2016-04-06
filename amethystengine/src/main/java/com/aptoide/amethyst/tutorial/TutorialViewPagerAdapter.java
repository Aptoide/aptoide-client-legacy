package com.aptoide.amethyst.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by trinkes on 3/28/16.
 */
public class TutorialViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUMBER_OF_PAGES = 3;

    public TutorialViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = Wizard.NewToAptoide1.newInstace();
                break;
            case 1:
                fragment = Wizard.NewToAptoide2.newInstace();
                break;
            case 2:
                fragment = Wizard.NewToAptoide3.newInstace();
                break;
            default:
                fragment = null;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }
}
