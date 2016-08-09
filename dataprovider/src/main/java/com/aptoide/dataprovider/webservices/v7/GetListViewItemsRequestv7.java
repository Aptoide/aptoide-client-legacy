/*******************************************************************************
 * Copyright (c) 2015 hsousa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.dataprovider.webservices.v7;

import com.aptoide.dataprovider.exceptions.MalformedActionUrlException;
import com.aptoide.dataprovider.exceptions.TicketException;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7GetStore;
import com.aptoide.dataprovider.webservices.models.v7.ListViewItems;
import com.aptoide.dataprovider.webservices.models.v7.ViewItem;
import com.aptoide.dataprovider.webservices.models.v7.ViewItemDataList;
import com.aptoide.models.displayables.AppItem;
import com.aptoide.models.displayables.BrickAppItem;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_LIST_APPS;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_LIST_STORES;

/**
 * This request is used for the retrieving Apps and Stores, and can be reused with listStores and listApps
 * <p/>
 * Created by hsousa on 14/09/15.
 */
public class GetListViewItemsRequestv7 extends BaseStoreRequest<ListViewItems> {

    private String layout;
    private String actionUrl;
    private int offset;

    public String user;
    public String password;

    public GetListViewItemsRequestv7(String actionUrl, String layout, int numColumns, int offset) {
        super(numColumns);
        this.layout = layout;
        this.offset = offset;
        try {
            this.actionUrl = setActionUrl(actionUrl);
        } catch (MalformedActionUrlException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected ListViewItems getResponse(Apiv7GetStore api) throws TicketException {
        return getService().postViewItems(actionUrl, api);
    }


    @Override
    public StoreHomeTab bind(ListViewItems response) {
        StoreHomeTab tab = new StoreHomeTab();
        ViewItemDataList dataList;
        try {
            dataList = response.datalist;
        } catch (Exception e) {
            return tab;
        }

        if (actionUrl.contains(EVENT_LIST_APPS)) {
            for (ViewItem itemList : dataList.list) {
                if (Constants.LAYOUT_BRICK.equals(layout)) {
                    BrickAppItem brick = createBrickItem(itemList);
                    tab.list.add(brick);
                } else {
                    AppItem appItem = createAppItem(itemList);
                    tab.list.add(appItem);
                }
            }

        } else if (actionUrl.contains(EVENT_LIST_STORES)) {
            for (ViewItem itemList : dataList.list) {
                tab.list.add(createStoreItem(itemList));
            }

        }

        try {
            tab.offset = dataList.next.intValue();
            tab.total = dataList.total.intValue();
            tab.hidden = dataList.hidden.intValue();
        } catch (Exception e) {
            // offset only used on listApps and stores
        }

        return tab;
    }

    @Override
    public Apiv7GetStore getApi() {
        Apiv7GetStore api = super.getApi();
        api.limit = numColumns * 10;
        if (offset > 0) {
            api.offset = offset;
        }

        api.store_user = user;
        api.store_pass_sha1 = password;

        return api;
    }
}
