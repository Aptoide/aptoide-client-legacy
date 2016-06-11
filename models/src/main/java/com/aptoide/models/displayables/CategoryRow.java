package com.aptoide.models.displayables;

import android.os.Parcel;

import com.aptoide.models.IHasMore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rmateus on 04/06/15.
 */
public class CategoryRow extends AbstractRow implements IHasMore {

    private String eventActionUrl;
    private String eventAltActionUrl;
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

    protected CategoryRow(Parcel in) {
        super(in);
        eventActionUrl = in.readString();
        eventAltActionUrl = in.readString();
        label = in.readString();
        tag = in.readString();
        graphic = in.readString();
        eventType = in.readString();
        eventName = in.readString();
        homepage = in.readByte() != 0;
        storeId = in.readLong();
        layout = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(eventActionUrl);
        dest.writeString(eventAltActionUrl);
        dest.writeString(label);
        dest.writeString(tag);
        dest.writeString(graphic);
        dest.writeString(eventType);
        dest.writeString(eventName);
        dest.writeByte((byte) (homepage ? 1 : 0));
        dest.writeLong(storeId);
        dest.writeString(layout);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryRow> CREATOR = new Creator<CategoryRow>() {
        @Override
        public CategoryRow createFromParcel(Parcel in) {
            return new CategoryRow(in);
        }

        @Override
        public CategoryRow[] newArray(int size) {
            return new CategoryRow[size];
        }
    };

    @Override
    public String getEventActionUrl() {
        return eventActionUrl;
    }

    public void setEventActionUrl(String eventActionUrl) {
        this.eventActionUrl = eventActionUrl;
    }

    @Override
    public String getAltEventActionUrl() {
        return eventAltActionUrl;
    }

    public void setEventAltActionUrl(String eventActionUrl) {
        this.eventAltActionUrl = eventActionUrl;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
