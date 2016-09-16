package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 03-11-2015.
 */
public class MoreVersionsItem extends AppItem {
    public MoreVersionsItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected MoreVersionsItem(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MoreVersionsItem> CREATOR = new Creator<MoreVersionsItem>() {
        @Override
        public MoreVersionsItem createFromParcel(Parcel in) {
            return new MoreVersionsItem(in);
        }

        @Override
        public MoreVersionsItem[] newArray(int size) {
            return new MoreVersionsItem[size];
        }
    };
}
