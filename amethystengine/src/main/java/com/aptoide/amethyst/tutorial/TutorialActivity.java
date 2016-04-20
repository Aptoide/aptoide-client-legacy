package com.aptoide.amethyst.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.flurry.android.FlurryAgent;

/**
 * Created by trinkes on 3/28/16.
 */
public class TutorialActivity extends AptoideBaseActivity {

    private ViewPager mViewPager;
    private Button back;
    private Button next;
    private Toolbar mToolbar;

    @Override

    protected String getScreenName() {
        return "Tutorial";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_tutorial);

        bindViews();
        setListeners();
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager.setAdapter(new TutorialViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateButtonsStates(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setListeners() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = mViewPager.getCurrentItem() + 1;
                if (nextItem >= mViewPager.getAdapter().getCount()) {
                    finish();
                }
                mViewPager.setCurrentItem(nextItem, true);
                updateButtonsStates(nextItem);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                updateButtonsStates(mViewPager.getCurrentItem());
            }
        });
    }

    private void updateButtonsStates(int currentItem) {
        if (currentItem > 0) {
            back.setVisibility(View.VISIBLE);
        } else {
            back.setVisibility(View.GONE);
        }
    }

    private void bindViews() {
        mViewPager = (ViewPager) findViewById(R.id.tutorialViewPager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);
    }

    @Override
    public void finish() {

        FlurryAgent.logEvent("Wizard_Added_Apps_As_Default_Store");
        Intent data = new Intent();
        data.putExtra("addDefaultRepo", true);
        setResult(RESULT_OK, data);
        Log.d("Tutorial-addDefaultRepo", "true");

        AptoideUtils.AppUtils.checkPermissions(this);
        Analytics.Tutorial.finishedTutorial(mViewPager.getCurrentItem()+1);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wizard, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_skip) {
            FlurryAgent.logEvent("Wizard_Skipped_Initial_Wizard");

//            if (currentFragment == lastFragment) {
//                getFragmentsActions();
//                runFragmentsActions();
//            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
