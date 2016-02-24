package com.aptoide.amethyst.adapter.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.webservices.json.TimelineListAPKsJson;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;



/**
 * Created by rmateus on 25-09-2014.
 */

public class EndlessWrapperAdapter extends EndlessAdapter {
    private final Callback callback;
    TimelineAdapter tla;

    public interface Callback{
        void runRequest();
    }

    public EndlessWrapperAdapter(TimelineAdapter tla,Callback callback, Context context) {
        super(context, tla, 0);
        this.tla=tla;
        this.callback = callback;
    }

    @Override
    protected View getPendingView(ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.row_progress_bar, parent, false);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        callback.runRequest();
        return true;
    }


    @Override
    protected void appendCachedData() {}

    public void addNativeAd(NativeAd ad,Context context,ArrayList<TimelineListAPKsJson.UserApk> list) {

        tla.addNativeAd(ad,context,list);
    }


}
