package com.aptoide.dataprovider.webservices.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rmateus on 01/06/15.
 */
public class Api {

    // vercode set by rmateus
    public static final int APTOIDE_VERCODE = 465;

    public Api(int aptoide_vercode, String filters){
        api_global_params = new ApiGlobalParams(aptoide_vercode, filters);
    }

    @JsonProperty
    private ApiParams api_params = new ApiParams();
    @JsonProperty
    private ApiGlobalParams api_global_params;

    public ApiParams getApi_params() {
        return api_params;
    }

    public ApiGlobalParams getApi_global_params() {
        return api_global_params;
    }

    public interface ApiParam {
        @JsonIgnore
        String getApiName();
    }

    public static class ApiParams {
        private Map<String, ApiParam> other = new HashMap<String, ApiParam>();

        @JsonAnyGetter
        public Map<String, ApiParam> getApiParamsMap() {
            return other;
        }

        @JsonAnySetter
        public void set(ApiParam value) {
            other.put(value.getApiName(), value);
        }
    }

    public static class ApiGlobalParams {
        public Number limit;
        public String mature;
        public String lang;
        public String store_name;
        public String store_user;
        public String store_pass_sha1;
        public String country;
        public int aptoide_vercode;
        public String q;


        public ApiGlobalParams(int aptoide_vercode, String filters){
            this.aptoide_vercode = aptoide_vercode;
            this.q = filters;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }


    }

    /**
     * Category params for listApps. Possible examples:<br />
     * "EDITORS_group_hrand" --> EditorsChoice (apps_list:EDITORS_group_hrand)
     * "EDITORS_cat_1" --> applications
     * "EDITORS_cat_2" --> games
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CategoryParam implements DatasetParam {
        @JsonIgnore
        private final String category;
        private Number limit;

        @JsonIgnore
        public CategoryParam(String categoryName) {
            this.category = categoryName;
        }

        @Override
        public String getDatasetName() {
            return category;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public Number getLimit() {
            return limit;
        }
    }

    public static class ListStores implements ApiParam {
        public int limit;
        public Number offset;
        public String order_by;
        public String order_dir;
        public DatasetParams datasets_params = new DatasetParams();
        public List<String> datasets = new ArrayList<>();

        @Override
        public String getApiName() {
            return "listStores";
        }
    }

    /**
     * "name": "Top Applications", "widgetid": "apps_list:group_top", "ref_id": "group_top"
     * "name": "Latest Applications", "widgetid": "apps_list:group_latest", "ref_id": "group_latest"
     * "name": "Apps for Kids", "widgetid": "apps_list:ucat_3194", "ref_id": "ucat_3194"
     * "name": "Aptoide Publishers", "widgetid": "apps_list:ucat_3239", "ref_id": "ucat_3239"
     * "name": "Applications", "widgetid": "apps_list:cat_1", "ref_id": "cat_1"
     * "name": "Games", "widgetid": "apps_list:cat_2", "ref_id": "cat_2"
     * "name": "Essential Apps", "widgetid": "apps_list:ucat_3183", "ref_id": "ucat_3183"
     * "name": "Summer Apps", "widgetid": "apps_list:ucat_4087", "ref_id": "ucat_4087"
     * "name": "Play-it!", "widgetid": "apps_list:ucat_4086", "ref_id": "ucat_4086"
     * "name": "Reviews", "widgetid": "reviews_list:latest", "ref_id": "latest"
     */
    public static class ListApps implements ApiParam {
        public int limit;
        public Number offset;
        public String order_by;
        public String order_dir;
        public DatasetParams datasets_params = new DatasetParams();
        public List<String> datasets = new ArrayList<>();

        @Override
        public String getApiName() {
            return "listApps";
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetStore implements ApiParam {
        public String store_name;
        public String store_user;
        public String store_pass_sha1;
        public List<String> datasets = new ArrayList<>();
        public DatasetParams datasets_params = new DatasetParams();

        public List<String> getDatasets() {
            return datasets;
        }

        public void addDataset(String dataset) {
            datasets.add(dataset);
        }

        public DatasetParams getDatasets_params() {
            return datasets_params;
        }

        @Override
        public String getApiName() {
            return "getStore";
        }

        public static class CategoriesParams implements DatasetParam {
            public String parent_ref_id;

            public void setParent_ref_id(String parent_ref_id) {
                this.parent_ref_id = parent_ref_id;
            }

            @Override
            public String getDatasetName() {
                return "categories";
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class WidgetParams implements DatasetParam {
            private String context;
            public String widgetid;
            public Number offset;
            public Number limit;

            public String getContext() {
                return context;
            }

            public void setContext(String context) {
                this.context = context;
            }

            @Override
            public String getDatasetName() {
                return "widgets";
            }

            public void setWidgetid(String widgetid) {
                this.widgetid = widgetid;
            }
        }
    }

    public interface DatasetParam {
        @JsonIgnore
        String getDatasetName();
    }

    public static class DatasetParams {
        private Map<String, DatasetParam> other = new HashMap<>();

        @JsonAnyGetter
        public Map<String, DatasetParam> any() {
            return other;
        }

        @JsonAnySetter
        public void set(DatasetParam value) {
            other.put(value.getDatasetName(), value);
        }
    }
}
