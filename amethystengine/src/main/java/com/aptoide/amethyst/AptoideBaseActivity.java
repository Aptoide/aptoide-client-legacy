package com.aptoide.amethyst;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aptoide.amethyst.analytics.Analytics;

import butterknife.ButterKnife;

/**
 * Created by rmateus on 01/06/15.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        Analytics.Lifecycle.Activity.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        Analytics.Lifecycle.Activity.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Analytics.Lifecycle.Activity.onStart(this);
    }

    @Override
    protected void onStop() {
        Analytics.Lifecycle.Activity.onStop(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.Lifecycle.Activity.onResume(this, getScreenName());
    }

    @Override
    protected void onPause() {
        Analytics.Lifecycle.Activity.onPause(this);
        super.onPause();
    }

    /*
     * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
     */
    protected abstract String getScreenName();
}
