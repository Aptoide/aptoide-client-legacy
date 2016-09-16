package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 17/06/15.
 */
public class UpdatesHeader extends HeaderRow {
    public UpdatesHeader(String name, boolean hasMore, int bucketSize) {
        super(name, hasMore, bucketSize);
    }

    protected UpdatesHeader(Parcel in) {
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

    public static final Creator<UpdatesHeader> CREATOR = new Creator<UpdatesHeader>() {
        @Override
        public UpdatesHeader createFromParcel(Parcel in) {
            return new UpdatesHeader(in);
        }

        @Override
        public UpdatesHeader[] newArray(int size) {
            return new UpdatesHeader[size];
        }
    };
}
