package com.aptoide.dataprovider.webservices.models.v7;

import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.Action.Event;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of apps, categories and stores
 * Created by hsousa on 16/09/15.
 */
public class ListViewItems {

    public Info info;
    public DataList datalist;   //used only on listApps
    public List<DisplayList> list;    //used only on categories (ie, DISPLAYS)


    public static class DataList {

        public Number total;
        public Number count;
        public Number next;
        public Number offset;
        public Number limit;
        public Number hidden;
        @JsonProperty("list")
        public List<ViewItem> itemView = new ArrayList<>();
    }

    public static class DisplayList {

        public String label;
        public String graphic;
        public Event event;
    }

}
