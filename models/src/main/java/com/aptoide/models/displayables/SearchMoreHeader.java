package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 28/06/15.
 */
public class SearchMoreHeader extends Displayable {
    public SearchMoreHeader(int bucketSize) {
        super(bucketSize);
    }

    protected SearchMoreHeader(Parcel in) {
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

    public static final Creator<SearchMoreHeader> CREATOR = new Creator<SearchMoreHeader>() {
        @Override
        public SearchMoreHeader createFromParcel(Parcel in) {
            return new SearchMoreHeader(in);
        }

        @Override
        public SearchMoreHeader[] newArray(int size) {
            return new SearchMoreHeader[size];
        }
    };
}
