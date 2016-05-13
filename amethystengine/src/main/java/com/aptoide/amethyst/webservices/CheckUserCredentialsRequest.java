package com.aptoide.amethyst.webservices;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.aptoide.amethyst.LoginActivity;
import com.aptoide.amethyst.model.json.CheckUserCredentialsJson;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Filters;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;
import java.util.Locale;

import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by brutus on 09-12-2013.
 */
public class CheckUserCredentialsRequest extends RetrofitSpiceRequest<CheckUserCredentialsJson, CheckUserCredentialsRequest.Webservice> {

    String baseUrl = WebserviceOptions.WebServicesLink+"3/getUserInfo";

    public interface Webservice{
        @FormUrlEncoded
        @POST("/webservices.aptoide.com/webservices/3/getUserInfo")
        CheckUserCredentialsJson getUserInfo(@FieldMap HashMap<String, String> args);
    }

    //"http://www.aptoide.com/webservices/checkUserCredentials/";

    private String user;
    private String password;
    private String repo;
    private String avatar;

    private boolean registerDevice;

    private String deviceId;
    private String model;
    private String sdk;
    private String density;
    private String cpu;
    private String screenSize;
    private String openGl;
    private String nameForGoogle;
    private LoginActivity.Mode mode;
    private String token;

    public CheckUserCredentialsRequest() {
        super(CheckUserCredentialsJson.class, CheckUserCredentialsRequest.Webservice.class);
    }

    protected Converter createConverter() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return new JacksonConverter(objectMapper);
    }

    @Override
    public CheckUserCredentialsJson loadDataFromNetwork() throws Exception {

//        GenericUrl url = new GenericUrl(baseUrl);

        HashMap<String, String > parameters = new HashMap<String, String>();
        //token = SecurePreferences.getInstance().getString("access_token", null);

        parameters.put("access_token", token);


        if(registerDevice) {
            parameters.put("device_id", deviceId);
            parameters.put("model", model);
            parameters.put("maxSdk", sdk);
            parameters.put("myDensity", density);
            parameters.put("myCpu", cpu);
            parameters.put("maxScreen", screenSize);
            parameters.put("maxGles", openGl);
        }

        parameters.put("mode", "json");

        //RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://").setConverter(createConverter()).build();
        //setService(adapter.create(getRetrofitedInterfaceClass()));


        return getService().getUserInfo(parameters);

//        HttpContent content = new UrlEncodedContent(parameters);
//
//        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
//
//        request.setParser(new JacksonFactory().createJsonObjectParser());
//
//        return request.execute().parseAs( getResultType() );
    }

    public String getUser() {
        return user;
    }

    public CheckUserCredentialsRequest setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CheckUserCredentialsRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setRegisterDevice(boolean registerDevice) {
        this.registerDevice = registerDevice;
    }

    public CheckUserCredentialsRequest setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public CheckUserCredentialsRequest setModel(String model) {
        this.model = model;
        return this;
    }

    public CheckUserCredentialsRequest setSdk(String sdk) {
        this.sdk = sdk;
        return this;
    }

    public CheckUserCredentialsRequest setDensity(String density) {
        this.density = density;
        return this;
    }

    public CheckUserCredentialsRequest setCpu(String cpu) {
        this.cpu = cpu;
        return this;
    }

    public CheckUserCredentialsRequest setScreenSize(String screenSize) {
        this.screenSize = screenSize;
        return this;
    }

    public CheckUserCredentialsRequest setOpenGl(String openGl) {
        this.openGl = openGl;
        return this;
    }

    public void setNameForGoogle(String nameForGoogle) {
        this.nameForGoogle = nameForGoogle;
    }

    public String getNameForGoogle() {
        return nameForGoogle;
    }

    public void setMode(LoginActivity.Mode mode) {
        this.mode = mode;
    }

    public LoginActivity.Mode getMode() {
        return mode;
    }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public void setToken(String token) {
        this.token = token;
    }

    public static CheckUserCredentialsRequest buildDefaultRequest(Context c, String token){
        CheckUserCredentialsRequest request = new CheckUserCredentialsRequest();

        String deviceId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);

        request.setSdk(String.valueOf(AptoideUtils.HWSpecifications.getSdkVer()));
        request.setDeviceId(deviceId);
        request.setCpu(AptoideUtils.HWSpecifications.getAbis());
        request.setDensity(String.valueOf(AptoideUtils.HWSpecifications.getNumericScreenSize(c)));
        request.setOpenGl(String.valueOf(AptoideUtils.HWSpecifications.getGlEsVer(c)));
        request.setModel(Build.MODEL);
        request.setScreenSize(Filters.Screen.values()[AptoideUtils.HWSpecifications.getScreenSize(c)].name().toLowerCase(Locale.ENGLISH));

        request.setToken(token);
        return request;
    }
}
