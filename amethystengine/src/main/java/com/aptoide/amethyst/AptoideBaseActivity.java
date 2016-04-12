package com.aptoide.amethyst;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.utils.LifeCycleMonitor;

import lombok.Getter;

/**
 * Created by rmateus on 01/06/15.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity implements AptoideUtils.AppNavigationUtils.AptoideNavigationInterface {

    @Getter private boolean _resumed = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AptoideUtils.AppNavigationUtils.onBackPressed(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);

        if (getIntent().hasExtra(AptoideUtils.AppNavigationUtils.STATE_KEY)) {
            Bundle bundle = (Bundle) getIntent().getExtras().get(AptoideUtils.AppNavigationUtils.STATE_KEY);
            if (bundle != null) {
                savedInstanceState = bundle;
            }
        }

        super.onCreate(savedInstanceState);
        Logger.d("debug", "onCreate: " + getClass().getSimpleName());
        AptoideUtils.AppNavigationUtils.onCreate(getIntent(), this);
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.CREATE);
        Analytics.Lifecycle.Activity.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        Analytics.Lifecycle.Activity.onDestroy(this);
        super.onDestroy();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.DESTROY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AptoideUtils.AppNavigationUtils.onSaveInstanceState(getIntent(), outState);
    }

    @Override
    protected void onStart() {
        AptoideUtils.AppNavigationUtils.onStart(getIntent(), this);
        super.onStart();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.START);
        Analytics.Lifecycle.Activity.onStart(this);
    }

    @Override
    protected void onStop() {
        Analytics.Lifecycle.Activity.onStop(this);
        super.onStop();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.STOP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.RESUME);
        _resumed = true;
        Analytics.Lifecycle.Activity.onResume(this, getScreenName());
        AptoideUtils.CrashlyticsUtils.addScreenToHistory(getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Analytics.Lifecycle.Activity.onPause(this);
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.PAUSE);
        _resumed = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == android.R.id.home || i == R.id.home) {
            AptoideUtils.AppNavigationUtils.startParentActivity(this ,this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
     */
    protected abstract String getScreenName();

    @Override
    public String getMetaData(String key) {
        try {
            ActivityInfo aiActivity = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
            if (aiActivity.metaData != null) {
                return aiActivity.metaData.getString(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return null;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
