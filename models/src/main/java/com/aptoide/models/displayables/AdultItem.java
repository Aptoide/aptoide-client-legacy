package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by hsousa on 12-10-2015.
 */
public class AdultItem extends Displayable {

    public AdultItem(int bucketSize) {
        super(bucketSize);
    }

    protected AdultItem(Parcel in) {
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

    public static final Creator<AdultItem> CREATOR = new Creator<AdultItem>() {
        @Override
        public AdultItem createFromParcel(Parcel in) {
            return new AdultItem(in);
        }

        @Override
        public AdultItem[] newArray(int size) {
            return new AdultItem[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}
