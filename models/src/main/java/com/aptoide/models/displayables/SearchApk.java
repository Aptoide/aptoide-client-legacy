package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchApk extends Displayable {

    public boolean fromSubscribedStore = false;
    public int position;
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
    public Number stars;
    public String signature;
    public String repo_theme;
    public long downloads;

    public SearchApk(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    @Override
    public String toString() {
        return "SearchApk{" +
                "name='" + name + '\'' +
                ", repo='" + repo + '\'' +
                ", packageName='" + packageName + '\'' +
                ", vername='" + vername + '\'' +
                ", vercode=" + vercode +
                ", md5sum='" + md5sum + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", age='" + age + '\'' +
                ", malrank=" + malrank +
                ", icon='" + icon + '\'' +
                ", hasOtherVersions=" + hasOtherVersions +
                ", hasOtherRepos=" + hasOtherRepos +
                ", iconHd='" + iconHd + '\'' +
                ", stars=" + stars +
                ", signature='" + signature + '\'' +
                '}';
    }
}