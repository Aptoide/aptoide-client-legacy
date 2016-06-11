package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 20-10-2015.
 */
public class CommentPlaceHolderRow extends Displayable {

    public CommentPlaceHolderRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected CommentPlaceHolderRow(Parcel in) {
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

    public static final Creator<CommentPlaceHolderRow> CREATOR = new Creator<CommentPlaceHolderRow>() {
        @Override
        public CommentPlaceHolderRow createFromParcel(Parcel in) {
            return new CommentPlaceHolderRow(in);
        }

        @Override
        public CommentPlaceHolderRow[] newArray(int size) {
            return new CommentPlaceHolderRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}