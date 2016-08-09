package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 20-10-2015.
 */
public class NoCommentPlaceHolderRow extends Displayable {

    public NoCommentPlaceHolderRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected NoCommentPlaceHolderRow(Parcel in) {
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

    public static final Creator<NoCommentPlaceHolderRow> CREATOR = new Creator<NoCommentPlaceHolderRow>() {
        @Override
        public NoCommentPlaceHolderRow createFromParcel(Parcel in) {
            return new NoCommentPlaceHolderRow(in);
        }

        @Override
        public NoCommentPlaceHolderRow[] newArray(int size) {
            return new NoCommentPlaceHolderRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}