package com.aptoide.models.displayables;

import com.aptoide.models.IHasMore;
import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by rmateus on 04/06/15.
 */
public class HeaderRow extends Displayable implements IHasMore {

    private final String label;
    private final String tag;
    private final boolean hasMore;
    private final String eventActionUrl;
    private final String eventType;
    private final String eventName;
    private final String layout;

    @JsonIgnore    public String theme;
    @JsonIgnore    public boolean homepage;
    @JsonIgnore    public long storeId;

    public HeaderRow(String label, String tag, boolean hasMore, String eventActionUrl, String eventType, String eventName, String layout, int bucketSize, boolean homepage, long storeId) {
        super(bucketSize);
        this.label = label;
        this.tag = tag;
        this.hasMore = hasMore;
        this.eventActionUrl = eventActionUrl;
        this.eventType = eventType;
        this.eventName = eventName;
        this.layout = layout;
        this.homepage = homepage;
        this.storeId = storeId;
    }

    public HeaderRow(String label, boolean hasMore, String eventName, int bucketSize, boolean homepage, long storeId) {
        this(label, "", hasMore, "", "", eventName, "", bucketSize, homepage, storeId);
    }

    /**
     *€
     * @param label
     * @param hasMore
     * @param bucketSize
     */
    public HeaderRow(String label, boolean hasMore, int bucketSize) {
        this(label, hasMore, "", bucketSize, false, -1);
    }


    // Needed for Jackson deserialization
    public HeaderRow() {
        super(0);
        this.eventName = "";
        this.label = "";
        this.tag = "";
        this.hasMore = false;
        this.eventActionUrl = "";
        this.eventType = null;
        this.layout = "";
    }


    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getTag() {
        return tag;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public String getEventActionUrl() {
        return eventActionUrl;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    public String getLayout() {
        return layout;
    }

    public String getTheme() {
        return theme;
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
