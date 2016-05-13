/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;

import java.util.ArrayList;


public class ScreenshotsViewer extends AptoideBaseActivity {

    private static final String POSITION = "position";
    private String[] images = new String[0];
    private int currentItem;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // do nothing
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.page_screenshots_viewer);

        if (savedInstanceState == null) {
            currentItem = getIntent().getIntExtra(POSITION, 0);
        } else {
            currentItem = savedInstanceState.getInt(POSITION, 0);
        }

        final ViewPager screenshots = (ViewPager) findViewById(R.id.screenShotsPager);

        ArrayList<String> uri = getIntent().getStringArrayListExtra("url");
        if (uri != null) {
            images = uri.toArray(images);
        }
        if (images != null && images.length > 0) {
            screenshots.setAdapter(new ViewPagerAdapterScreenshots(uri));
            screenshots.setCurrentItem(currentItem);
        }

        View btnCloseViewer = findViewById(R.id.btn_close_screenshots_window);
        btnCloseViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, currentItem);
    }

    @Override
    protected String getScreenName() {
        return "Screenshots Viewer";
    }
}