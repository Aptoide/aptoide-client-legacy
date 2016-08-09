package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 04/06/15.
 */
public class UpdateHeaderRow extends HeaderRow {

    public UpdateHeaderRow(String name, boolean hasMore, int bucketSize) {
        super(name, hasMore, bucketSize);
    }

    protected UpdateHeaderRow(Parcel in) {
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

    public static final Creator<UpdateHeaderRow> CREATOR = new Creator<UpdateHeaderRow>() {
        @Override
        public UpdateHeaderRow createFromParcel(Parcel in) {
            return new UpdateHeaderRow(in);
        }

        @Override
        public UpdateHeaderRow[] newArray(int size) {
            return new UpdateHeaderRow[size];
        }
    };
}
