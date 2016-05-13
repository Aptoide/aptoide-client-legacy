package com.aptoide.amethyst.subscription;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.UrlQuerySanitizer;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Date;

/**
 * Created by ruicardoso on 07-03-2016.
 */
public class SubscriptionActivity extends AptoideBaseActivity {
    private static final String TAG = SubscriptionActivity.class.getSimpleName();

    public static final int SUBSCRIPTION_REQ_CODE = 102;

    /**
     * When successfully subscribed, will be redirecto to RETURN_URL
     */
    private static final String RETURN_URL = "http://www.aptoide.com";

    /**
     * URL parameter containing access token
     */
    private static final String PARAM_ACCESS_TOKEN = "accessToken";
    
    private boolean subscribedSuccessfully = false;
    private boolean showSkipToolbar = false;
    protected SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_subscription);

        showSkipToolbar = getIntent() != null && getIntent().getBooleanExtra(Subscription.SHOW_SKIP, false);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_subscription);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){

            //Only shows skip on toolbar on skippable sections (i.e.: When opening store)
            if(!showSkipToolbar){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setupWebView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    /**
     * Setup subscription web view
     */
    private void setupWebView() {
        WebView subscribeWebview = (WebView) findViewById(R.id.subscribe_webview);
        subscribeWebview.setWebViewClient(new SubscriptionWebViewClient());
        subscribeWebview.getSettings().setJavaScriptEnabled(true);
        subscribeWebview.getSettings().setSavePassword(false);
        subscribeWebview.getSettings().setSaveFormData(false);
        subscribeWebview.getSettings().setSupportZoom(false);

        //Append return url to subscription
        String url = getString(R.string.subscription_url) + "&returnUrl=" + RETURN_URL;
        subscribeWebview.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribedSuccessfully = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(showSkipToolbar){
            getMenuInflater().inflate(R.menu.menu_wizard, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home || i == R.id.menu_skip) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){
        if(subscribedSuccessfully){
            setResult(RESULT_OK);
        }else{
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

    @Override
    protected String getScreenName() {
        return "Subscription Web View";
    }

    /**
     * Checks if access token is valid using partner webservice. Will leave webview after the request
     * is completed, successfully or not.
     *
     * @param accessToken - Access token to be verified
     */
    private void checkValidToken(final String accessToken){
        Subscription.subscriptionTokenVerificationRequest(spiceManager, accessToken, new RequestListener<SubscriptionStatus>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.i(TAG, "Subscription token error: " + spiceException.getMessage());
                finish();
            }

            @Override
            public void onRequestSuccess(SubscriptionStatus subscriptionStatus) {
                if (subscriptionStatus != null && subscriptionStatus.isSubscribed()) {
                    SharedPreferences.Editor securePreferences = SecurePreferences.getInstance().edit();
                    securePreferences.putString("subscription_access_token", accessToken);
                    securePreferences.apply();

                    SharedPreferences.Editor sPrefEditor = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit();
                    sPrefEditor.putLong("subscription_expiration", (new Date().getTime() / 1000l) + subscriptionStatus.validity * 60);
                    sPrefEditor.apply();

                    subscribedSuccessfully = true;
                    finish();
                } else {
                    finish();
                }
            }
        });
    }

    private class SubscriptionWebViewClient extends WebViewClient {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();

        /**
         * Will override url loading if it contains the access token, stopping unnecessary requests
         * @param view
         * @param url
         * @return true if url contains access token
         */

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith(RETURN_URL)){
                sanitizer.setAllowUnregisteredParamaters(true);
                sanitizer.parseUrl(url);
                String accessToken = sanitizer.getValue(PARAM_ACCESS_TOKEN);
                if(accessToken != null){
                    checkValidToken(accessToken);
                    return true;
                }
            }
            return false;
        }
    }
}
