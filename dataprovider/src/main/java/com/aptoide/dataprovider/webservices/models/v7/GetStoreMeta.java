package com.aptoide.dataprovider.webservices.models.v7;

/**
 * Created by hsousa on 17/09/15.
 */
public class GetStoreMeta {

    public Info info;
    public Data data;

    public static class Data {

        public Number id;
        public String name;
        public String avatar;
        public Appearance appearance;
        public Stats stats;

        public static class Stats {

            public Number apps;
            public Number subscribers;
            public Number downloads;
        }

        public static class Appearance {

            public String theme;
            public String description;
        }
    }
}
