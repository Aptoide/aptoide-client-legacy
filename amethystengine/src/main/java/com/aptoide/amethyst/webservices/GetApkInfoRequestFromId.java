package com.aptoide.amethyst.webservices;

import android.content.Context;

import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fabio on 21-10-2015.
 */
public class GetApkInfoRequestFromId extends GetApkInfoRequest{
    public GetApkInfoRequestFromId(Context context) {
        super(context);
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    private String appId;
    protected ArrayList<WebserviceOptions> fillWithExtraOptions(ArrayList<WebserviceOptions> options){
        return options;
    }

    protected HashMap<String, String > getParameters(){
        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("identif", "id:" + appId);

        if(repoName != null) {
            parameters.put("repo", repoName);
        }

        parameters.put("mode", "json");
        return parameters;
    }

}
