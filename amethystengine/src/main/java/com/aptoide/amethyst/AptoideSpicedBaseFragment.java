package com.aptoide.amethyst;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by rmateus on 01/06/15.
 */
public class AptoideSpicedBaseFragment extends Fragment {

    protected SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);

        @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AptoideUtils.CrashlyticsUtils.addScreenToHistory(getClass().getSimpleName());
        }
    }

    @Override
    public void onAttach(Context context) {
        spiceManager.start(context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        spiceManager.shouldStop();
        super.onDetach();
    }
}
