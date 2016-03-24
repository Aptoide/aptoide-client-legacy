package com.aptoide.models.displayables;

import com.aptoide.models.IHasMore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmateus on 19/06/15.
 */
public class EditorsChoiceRow extends Displayable implements IHasMore {

    private boolean hasMore;
    private String eventActionUrl;
    private String tag;
    private String label;
    private String eventType;
    private String eventName;
    private String layout;
    private boolean homepage;
    private long storeId;

    public List<AppItem> appItemList = new ArrayList<>();

    public EditorsChoiceRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    public EditorsChoiceRow(int bucketSize, boolean hasMore, String eventActionUrl, String label, String tag, String eventType, String eventName, String layout, boolean homepage, long storeId) {
        super(bucketSize);
        this.hasMore = hasMore;
        this.eventActionUrl = eventActionUrl;
        this.label = label;
        this.tag = tag;
        this.eventType = eventType;
        this.eventName = eventName;
        this.layout = layout;
        this.homepage = homepage;
        this.storeId = storeId;
    }

    @Override
    public int getSpanSize() {
        return super.getSpanSize();
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
        return hasMore;
    }

    @Override
    public String getEventActionUrl() {
        return eventActionUrl;
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
}
