package com.aptoide.amethyst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.utils.AptoideUtils;

import lombok.Getter;

/**
 * Created by rmateus on 01/06/15.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity {

    @Getter private boolean _resumed = false;

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
        _resumed = true;
        Analytics.Lifecycle.Activity.onResume(this, getScreenName());
//        AptoideUtils.CrashlyticsUtils.addScreenToHistory(getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        Analytics.Lifecycle.Activity.onPause(this);
        super.onPause();
        _resumed = false;
    }

    /*
     * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
     */
    protected abstract String getScreenName();
}
