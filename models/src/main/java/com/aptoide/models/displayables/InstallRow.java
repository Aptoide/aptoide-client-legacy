package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by rmateus on 17/06/15.
 */
public class InstallRow extends AppItem {

    public String versionName;
    public long firstInstallTime;

    public InstallRow(int bucketSize) {
        super(bucketSize);
    }

    protected InstallRow(Parcel in) {
        super(in);
        versionName = in.readString();
        firstInstallTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(versionName);
        dest.writeLong(firstInstallTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InstallRow> CREATOR = new Creator<InstallRow>() {
        @Override
        public InstallRow createFromParcel(Parcel in) {
            return new InstallRow(in);
        }

        @Override
        public InstallRow[] newArray(int size) {
            return new InstallRow[size];
        }
    };
}
