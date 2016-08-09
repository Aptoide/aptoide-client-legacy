package com.aptoide.dataprovider.webservices.v7;

import com.aptoide.dataprovider.exceptions.MalformedActionUrlException;
import com.aptoide.dataprovider.exceptions.TicketException;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets;

import java.util.List;

/**
 *
 * This request is used for the Category Tabs of the StoreActivity
 * Created by hsousa on 14/09/15.
 */
public class GetStoreWidgetRequestv7 extends BaseStoreRequest<GetStoreWidgets> {

    private String actionUrl;
    public String widget;
    public String user;
    public String password;


    public GetStoreWidgetRequestv7(String actionUrl, int numColumns) {
        super(numColumns);

        try {
            this.actionUrl = setActionUrl(actionUrl);
        } catch (MalformedActionUrlException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected GetStoreWidgets getResponse(Apiv7GetStore api) throws TicketException {
        return getService().postStoreWidget(actionUrl, api);
    }


    @Override
    public StoreHomeTab bind(GetStoreWidgets response) {
        StoreHomeTab tab = new StoreHomeTab();
        List<GetStoreWidgets.WidgetDatalist.WidgetList> list;
        try {
            list = response.datalist.widgetList;
        } catch (Exception e) {
            return tab;
        }


        parseWidgetList(tab, list);
        return tab;
    }

    @Override
    public Apiv7GetStore getApi() {
        Apiv7GetStore api = super.getApi();


        api.store_user = user;
        api.store_pass_sha1 = password;

        return api;
    }

}
