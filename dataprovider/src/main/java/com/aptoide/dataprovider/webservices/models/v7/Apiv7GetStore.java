package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * http://ws2.aptoide.com/api/7/getStore/info/1
 * Created by hsousa on 15/09/15.
 */
public class Apiv7GetStore extends Apiv7 {

    public String context;
    public String nview;
    public Long store_id;
    public String store_name;
    public String store_pass_sha1;
    public String store_user;
    public boolean mature;

    /**
     * Fields for getApp webservice. This could (and should) be refactored, but there is no time
     */
    public Long app_id;
    @JsonProperty("package_name")
    public String packageName;


    @JsonProperty("widgets_args")
    private Map<String, ApiParam> widgetArgs;

    @JsonProperty("nodes")
    private Map<String, NodeParams> nodeArgs;

    public void setApiParams(ApiParam value) {
        if (widgetArgs == null) {
            widgetArgs = new HashMap<>();
        }
        widgetArgs.put(value.getDatasetName(), value);
    }

    public void setNodeParams(NodeParams value) {
        if (nodeArgs == null) {
            nodeArgs = new HashMap<>();
        }
        nodeArgs.put(value.getDatasetName(), value);
    }

    public static class WidgetParams implements ApiParam {

        @JsonIgnore
        private final String category;
        public int grid_row_size;

        public WidgetParams(String category) {
            this.category = category;
        }

        @Override
        public String getDatasetName() {
            return category;
        }
    }


    /**
     * {
     *   "nodes":{
     *      "meta":{
     *          "store_name":"<somestore>",
     *          "app_id":"<appid>"
     *      },
     *      "versions":{
     *          "package_name":"<packagename>"
     *     }
     *   }
     * }
     *
     */
    public static class NodeParams implements ApiParam {

        @JsonIgnore
        private final String category;
        public String store_name;
        public String app_id; // so it can be nullable
        public String package_name;
        public String apk_md5sum;

        public NodeParams(String category) {
            this.category = category;
        }

        @Override
        public String getDatasetName() {
            return category;
        }
    }

    public interface ApiParam {
        @JsonIgnore
        String getDatasetName();
    }

}
