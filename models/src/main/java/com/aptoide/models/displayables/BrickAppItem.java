package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Editor's choice displayable.
 *
 * Created by rmateus on 04/06/15.
 */
public class BrickAppItem extends AppItem {

    public BrickAppItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected BrickAppItem(Parcel in) {
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

    public static final Creator<BrickAppItem> CREATOR = new Creator<BrickAppItem>() {
        @Override
        public BrickAppItem createFromParcel(Parcel in) {
            return new BrickAppItem(in);
        }

        @Override
        public BrickAppItem[] newArray(int size) {
            return new BrickAppItem[size];
        }
    };
}