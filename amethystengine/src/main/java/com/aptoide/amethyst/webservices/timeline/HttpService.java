package com.aptoide.amethyst.webservices.timeline;

import com.aptoide.amethyst.BuildConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.retrofit.RetrofitJackson2SpiceService;

import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

/**
 * Created by fabio on 13-10-2015.
 */
public class HttpService extends RetrofitJackson2SpiceService {

    @Override
    protected String getServerUrl() {
        return "http://";
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        return new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setEndpoint(getServerUrl())
                .setConverter(getConverter());
    }

    @Override
    protected Converter createConverter() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return new JacksonConverter(objectMapper);
    }

}