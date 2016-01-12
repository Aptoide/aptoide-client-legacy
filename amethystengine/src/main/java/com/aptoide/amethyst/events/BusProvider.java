package com.aptoide.amethyst.events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 08-11-2013
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class BusProvider extends Bus implements Serializable{

    private static final BusProvider BUS = new BusProvider();

    public static BusProvider getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

    public void nativePost(final Object event){
        super.post(event);
    }

}

