package com.aptoide.dataprovider.webservices.models.v7;

/**
 * Created by hsousa on 08/09/15.
 */
public class GetStore {
    public Info info;
    public Nodes nodes;

    public static class Ticket {

        public String uid;
        public String status;
        public Number progress;
        public String added;
    }

    public static class Nodes {

        public GetStoreMeta meta;
        public GetStoreTabs tabs;
        public GetStoreWidgets widgets;
    }
}
