package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 17/06/15.
 */
public class UpdateRow extends AppItem {

    public String packageName;
    public String versionName;
    public String versionNameInstalled;
    public int versionCode;

    public UpdateRow(int bucketSize) {
        super(bucketSize);
    }

    protected UpdateRow(Parcel in) {
        super(in);
        packageName = in.readString();
        versionName = in.readString();
        versionNameInstalled = in.readString();
        versionCode = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(packageName);
        dest.writeString(versionName);
        dest.writeString(versionNameInstalled);
        dest.writeInt(versionCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpdateRow> CREATOR = new Creator<UpdateRow>() {
        @Override
        public UpdateRow createFromParcel(Parcel in) {
            return new UpdateRow(in);
        }

        @Override
        public UpdateRow[] newArray(int size) {
            return new UpdateRow[size];
        }
    };
}
