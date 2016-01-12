package com.aptoide.amethyst.webservices.json;

import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by asantos on 24-09-2014.
 */
public class TimelineListAPKsJson extends GenericResponseV2 {

    public List<UserApk> usersapks;

    public static class UserApk {

        public boolean animate;
        public Info info;
        public APK apk;

        public static class Info {
            public Number id;
            public String username;
            public String status;
            public boolean owned;
            public Number likes;
            public Number comments;
            public String avatar;
            public String timestamp;
            public String userliked;

            public boolean isStatusActive() {
                return status.equals("active");
            }
        }

        public static class APK {

            public String name;
            public String repo;
            @JsonProperty("package")
            public String packageName;
            public String vername;
            public Number vercode;
            public String md5sum;
            public String timestamp;
            public String age;
            public String icon;
            public String icon_hd;
            public String signature;
        }
    }
}