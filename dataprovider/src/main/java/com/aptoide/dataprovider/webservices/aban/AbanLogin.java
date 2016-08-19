package com.aptoide.dataprovider.webservices.aban;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by diogoloureiro on 18/08/16.
 */
public class AbanLogin extends RetrofitSpiceRequest<AbanLogin.Respose, IAbanServices> {

    private final String phoneNr;
    private final String password;

    public AbanLogin(String phoneNr, String password) {
        super(AbanLogin.Respose.class, IAbanServices.class);
        this.phoneNr = phoneNr;
        this.password = password;
    }

    @Override
    public Respose loadDataFromNetwork() throws Exception {
        Request loginBody = new Request(phoneNr,password);
        return getService().verifyLogin(loginBody);
    }

    public static class Respose {
        String username;
        String sdp_token;

        public String getUsername() {
            return username;
        }

        public String getSdp_token(){
            return sdp_token;
        }
    }

    public static class Request {
        String username;
        String password;

        public Request(String username, String password){
            this.username=username;
            this.password=password;
        }
    }
}
