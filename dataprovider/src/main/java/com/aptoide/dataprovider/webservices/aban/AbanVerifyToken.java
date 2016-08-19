package com.aptoide.dataprovider.webservices.aban;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by diogoloureiro on 18/08/16.
 */
public class AbanVerifyToken extends RetrofitSpiceRequest<AbanVerifyToken.Respose, IAbanServices> {

    private final String phoneNr;
    private final String token;

    public AbanVerifyToken(String phoneNr, String token) {
        super(AbanVerifyToken.Respose.class, IAbanServices.class);
        this.phoneNr = phoneNr;
        this.token = token;
    }

    @Override
    public Respose loadDataFromNetwork() throws Exception {
        return getService().verifyToken(phoneNr,token);
    }

    public static class Respose {
        String status;

        public String getStatus() {
            return status;
        }
    }
}