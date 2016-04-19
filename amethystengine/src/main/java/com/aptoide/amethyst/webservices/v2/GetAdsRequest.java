package com.aptoide.amethyst.webservices.v2;

import android.text.TextUtils;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.preferences.EnumPreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.ReferrerUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.InstalledPackage;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rmateus on 29-07-2014.
 */
public class GetAdsRequest extends RetrofitSpiceRequest<ApkSuggestionJson, GetAdsRequest.Webservice> {

    private int CONNECTION_TIMEOUT = 10000;

    private String location;
    private String keyword;
    private int limit;
    private String package_name;
    private String repo;
    private String categories;

    private String excludedPackage;
    private boolean addGlobalExcludedAds;
    private String excludedNetworks;

    public GetAdsRequest(String excludedPackageName, boolean addGlobalExcludedAds) {
        this();
        this.excludedPackage = excludedPackageName;
        this.addGlobalExcludedAds = addGlobalExcludedAds;
    }

    public GetAdsRequest() {
        super(ApkSuggestionJson.class, Webservice.class);

    }

    public interface Webservice{
        @POST("/webservices.aptwords.net/api/2/getAds")
        @FormUrlEncoded
        ApkSuggestionJson getAds(@FieldMap HashMap<String, String> arg);
    }

    String url = "http://webservices.aptwords.net/api/2/getAds";

    @Override
    public ApkSuggestionJson loadDataFromNetwork() throws Exception {

        HashMap<String, String> parameters = new HashMap<>();
        final AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());

        parameters.put("q", AptoideUtils.HWSpecifications.filters(Aptoide.getContext()));
        parameters.put("lang", AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext()));

        String myid = AptoideUtils.getSharedPreferences().getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), "NoInfo");
        parameters.put("cpuid", myid);

        String mature = "0";

        if(AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false)) {
            mature = "1";
        }

        parameters.put("aptvercode", String.valueOf(AptoideUtils.getSharedPreferences().getInt("version", 0)));
        parameters.put("location","native-aptoide:" + location);
        parameters.put("type", "1-3");

        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(Aptoide.getContext())==0){
            parameters.put("flag", "gms");
        }

        parameters.put("keywords", keyword);
        parameters.put("categories", categories);


        String oemid = Aptoide.getConfiguration().getExtraId();

        if( !TextUtils.isEmpty(oemid) ){
            parameters.put("oemid", oemid);
        }

        String join;
        final ArrayList<String> excludedAds = database.getExcludedAds();
        if(excludedPackage != null && !excludedPackage.isEmpty()){
            excludedAds.add(excludedPackage);
        }

        if (excludedPackage == null || addGlobalExcludedAds) {
            join = TextUtils.join(",", excludedAds);
        } else {
            join = excludedPackage;
        }

        if(!TextUtils.isEmpty(join)){
            parameters.put("excluded_pkg", join);
        }

        parameters.put("limit", String.valueOf(limit));
        parameters.put("get_mature", mature);
        parameters.put("partners", "1-3,5-10");
        //parameters.put("partners", "7-9");
        parameters.put("app_pkg", package_name);
        parameters.put("app_store", repo);
        parameters.put("filter_pkg", "true");
        parameters.put("conn_type", AptoideUtils.NetworkUtils.getConnectionType().toString());

        if(Aptoide.DEBUG_MODE){
            parameters.put("country", AptoideUtils.getSharedPreferences().getString("forcecountry", null));
        }

        if (excludedNetworks != null) {
            parameters.put("excluded_partners", excludedNetworks);
        }

        ApkSuggestionJson result = getService().getAds(parameters);


        Map<String, String> adsParams = new HashMap<String, String>();
        adsParams.put("placement", location);
        final ArrayList<String> arrayList = new ArrayList<>();

        for(ApkSuggestionJson.Ads suggestionJson : result.ads) {
            String ad_type = suggestionJson.info.ad_type;
            adsParams.put("type", ad_type);

            arrayList.add(suggestionJson.data.packageName);

//            FlurryAgent.logEvent("Get_Sponsored_Ad", adsParams);

            if(suggestionJson.partner != null){

                try{
                    String impressionUrlString = suggestionJson.partner.partnerData.impression_url;

                    impressionUrlString = AptoideUtils.AdNetworks.parseString(Aptoide.getContext(), impressionUrlString);

                    Request request = new Request.Builder().get().url(impressionUrlString).build();

                    new OkHttpClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(Response response) throws IOException {

                        }

                    });

                } catch (Exception ignored) {}

            }

//            Log.d("AdsFlurry", "Map is " + adsParams);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> installedPackages = new ArrayList<>();
                List<InstalledPackage> startupInstalled = database.getStartupInstalled();

                for (InstalledPackage installedPackage : startupInstalled) {
                    installedPackages.add(installedPackage.getPackage_name());
                }

                arrayList.retainAll(installedPackages);
                arrayList.removeAll(excludedAds);

                for (String excludedAd : arrayList) {
                    database.addToAdsExcluded(excludedAd);
                }


            }
        }).start();

        return result;
    }

    public void setTimeout(int timeout){
        CONNECTION_TIMEOUT = timeout;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public static GetAdsRequest newDefaultRequest(String placement, String packageName) {

        final GetAdsRequest request = new GetAdsRequest();
        request.setLimit(1);
        request.setLocation(placement);
        request.setKeyword("__NULL__");
        request.setPackage_name(packageName);

        // Só queremos adicionar excluídos no secondtry.
        if ("secondtry".equals(placement)) {
            if (ReferrerUtils.excludedCampaings.containsKey(packageName)) {
                request.excludedNetworks = AptoideUtils.StringUtils.commaSeparatedValues(ReferrerUtils.excludedCampaings.get(packageName));
            }
        }

        return request;
    }
}
