package com.aptoide.dataprovider.webservices.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmateus on 15/06/15.
 */
public class UpdatesApi {

    public static final String DEFAULT_CPUID = "NoInfo";

    public List<String> store_names = new ArrayList<>();
    public List<Package> apks_data = new ArrayList<>();
    public List<StoreAuth> stores_auth;

    public String q;
    public boolean mature;
    public String aaid = null;
    public String access_token = null;
    public String cpuid = DEFAULT_CPUID;

    public static class StoreAuth {

        public String store_name;
        public String store_user;
        public String store_pass_sha1;
    }

    public static class Package {
        @JsonProperty("package")
        public String packageName;

        public Number vercode;
        public String signature;
    }
}
