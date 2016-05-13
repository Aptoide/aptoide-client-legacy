package com.aptoide.amethyst.webservices;

import android.os.Build;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 29-07-2014.
 */
public class RegisterAdRefererRequest extends RetrofitSpiceRequest<RegisterAdRefererRequest.DefaultResponse, RegisterAdRefererRequest.Webservice> {

     private long adId;
     private long appId;
     private String tracker;
     private String success;

    public RegisterAdRefererRequest(long adId, long appId, String clickUrl, boolean success) {
        super(DefaultResponse.class, Webservice.class);
        this.adId = adId;
        this.appId = appId;
        this.success = (success ? "1" : "0");

        extractAndSetTracker(clickUrl);
    }

    private void extractAndSetTracker(String clickUrl) {
        int i = clickUrl.indexOf("//");

        int last = clickUrl.indexOf("/", i + 2);

        tracker = clickUrl.substring(0, last);
    }

    public RegisterAdRefererRequest() {
        super(DefaultResponse.class, Webservice.class);
    }

    public interface Webservice {
        @POST(url)
        @FormUrlEncoded
        DefaultResponse load(@FieldMap HashMap<String, String> arg);
    }

    final static String url = "/webservices.aptwords.net/api/2/registerAdReferer";

    @Override
    public DefaultResponse loadDataFromNetwork() throws Exception {

        HashMap<String, String> map = new HashMap<>();

        map.put("success", success);
        map.put("adid", Long.toString(adId));
        map.put("appid", Long.toString(appId));
        map.put("q", AptoideUtils.HWSpecifications.filters(Aptoide.getContext()));
        map.put("androidversion", Build.VERSION.RELEASE);
        map.put("tracker", tracker);

//        http://webservices.aptwords.net/api/2/registerAdReferer/
// success/1
// /adid/2336
// /appid/8038083
// /q/bWF4U2RrPTIxJm1heFNjcmVlbj1ub3JtYWwmbWF4R2xlcz0zLjAmbXlDUFU9YXJtZWFiaS12N2EsYXJtZWFiaSZteURlbnNpdHk9MzIw
// /androidversion/4.0
// /httpcode/404
// /tracker/aptwords.net
// /mimetype/text_html

        return getService().load(map);
    }

    public static class DefaultResponse {
        String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static RequestListener<DefaultResponse> newDefaultResponse() {
        return new RequestListener<DefaultResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(RegisterAdRefererRequest.DefaultResponse defaultResponse) {

            }
        };
    }

    public long getAdId() {
        return adId;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getTracker() {
        return tracker;
    }

    public void setTracker(String tracker) {
        this.tracker = tracker;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
