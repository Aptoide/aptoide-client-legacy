package org.onepf.oms;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.concurrent.CountDownLatch;

import com.aptoide.amethyst.openiab.webservices.IabAvailableRequest;
import com.aptoide.amethyst.openiab.webservices.json.IabAvailableJson;

/**
 * Created by j-pac on 12-02-2014.
 */

public class StoreBinder extends IOpenAppstore.Stub {

    private static final String BILLING_BIND_INTENT = "org.onepf.oms.billing.BIND";
    private final SpiceManager manager;

    private Context context;

    public StoreBinder(Context context, SpiceManager manager) {
        this.context = context;
        this.manager = manager;
    }

    @Override
    public String getAppstoreName() throws RemoteException {
        Log.d("AptoideStore", "[getAppstoreName]");
        return "cm.aptoide.pt";
    }

    @Override
    public boolean isPackageInstaller(String packageName) throws RemoteException {
        Log.d("AptoideStoreS", "[isPackageInstaller] "+ context);

        String packageInstaller = context.getPackageManager().getInstallerPackageName(packageName);

        return packageInstaller == null || packageInstaller.equals(context.getPackageName());

    }

    @Override
    public boolean isBillingAvailable(String packageName) throws RemoteException {
        Log.d("AptoideStore", "[isBillingAvailable]");

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.FROYO){
            return false;
        }

        try {

                final CountDownLatch latch = new CountDownLatch(1);
                IabAvailableRequest request = new IabAvailableRequest();
                request.setApiVersion(Integer.toString(3));

                request.setPackageName(packageName);
                final boolean[] result = { false };
                manager.execute(request, packageName + "-iabavalaible", DurationInMillis.ONE_MINUTE,new RequestListener<IabAvailableJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        latch.countDown();
                    }

                    @Override
                    public void onRequestSuccess(IabAvailableJson response) {
                        if("OK".equals(response.getStatus())) {
                            if("OK".equals(response.getResponse().getIabavailable())) {
                                Log.d("AptoideStore", "billing is available");
                                result[0] = true;
                            }
                        }
                        latch.countDown();
                    }
                });
                latch.await();


                return result[0];



        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int getPackageVersion(String packageName) throws RemoteException {
        Log.d("AptoideStore", "[getPackageVersion]");
        return Integer.MAX_VALUE; //Undefined
    }

    @Override
    public Intent getBillingServiceIntent() throws RemoteException {
        Log.d("AptoideStore", "[getBillingServiceIntent]");
        Intent intent = new Intent(context, BillingService.class);
        intent.setAction(BILLING_BIND_INTENT);

        return intent;
    }

    @Override
    public Intent getProductPageIntent(String packageName) throws RemoteException {
        return null;
    }

    @Override
    public Intent getRateItPageIntent(String packageName) throws RemoteException {
        return null;
    }

    @Override
    public Intent getSameDeveloperPageIntent(String packageName) throws RemoteException {
        return null;
    }

    @Override
    public boolean areOutsideLinksAllowed() throws RemoteException {
        return false;
    }
}
