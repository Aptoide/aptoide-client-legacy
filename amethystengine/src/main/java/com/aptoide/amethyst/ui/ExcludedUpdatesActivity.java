package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;

import com.aptoide.amethyst.fragments.ExcludedUpdatesFragment;

public class ExcludedUpdatesActivity extends AptoideBaseActivity {

    Toolbar mToolbar;
    FrameLayout content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        mToolbar.setCollapsible(false);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        // AppBarLayout$ScrollingViewBehavior issue
//        content.setVisibility(View.GONE);
//        contentNoBehaviour.setVisibility(View.VISIBLE);

        // Apenas se nao for uma orientation change
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, ExcludedUpdatesFragment.newInstance(), "").commit();
        }




    }

    protected void bindViews() {
        mToolbar = (Toolbar )findViewById(R.id.toolbar);
        content = (FrameLayout )findViewById(R.id.content);
    }

    protected int getContentView() {
        return R.layout.activity_fragment_layout;
    }

    @Override
    protected String getScreenName() {
        return "Excluded Updates";
    }
}