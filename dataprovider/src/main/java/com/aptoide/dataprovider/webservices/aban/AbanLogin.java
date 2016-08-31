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
        return getService().verifyLogin(new Request(phoneNr,password));
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

    public class Request {
        String username;
        String password;

        Request(String username, String password){
            this.username=username;
            this.password=password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
