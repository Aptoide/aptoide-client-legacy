package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.webservices.interfaces.v7.IGetAppV7WebService;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.GetAppModel;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetApp;
import com.aptoide.dataprovider.webservices.models.v7.ViewItem;
import com.aptoide.models.displayables.MoreVersionsAppViewItem;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.RetrofitError;

/**
 * Created by hsousa on 28/10/15.
 * This could (and should) be refactored, but there is no time
 */
public class GetAppRequest extends RetrofitSpiceRequest<GetAppModel, IGetAppV7WebService> {


    public String token;
    public Boolean mature;
    public String lang;
    public Integer aptoideVercode;
    public String filters;
    public String md5;

    public long appId;
    public String packageName;
    public String storeName;

    public String user;
    public String password;
    private int numColumns;

    /**
     * Tested with static. Although probably not needed, it needs testing if you convert it to a field
     */
    static int attempts = 0;

    public GetAppRequest(int numColumns) {
        super(GetAppModel.class, IGetAppV7WebService.class);
        this.numColumns = numColumns;
    }

    @Override
    public GetAppModel loadDataFromNetwork() throws Exception {
        attempts++;
        try {
            return bind(getService().getApp(getApi()));
        } catch (RetrofitError error) {
            if (attempts == 1) {
                OauthErrorHandler.handle(error);
            }
            throw error;
        }
    }

    private GetAppModel bind(GetApp getApp) {
        GetAppModel model = new GetAppModel();
        model.getApp = getApp;

        for (ViewItem item : getApp.nodes.versions.list) {
            model.list.add(createMore(item));
        }

        return model;
    }

    /**
     * Given a Json POJO, creates a MoreVersionsAppViewItem displayable in order to
     * populate the moreVersions recyclerView
     *
     * @param item POJO coming from the webservice's Json
     * @return A MoreVersionsAppViewItem displayable item
     */
    private MoreVersionsAppViewItem createMore(ViewItem item) {

        MoreVersionsAppViewItem moreVersionsItem = new MoreVersionsAppViewItem(numColumns);
        moreVersionsItem.id = item.id.longValue();
        moreVersionsItem.storeName = item.store.name;
        moreVersionsItem.storeAvatar = item.store.avatar;
        moreVersionsItem.storeId = item.store.id.longValue();
        moreVersionsItem.storeTheme = item.store.appearance.theme;
        moreVersionsItem.appName = item.name;
        moreVersionsItem.packageName = item.packageName;
        moreVersionsItem.versionName = item.file.vername;
        moreVersionsItem.icon = item.icon;
        moreVersionsItem.versionCode = item.file.vercode.intValue();

        return moreVersionsItem;
    }

    /**
     * This API has no mandatory arguments, except app_id
     * <p>
     * However, it has one specific "feature":
     * <p>
     * In order to appear the latest "global" version of an app, it is necessary to
     * 1) pass along the packageName so it will be passed along to the "versions" node, and
     * 2) do _not_ pass the storeName.
     * According to the web team, passing the packageName and ignoring the storeName
     * will search this app on other stores other than "apps".
     * <p>
     *     Right now, the "versions" node only returns trusted versions when no store is defined as
     *     an api parameter, so it may happen the that the "versions" node may come empty,
     *     even when there are other apks.
     * </p>
     *
     * @return Apiv7 the Api params
     */
    public Apiv7GetStore getApi() {

        Apiv7GetStore api = new Apiv7GetStore();

        api.mature = mature;
        api.q = filters;
        api.lang = lang;
        api.aptoide_vercode = aptoideVercode;
        if (attempts < 3) {
            api.access_token = SecurePreferences.getInstance().getString(Constants.ACCESS_TOKEN, null);
        } else {
            attempts = 0;
        }

        Apiv7GetStore.NodeParams metaParams = new Apiv7GetStore.NodeParams("meta");
        metaParams.package_name = packageName;

        Apiv7GetStore.NodeParams versionsParams = new Apiv7GetStore.NodeParams("versions");
        versionsParams.package_name = packageName;

        if (appId == 0) {
            metaParams.apk_md5sum = md5;
            versionsParams.apk_md5sum = md5;
            metaParams.store_name = storeName;
            api.setNodeParams(versionsParams);
        } else {
            metaParams.app_id = String.valueOf(appId);
            if (packageName == null) {
                versionsParams.app_id = String.valueOf(appId);
            }
        }

        api.setNodeParams(versionsParams);
        api.setNodeParams(metaParams);

        api.store_user = user;
        api.store_pass_sha1 = password;

        return api;
    }

}
