package com.aptoide.dataprovider.webservices.v7;

import com.aptoide.dataprovider.exceptions.TicketException;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetStore;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreTabs;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets;

import java.util.Iterator;
import java.util.List;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_APK_COMMENTS_TAB;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_STORE_TAB;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_STORE_WIDGETS_TAB;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.GET_REVIEWS_TAB;

/**
 * Created by hsousa on 14/09/15.
 */
public class GetStoreRequestv7 extends BaseStoreRequest<GetStore> {

    public long storeId;
    public String storeName;
    public String user;
    public String password;
    public String country;

    public GetStoreRequestv7(int numColumns) {
        super(numColumns);

    }

    @Override
    protected GetStore getResponse(Apiv7GetStore api) throws TicketException {
        return getService().getStore(api);
    }

    public StoreHomeTab bind(GetStore response) {
        StoreHomeTab tab = new StoreHomeTab();
        List<GetStoreWidgets.WidgetDatalist.WidgetList> list;

        try {
            tab.store = response;
            list = response.nodes.widgets.datalist.widgetList;

            /** Remove unknown tabs */
            for(Iterator<GetStoreTabs.Tab> i = response.nodes.tabs.tabList.iterator(); i.hasNext();) {

                switch (i.next().event.name) {
                    case GET_STORE_TAB:
                    case GET_STORE_WIDGETS_TAB:
                    case GET_APK_COMMENTS_TAB:
                    case GET_REVIEWS_TAB:
                        break;
                    default:
                        i.remove();
                }
            }

        } catch (Exception e) {
            return tab;
        }

        parseWidgetList(tab, list);

        return tab;
    }

    @Override
    public Apiv7GetStore getApi() {
        Apiv7GetStore api = super.getApi();

        if (storeId == 0) {
            api.store_name = storeName;
        } else {
            api.store_id = storeId;

            api.store_user = user;
            api.store_pass_sha1 = password;

        }
        return api;
    }
}
