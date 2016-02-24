package com.aptoide.amethyst.webservices;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.webservices.json.GetApkInfoJson;

import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by asantos on 07-08-2014.
 */
public abstract class GetApkInfoRequest extends RetrofitSpiceRequest<GetApkInfoJson, GetApkInfoRequest.Webservice> {


    protected String repoName;
    protected String packageName;
    protected String versionName;
    protected String token;
    protected Context context;
    private boolean fromSponsored;

    public GetApkInfoRequest(Context context) {
        super(GetApkInfoJson.class, GetApkInfoRequest.Webservice.class);
        this.context = context;
    }

    public boolean isFromSponsored() {
        return fromSponsored;
    }

    public void setFromSponsored(boolean fromSponsored) {
        this.fromSponsored = fromSponsored;
    }

    protected abstract ArrayList<WebserviceOptions> fillWithExtraOptions(ArrayList<WebserviceOptions> options);

    protected abstract HashMap<String, String> getParameters();

    @Override
    public GetApkInfoJson loadDataFromNetwork() throws Exception {
        ArrayList<WebserviceOptions> options = getoptions();
        token = SecurePreferences.getInstance().getString("access_token", null);
        fillWithExtraOptions(options);
        HashMap<String, String> parameters = getParameters();
        parameters.put("options", buildOptions(options));
        parameters.put("mode", "json");
        parameters.put("access_token", token);

        if (fromSponsored) {
            parameters.put("adview", "1");
        }

        try {
            return getService().getApkInfo(parameters);
        } catch (RetrofitError e) {
            OauthErrorHandler.handle(e);
            throw e;
        }

    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    protected ArrayList<WebserviceOptions> getoptions() {

        ArrayList<WebserviceOptions> options = new ArrayList<WebserviceOptions>();
        options.add(new WebserviceOptions("cmtlimit", "5"));
        options.add(new WebserviceOptions("payinfo", "true"));
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            options.add(new WebserviceOptions("mnc", getMncCode(telephonyManager.getNetworkOperator())));
            options.add(new WebserviceOptions("mcc", getMccCode(telephonyManager.getNetworkOperator())));
        }

        options.add(new WebserviceOptions("q", AptoideUtils.HWSpecifications.filters(context)));
        options.add(new WebserviceOptions("lang", AptoideUtils.StringUtils.getMyCountryCode(context)));
        return options;
    }

    private String getMccCode(String networkOperator) {
        return networkOperator == null ? "" : networkOperator.substring(0, mncPortionLength(networkOperator));

    }

    private int mncPortionLength(String networkOperator) {
        return Math.min(3, networkOperator.length());
    }

    private String getMncCode(String networkOperator) {
        return networkOperator == null ? "" : networkOperator.substring(mncPortionLength(networkOperator));

    }

    protected String buildOptions(ArrayList<WebserviceOptions> options) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (WebserviceOptions option : options) {
            sb.append(option);
            sb.append(";");
        }
        sb.append(")");
        return sb.toString();
    }

    public interface Webservice {
        @POST("/webservices.aptoide.com/webservices/3/getApkInfo")
        @FormUrlEncoded
        GetApkInfoJson getApkInfo(@FieldMap HashMap<String, String> args);
    }
}
