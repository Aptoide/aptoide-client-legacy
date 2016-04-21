package com.aptoide.models.displayables;

import com.aptoide.models.displayables.AbstractRow;
import com.aptoide.models.IHasMore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rmateus on 04/06/15.
 */
public class CategoryRow extends AbstractRow implements IHasMore {

    private String eventActionUrl;
    private String label;
    private String tag;
    private String graphic;
    private String eventType; // API, v3
    private String eventName; // listApps, getStore, getStoreWidgets, getApkComments
    private boolean homepage;
    private long storeId;
    private String layout;

    public CategoryRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    @Override
    public String getEventActionUrl() {
        return eventActionUrl;
    }

    public void setEventActionUrl(String eventActionUrl) {
        this.eventActionUrl = eventActionUrl;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public boolean isHasMore() {
        return true;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getLayout() {
        return layout;
    }

    @Override
    public boolean getHomepage() {
        return homepage;
    }

    @Override
    public long getStoreId() {
        return storeId;
    }

    public String getGraphic() {
        return graphic;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setGraphic(String graphic) {
        this.graphic = graphic;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setHomepage(boolean homepage) {
        this.homepage = homepage;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Override
    public int getSpanSize() {
        return spanSize;
    }
}
