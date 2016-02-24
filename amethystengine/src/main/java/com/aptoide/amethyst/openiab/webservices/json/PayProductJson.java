package com.aptoide.amethyst.openiab.webservices.json;



/**
 * Created by j-pac on 21-02-2014.
 */
public class PayProductJson {


    public String status;


    public Response response;

    public String getStatus() {
        return status;
    }

    public Response getResponse() {
        return response;
    }

    public static class Response {


        public String orderId;

        public String getOrderId() {
            return orderId;
        }

    }
}
