package com.aptoide.dataprovider.webservices.models.v7;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 28/10/15.
 */
public class GetApp {

    public Info info;
    public Nodes nodes;

    public static class Nodes {

        /**
         * Defined on its own file
         */
        public GetAppMeta meta;
        public ListAppsVersions versions;

        public static class ListAppsVersions {

            public Info info;

            /**
             * The other versions list always returns one item (itself), as per the web team.
             */
            public List<ViewItem> list = new ArrayList<>();
        }
    }

}
