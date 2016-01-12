package com.aptoide.dataprovider.webservices.models.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * http://ws2.aptoide.com/api/7/getStore/info/1
 * Created by hsousa on 15/09/15.
 */
public class Apiv2 {

    public String access_token = null;
    public String country;
    public String lang;
    public int limit;
    public int offset;
    public String mode;
    public String q = null;
    @JsonProperty("repo")
    public String storeName;
    public Long store_id;
    @JsonProperty("apkid")
    public String packageName;
    @JsonProperty("apkversion")
    public String versionName;

    public Apiv2() {}

}
