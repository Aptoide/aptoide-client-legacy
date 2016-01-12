package com.aptoide.dataprovider.webservices;

import com.aptoide.dataprovider.webservices.json.review.ReviewJson;
import com.aptoide.dataprovider.webservices.json.review.ReviewListJson;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 23-02-2015.
 */
public abstract class GetReviews<T> extends RetrofitSpiceRequest<T, GetReviews.GetReviewListWebservice> {

    public long store_id;
    public int offset;
    public int limit;

    public GetReviews(Class<T> clazz) {
        super(clazz, GetReviewListWebservice.class);
    }

    @Override
    public T loadDataFromNetwork() throws Exception {
        HashMap<String, String> args = new HashMap<>();

        args.put("mode", "json");
        args.put("status", "active");
        args.put("repo_id", String.valueOf(store_id));
        if (offset > 0) {
            args.put("offset", String.valueOf(offset));
        }

        if (limit > 0) {
            args.put("limit", String.valueOf(limit));
        }

        return response(args);
    }

    public abstract T response(HashMap<String, String> args);

    public interface GetReviewListWebservice {

        @POST("/webservices.aptoide.com/webservices/3/getReviewList")
        @FormUrlEncoded
        ReviewListJson getReviewList(@FieldMap HashMap<String, String> map);

        @POST("/webservices.aptoide.com/webservices/3/getReview")
        @FormUrlEncoded
        ReviewJson getReview(@FieldMap HashMap<String, String> map);
    }

    public static class GetReview extends GetReviews<ReviewJson> {
        public void setId(int id) {
            this.id = id;
        }
        public void setDensity(String density) {
            this.density = density;
        }

        private int id;
        private String density;

        public GetReview() {
            super(ReviewJson.class);
        }

        @Override
        public ReviewJson response(HashMap<String, String> args) {

            if (id > 0) {
                args.put("id", String.valueOf(id));
            }

            args.put("ss_resolution_type", density);

            return getService().getReview(args);
        }
    }


    public static class GetReviewList extends GetReviews<ReviewListJson> {
        public long store_id;
        public boolean homePage;

        private String order_by;
        private String order = "asc";

        public GetReviewList() {
            super(ReviewListJson.class);
        }

        @Override
        public ReviewListJson response(HashMap<String, String> args) {

            if (homePage) {
                args.put("editors", "true");
            }

            if (store_id > 0) {
                args.put("repo_id", String.valueOf(store_id));
            }

            if (order_by != null) {
                args.put("order_by", order_by);
            }
            args.put("order", order);
            args.put("status", "active");
            return getService().getReviewList(args);
        }

        /**
         * @param order_by Sort by: options are: id, rand

         */

        public void setOrderBy(String order_by) {
            this.order_by = order_by;
        }

        /**
         * @param order Sort order: options are: asc, desc
         */
        public void setOrder(String order) {
            this.order = order;
        }
    }

}
