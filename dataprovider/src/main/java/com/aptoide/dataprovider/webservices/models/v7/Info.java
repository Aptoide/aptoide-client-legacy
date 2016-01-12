package com.aptoide.dataprovider.webservices.models.v7;

/**
 * Created by hsousa on 17/09/15.
 */
public class Info {
    // OK, QUEUED, FAIL
    public String status;
    public Time time;

    public static class Time {
        public Number seconds;
        public String human;
    }

}
