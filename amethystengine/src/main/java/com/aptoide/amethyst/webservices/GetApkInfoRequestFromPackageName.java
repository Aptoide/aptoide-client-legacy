package com.aptoide.amethyst.webservices;

import android.content.Context;

import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fabio on 22-10-2015.
 */
public class GetApkInfoRequestFromPackageName extends GetApkInfoRequest {

    public GetApkInfoRequestFromPackageName(Context context) {
        super(context);
    }
    protected ArrayList<WebserviceOptions> fillWithExtraOptions(ArrayList<WebserviceOptions> options){
        if(token!=null)
            options.add(new WebserviceOptions("token", token));
        return options;
    }
    protected HashMap<String, String > getParameters() {
        HashMap<String, String > parameters = new HashMap<String, String>();
        if(repoName != null) {
            parameters.put("repo", repoName);
        }
        parameters.put("identif", "package:" + packageName);
        return parameters;
    }

}
