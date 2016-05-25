package com.aptoide.dataprovider.webservices.models.v7;

import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of apps, categories and stores
 * Created by hsousa on 16/09/15.
 */
public class ListViewItems {

    public Info info;
    public ViewItemDataList datalist;   //used only on listApps
    public List<DisplayList> list;    //used only on categories (ie, DISPLAYS)

    public static class DisplayList {

        public String tag;
        public String label;
        public String graphic;
        public Event event;
    }

}
