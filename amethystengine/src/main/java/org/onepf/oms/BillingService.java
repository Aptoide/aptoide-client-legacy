package org.onepf.oms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by j-pac on 12-02-2014.
 */
public class BillingService extends Service {
    SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);
    private final IOpenInAppBillingService.Stub wBinder = new BillingBinder(this, manager);

    @Override
    public IBinder onBind(Intent intent) {
        return wBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(!manager.isStarted())manager.start(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(manager.isStarted())manager.shouldStop();
    }
}
