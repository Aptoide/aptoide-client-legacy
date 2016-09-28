package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 25/09/15.
 */
public class TimeLinePlaceHolderRow extends Displayable {

    public TimeLinePlaceHolderRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected TimeLinePlaceHolderRow(Parcel in) {
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

    public static final Creator<TimeLinePlaceHolderRow> CREATOR = new Creator<TimeLinePlaceHolderRow>() {
        @Override
        public TimeLinePlaceHolderRow createFromParcel(Parcel in) {
            return new TimeLinePlaceHolderRow(in);
        }

        @Override
        public TimeLinePlaceHolderRow[] newArray(int size) {
            return new TimeLinePlaceHolderRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}
