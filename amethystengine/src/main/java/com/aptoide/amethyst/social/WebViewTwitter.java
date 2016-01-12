/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.social;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;

public class WebViewTwitter extends AptoideBaseActivity {
	private String url;
	private WebView TwitterWebView;
	private TextView waitingText;
	private ProgressBar waitingBar;


    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_social);
        url = "http://mobile.twitter.com/aptoide";

        try {
        	waitingText = (TextView) findViewById(R.id.waiting_text);
        	waitingBar = (ProgressBar) findViewById(R.id.waiting_bar);
        	TwitterWebView = (WebView) findViewById(R.id.webview);

            TwitterWebView.setWebViewClient(new WebViewClient() {
    			public boolean shouldOverrideUrlLoading (WebView view, String url) {
    				view.loadUrl(url);
    				return true;
    			}

    		});
            TwitterWebView.getSettings().setJavaScriptEnabled(true);
//            TwitterWebView.getSettings().setDomStorageEnabled(true);
            TwitterWebView.getSettings().setSavePassword(false);
            TwitterWebView.getSettings().setSaveFormData(false);
            TwitterWebView.getSettings().setSupportZoom(false);

            TwitterWebView.setWebChromeClient(new WebChromeClient() {
    			public void onProgressChanged(WebView view, int progress)
    			{
                    setProgress(progress * 100);

                    if(progress == 100){
                    	waitingText.setVisibility(View.GONE);
                    	waitingBar.setVisibility(View.GONE);
                    }
    			}
    		});

            TwitterWebView.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setCollapsible(false);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getScreenName() {
        return "Web View Twitter";
    }

}

