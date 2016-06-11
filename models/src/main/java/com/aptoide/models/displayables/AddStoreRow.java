package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 23-06-2015.
 */
public class AddStoreRow extends Displayable {

    public AddStoreRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected AddStoreRow(Parcel in) {
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

    public static final Creator<AddStoreRow> CREATOR = new Creator<AddStoreRow>() {
        @Override
        public AddStoreRow createFromParcel(Parcel in) {
            return new AddStoreRow(in);
        }

        @Override
        public AddStoreRow[] newArray(int size) {
            return new AddStoreRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return BUCKETSIZE;
    }
}
