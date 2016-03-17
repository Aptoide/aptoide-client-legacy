package com.aptoide.amethyst.webservices;

import com.aptoide.models.displayables.SearchApk;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmateus on 12/06/15.
 */

public class SearchJson {

    private final int bucketSize;

    public SearchJson(int bucketSize) {
        this.bucketSize = bucketSize;
    }

    public String status;
    public Results results;

    public static class Results {

        public List<String> didyoumean = new ArrayList<>();
        public List<Apk> apks = new ArrayList<>();
        @JsonProperty("u_apks")
        public List<Apk> uApks = new ArrayList<>();
    }

    public SearchJson() {
        bucketSize = 0;
    }

    public static class Apk {

        public String name;
        public String repo;
        @JsonProperty("package")
        public String packageName;
        public String vername;
        public Integer vercode;
        public String md5sum;
        public String timestamp;
        public String age;
        public Integer malrank;
        public String icon;
        @JsonProperty("has_other_versions")
        public boolean hasOtherVersions;
        @JsonProperty("has_other_repos")
        public boolean hasOtherRepos;
        @JsonProperty("icon_hd")
        public String iconHd;
        public Integer stars;
        public String signature;
        public String repo_theme;
        public long downloads;


        public SearchApk toSearchApk() {

            SearchApk searchApk = new SearchApk(0); // TODO: 02/10/15 BUCKETSIZE

            searchApk.age = age;
            searchApk.hasOtherRepos = hasOtherRepos;
            searchApk.hasOtherVersions = hasOtherVersions;
            searchApk.md5sum = md5sum;
            searchApk.malrank = malrank;
            searchApk.icon = icon;
            searchApk.iconHd = iconHd;
            searchApk.name = name;
            searchApk.stars = stars;
            searchApk.vercode = vercode;
            searchApk.vername = vername;
            searchApk.repo = repo;
            searchApk.signature = signature;
            searchApk.packageName = packageName;
            searchApk.timestamp = timestamp;
            searchApk.repo_theme = repo_theme;
            searchApk.downloads = downloads;

            return searchApk;
        }
    }
}