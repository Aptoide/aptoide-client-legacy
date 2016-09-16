package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class to hold data used to display an AdItem
 *
 * Created by hsousa on 29/09/15.
 */
public class AdItem extends AppItem {

    public String cpcUrl;
    public String cpiUrl;
    public String cpdUrl;
    public String partnerName;
    public String partnerClickUrl;
    public long adId;

    public AdItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected AdItem(Parcel in) {
        super(in);
        cpcUrl = in.readString();
        cpiUrl = in.readString();
        cpdUrl = in.readString();
        partnerName = in.readString();
        partnerClickUrl = in.readString();
        adId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(cpcUrl);
        dest.writeString(cpiUrl);
        dest.writeString(cpdUrl);
        dest.writeString(partnerName);
        dest.writeString(partnerClickUrl);
        dest.writeLong(adId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdItem> CREATOR = new Creator<AdItem>() {
        @Override
        public AdItem createFromParcel(Parcel in) {
            return new AdItem(in);
        }

        @Override
        public AdItem[] newArray(int size) {
            return new AdItem[size];
        }
    };
}