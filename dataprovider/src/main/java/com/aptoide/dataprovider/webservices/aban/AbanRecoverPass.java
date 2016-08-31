package com.aptoide.dataprovider.webservices.aban;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by diogoloureiro on 18/08/16.
 */
public class AbanRecoverPass extends RetrofitSpiceRequest<AbanRecoverPass.Respose, IAbanServices> {

    private final String phoneNr;
    private final String msg = "salam";

    public AbanRecoverPass(String phoneNr) {
        super(AbanRecoverPass.Respose.class, IAbanServices.class);
        this.phoneNr = phoneNr;
    }

    @Override
    public Respose loadDataFromNetwork() throws Exception {
        return getService().recoverPassword(phoneNr,msg);
    }

    public static class Respose {
        String status;
    }
}