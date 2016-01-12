package com.aptoide.dataprovider.webservices.models;

import android.content.pm.ApplicationInfo;

/**
 * Created by rmateus on 15/06/15.
 */


public class UpdatesResponse {

    public BulkResponse.Info info;
    public BulkResponse.Ticket ticket;
    public BulkResponse.Data<UpdateApk> data;

    public static class UpdateApk extends BulkResponse.ListApps.Apk  {
        public ApplicationInfo info;
        public Apk apk;
        public int vercode;

        public static class Apk {
            public String path;
            public String path_alt;
            public Number filesize;
        }
    }


}
