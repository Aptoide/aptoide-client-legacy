package com.aptoide.dataprovider.webservices.aban;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by diogoloureiro on 18/08/16.
 */
public class AbanGeneratePassCode extends RetrofitSpiceRequest<AbanGeneratePassCode.Respose, IAbanServices> {

    private final String phoneNr;

    public AbanGeneratePassCode(String phoneNr) {
        super(AbanGeneratePassCode.Respose.class, IAbanServices.class);
        this.phoneNr = phoneNr;
    }

    @Override
    public Respose loadDataFromNetwork() throws Exception {
        return getService().generatePassCode(phoneNr,13);
    }

    public static class Respose {
        String status;

        public String getStatus() {
            return status;
        }
    }
}
