package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class supports all kind of items coming from the Webservices.
 * It can be an app, or a store item.
 *
 * Created by hsousa on 16/09/15.
 */

public class ViewItem {

    public Number id;
    public String name;
    @JsonProperty("package")
    public String packageName;
    public Number size;
    public String icon;
    public String graphic;
    public String added;
    public String modified;
    public String updated;
    public String uptype;
    /**
     * Class used on an App item
     */
    public GetStoreMeta.Data store;
    public File file;
    public Stats stats;
    public Appearance appearance;

    public String avatar; // used only on Store

    /**
     * Class used on an App item
     */
    public static class File {

        public String vername;
        public Number vercode;
        public String md5sum;
        public Malware malware;

        public static class Malware {
            public static final String TRUSTED = "TRUSTED";
            public static final String WARNING = "WARNING";
            public static final String UNKNOWN = "UNKNOWN";
            public String rank;
        }
    }


    /**
     * Class used on an Store item
     */
    public static class Appearance {

        public String theme;
        public String description;
        public String view;
    }


    /**
     * Class used both on App items and Store items
     * Example on the listAppsVersions:
     *
     * stats": {
     *  "downloads": ​22288,
     *  "rating": {
     *      "avg": ​4.09,
     *      "total": ​11,
     *      "votes": [
     *          {
     *              "value": ​5,
     *              "count": ​8
     *          },
     *          {
     *              "value": ​4,
     *              "count": ​0
     *          },
     *          {
     *              "value": ​3,
     *              "count": ​0
     *          },
     *          {
     *              "value": ​2,
     *              "count": ​2
     *          },
     *          {
     *              "value": ​1,
     *              "count": ​1
     *          }
     *      ]
     *   }
     * }
     *
     */
    public static class Stats {

        public Number apps;         // used on Store items
        public Number subscribers;  // used both on App items and Store items
        public Number downloads;    // used on listApps, Store items and listAppsVersions
        public Rating rating;       // used on App items and listAppsVersions

        public static class Rating {

            public Number avg;
            public Number total;
            public List<Vote> votes = new ArrayList<>();

            public static class Vote {
                public Number value;
                public Number count;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewItem viewItem = (ViewItem) o;

        return id.equals(viewItem.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
