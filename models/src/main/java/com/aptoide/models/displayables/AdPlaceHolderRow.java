package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 25/09/15.
 */
public class AdPlaceHolderRow extends Displayable {

    public AdPlaceHolderRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected AdPlaceHolderRow(Parcel in) {
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

    public static final Creator<AdPlaceHolderRow> CREATOR = new Creator<AdPlaceHolderRow>() {
        @Override
        public AdPlaceHolderRow createFromParcel(Parcel in) {
            return new AdPlaceHolderRow(in);
        }

        @Override
        public AdPlaceHolderRow[] newArray(int size) {
            return new AdPlaceHolderRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}
