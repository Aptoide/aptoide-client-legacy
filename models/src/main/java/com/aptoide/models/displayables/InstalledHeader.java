package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 17/06/15.
 */
public class InstalledHeader extends Displayable {

    public InstalledHeader(int bucketSize) {
        super(bucketSize);
    }

    protected InstalledHeader(Parcel in) {
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

    public static final Creator<InstalledHeader> CREATOR = new Creator<InstalledHeader>() {
        @Override
        public InstalledHeader createFromParcel(Parcel in) {
            return new InstalledHeader(in);
        }

        @Override
        public InstalledHeader[] newArray(int size) {
            return new InstalledHeader[size];
        }
    };
}
