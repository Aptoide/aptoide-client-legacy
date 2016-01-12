package com.aptoide.dataprovider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.retrofit.RetrofitJackson2SpiceService;

import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

/**
 * Created by rmateus on 29/05/15.
 */
public class AptoideSpiceHttpService extends RetrofitJackson2SpiceService {

    @Override
    public int getThreadCount() {
        return 4;
    }

    @Override
    protected String getServerUrl() {
        return "http://";
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        return new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.NONE).setEndpoint(getServerUrl()).setConverter(getConverter());
    }

    @Override
    protected Converter createConverter() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return new JacksonConverter(objectMapper);
    }
}
