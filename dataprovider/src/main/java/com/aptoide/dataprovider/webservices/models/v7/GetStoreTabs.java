package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event;

/**
 * Created by hsousa on 17/09/15.
 */
public class GetStoreTabs {

    public Info info;
    @JsonProperty("list")
    public List<Tab> tabList;

    public static class Tab {

        public String label;
        public Event event;
    }
}