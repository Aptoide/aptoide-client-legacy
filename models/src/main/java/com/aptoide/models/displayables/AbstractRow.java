package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 04/06/15.
 */
public abstract class AbstractRow extends Displayable {

    public AbstractRow(int bucketSize) {
        super(bucketSize);
    }

    protected AbstractRow(Parcel in) {
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
}
