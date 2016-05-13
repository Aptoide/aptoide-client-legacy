package com.aptoide.amethyst.openiab.webservices.json;


/**
 * Created by j-pac on 19-02-2014.
 */
public class IabAvailableJson  {


    public String status;


    public Response response;

    public String getStatus() {
        return status;
    }

    public Response getResponse() { return response; }

    public static class Response {

        public String iabavailable;

        public String getIabavailable() {
            return iabavailable;
        }
    }
}
