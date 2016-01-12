package com.aptoide.amethyst.webservices.v2;

import android.content.Context;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 27-12-2013.
 */
public class AddCommentRequest extends RetrofitSpiceRequest<GenericResponseV2, AddCommentRequest.Webservice> {


    public interface Webservice {
        @POST("/webservices.aptoide.com/webservices/3/addApkComment")
        @FormUrlEncoded
        GenericResponseV2 addComment(@FieldMap HashMap<String, String> args);
    }

    String baseUrl = WebserviceOptions.WebServicesLink + "3/addApkComment";
    private Context context;
    private String token;
    private String repo;
    private String packageName;
    private String apkversion;
    private String text;
    private String answearTo;

    public void setToken(String token) {
        this.token = token;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setApkversion(String apkversion) {
        this.apkversion = apkversion;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAnswearTo(String answearTo) {
        this.answearTo = answearTo;
    }

    public AddCommentRequest(Context context) {
        super(GenericResponseV2.class, Webservice.class);
        this.context = context;
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("mode", "json");

        parameters.put("repo", repo);
        parameters.put("apkid", packageName);
        parameters.put("apkversion", apkversion);
        parameters.put("text", text);
        parameters.put("lang", AptoideUtils.StringUtils.getMyCountryCode(context));
        if (answearTo != null) {
            parameters.put("answerto", answearTo);
        }

        token = SecurePreferences.getInstance().getString("access_token", "empty");

        parameters.put("access_token", token);

        GenericResponseV2 responseV2 = null;

        try {
            responseV2 = getService().addComment(parameters);
        } catch (RetrofitError error) {
            OauthErrorHandler.handle(error);
        }


        return responseV2;

    }

}
