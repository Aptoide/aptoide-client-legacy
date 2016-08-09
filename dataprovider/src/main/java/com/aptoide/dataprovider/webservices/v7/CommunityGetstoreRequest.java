package com.aptoide.dataprovider.webservices.v7;

import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets;
import com.aptoide.models.displayables.HeaderRow;

import java.util.List;

/**
 * Created by fabio on 29-01-2016.
 */
public class CommunityGetstoreRequest extends GetStoreRequestv7 {
    int fullRow;

    public CommunityGetstoreRequest(int numColumns, int fullRow) {
        super(numColumns);
        this.fullRow = fullRow;
        singleSpanSize = fullRow / numColumns;
    }

    @Override
    protected HeaderRow createHeaderRow(String name, String tag, boolean hasMore, List<GetStoreWidgets.WidgetDatalist.WidgetList.Action> actions, long storeId, String layout) {

        if (actions != null && !actions.isEmpty()) {
            for (GetStoreWidgets.WidgetDatalist.WidgetList.Action action : actions) {
                if (action != null && action.event != null && action.event.action != null && action.type.equals("button")) {
                    return createHeaderRow(name, tag, hasMore, action, storeId, layout);
                }
            }
        } else {
            return new HeaderRow(name, false, numColumns);
        }
        return null;

    }

    private HeaderRow createHeaderRow(String name, String tag, boolean hasMore, GetStoreWidgets.WidgetDatalist.WidgetList.Action action, long storeId, String layout) {

        HeaderRow header = new HeaderRow(name, tag, hasMore, action.event.action, action.event.type, action.event.name, layout, numColumns, storeId == Defaults.DEFAULT_STORE_ID, storeId);
        header.FULL_ROW= fullRow;
        return header;
    }

}
