package com.aptoide.dataprovider.webservices.v7;

import android.support.annotation.NonNull;

import com.aptoide.dataprovider.webservices.interfaces.v7.IGetAppV7WebService;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetApp;
import com.aptoide.dataprovider.webservices.models.v7.ViewItem;
import com.aptoide.models.displayables.DisplayableList;
import com.aptoide.models.displayables.MoreVersionsItem;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by fabio on 28-10-2015.
 */
public class GetMoreVersionsAppRequest extends RetrofitSpiceRequest<DisplayableList, IGetAppV7WebService> {

    public int totalSpanSize;
    public boolean loggedIn;
    public int singleSpanSize;
    public int numColumns;

    public boolean mature;
    public String lang;
    public int aptoideVercode;
    public long appId;
    public String filters;
    public String packageName;
    public int limit;
    public int offset;

    public GetMoreVersionsAppRequest(int numColumns) {
        super(DisplayableList.class, IGetAppV7WebService.class);
        this.numColumns = numColumns;
        this.totalSpanSize = numColumns * 2;
        this.singleSpanSize = totalSpanSize / numColumns;
    }

    @Override
    public DisplayableList loadDataFromNetwork() throws Exception {

        GetApp.Nodes.ListAppsVersions listAppsVersions = getService().listAppsVersions(getApi());
        DisplayableList list = new DisplayableList();

        for (ViewItem item : listAppsVersions.list) {
            list.add(getAppVersionRow(item));
        }

        return list;
    }

    /**
     * This API has no mandatory arguments, excepto app_id / packageName
     *
     * @return
     */
    public Apiv7GetStore getApi() {

        Apiv7GetStore api = new Apiv7GetStore();

        api.mature = mature;
        api.q = filters;
        api.lang = lang;
        api.aptoide_vercode = aptoideVercode;
//        api.app_id = appId;
//        api.apk_md5sum = apk_md5sum;
        api.packageName = packageName;
        api.limit = limit;
        api.offset = offset;

        return api;
    }

    @NonNull
    protected MoreVersionsItem getAppVersionRow(ViewItem viewItem) {
        MoreVersionsItem appItem = new MoreVersionsItem(numColumns);
        appItem.icon = viewItem.icon;
        appItem.appName = viewItem.name;
        appItem.versionName = viewItem.file.vername;
        appItem.versionCode = viewItem.file.vercode.toString();
        appItem.packageName = viewItem.packageName;
        appItem.storeName = viewItem.store.name;
        appItem.updated = viewItem.updated;
        appItem.modified = viewItem.modified;
        appItem.id = viewItem.id.longValue();
        appItem.setSpanSize(singleSpanSize);
        return appItem;
    }
}
