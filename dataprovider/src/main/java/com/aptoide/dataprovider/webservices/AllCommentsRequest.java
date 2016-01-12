package com.aptoide.dataprovider.webservices;

import com.aptoide.dataprovider.webservices.models.v2.GetComments;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.aptoide.dataprovider.webservices.models.v2.Apiv2;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by rmateus on 27-12-2013.
 */
public class AllCommentsRequest extends RetrofitSpiceRequest<GetComments, AllCommentsRequest.WebService> {

    public interface WebService {
        @POST(baseUrl)
        GetComments getAllComments(@Body Apiv2 api);
    }

    private static final String baseUrl = WebserviceOptions.WebServicesLink + "2/listApkComments";

    public String storeName;
    public String packageName;
    public String versionName;
    public String filters;
    public String lang;
    public int limit;
    public int offset;

    public AllCommentsRequest() {
        super(GetComments.class, WebService.class);
    }


    @Override
    public GetComments loadDataFromNetwork() throws Exception {
        Apiv2 api = new Apiv2();
        api.mode = "json";
        api.q = filters;
        api.lang = lang;
        api.storeName = storeName;
        api.packageName = packageName;
        api.versionName = versionName;
        api.limit = limit;
        api.offset = offset;
        return getService().getAllComments(api);
    }

}
