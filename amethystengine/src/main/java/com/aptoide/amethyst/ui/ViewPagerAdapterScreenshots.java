/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aptoide.amethyst.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ViewPagerAdapterScreenshots extends PagerAdapter {

    private ArrayList<String> urls;

    public ViewPagerAdapterScreenshots(ArrayList<String> urls) {
        this.urls = urls;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        @SuppressLint("InflateParams")
        final View v = LayoutInflater.from(container.getContext()).inflate(R.layout.row_item_screenshots_big, null);
        Glide.with(container.getContext()).load(urls.get(position)).placeholder(getPlaceholder(container.getContext())).into((ImageView) v.findViewById(R.id.screenshot_image_big));
        container.addView(v);
        return v;
    }

    private int getPlaceholder(Context ctx) {
        int id;
        if(ctx.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            id = R.drawable.placeholder_144x240;
        }else{
            id = R.drawable.placeholder_256x160;
        }
        return id;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

    }

}
