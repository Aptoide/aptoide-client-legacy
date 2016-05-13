package com.aptoide.dataprovider.webservices.models;

import com.aptoide.dataprovider.webservices.models.v7.GetApp;
import com.aptoide.models.displayables.MoreVersionsAppViewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 03/12/15.
 */
public class GetAppModel {

    /**
     * Json model of the app
     */
    public GetApp getApp;

    /**
     * DisplayableList for the MoreVersions
     */
    public List<MoreVersionsAppViewItem> list = new ArrayList<>();

}
