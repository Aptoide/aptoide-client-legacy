package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 25/09/15.
 */
public class ReviewPlaceHolderRow extends Displayable {

    public ReviewPlaceHolderRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected ReviewPlaceHolderRow(Parcel in) {
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

    public static final Creator<ReviewPlaceHolderRow> CREATOR = new Creator<ReviewPlaceHolderRow>() {
        @Override
        public ReviewPlaceHolderRow createFromParcel(Parcel in) {
            return new ReviewPlaceHolderRow(in);
        }

        @Override
        public ReviewPlaceHolderRow[] newArray(int size) {
            return new ReviewPlaceHolderRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}
