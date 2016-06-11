package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

import com.aptoide.models.IHasMore;
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
    public boolean homepage;
    @JsonIgnore    public long storeId;
    public String bundleCategory;

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

    public HeaderRow(String label, String tag, boolean hasMore, String eventName, int bucketSize, boolean homepage, long storeId) {
        this(label, tag, hasMore, "", "", eventName, "", bucketSize, homepage, storeId);
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


    protected HeaderRow(Parcel in) {
        super(in);
        label = in.readString();
        tag = in.readString();
        hasMore = in.readByte() != 0;
        eventActionUrl = in.readString();
        eventType = in.readString();
        eventName = in.readString();
        layout = in.readString();
        theme = in.readString();
        homepage = in.readByte() != 0;
        storeId = in.readLong();
        bundleCategory = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(label);
        dest.writeString(tag);
        dest.writeByte((byte) (hasMore ? 1 : 0));
        dest.writeString(eventActionUrl);
        dest.writeString(eventType);
        dest.writeString(eventName);
        dest.writeString(layout);
        dest.writeString(theme);
        dest.writeByte((byte) (homepage ? 1 : 0));
        dest.writeLong(storeId);
        dest.writeString(bundleCategory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HeaderRow> CREATOR = new Creator<HeaderRow>() {
        @Override
        public HeaderRow createFromParcel(Parcel in) {
            return new HeaderRow(in);
        }

        @Override
        public HeaderRow[] newArray(int size) {
            return new HeaderRow[size];
        }
    };

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

    @Override
    public String getAltEventActionUrl() {
        return null;
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
