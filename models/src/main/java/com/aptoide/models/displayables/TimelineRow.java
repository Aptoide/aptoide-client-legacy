package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rmateus on 04/06/15.
 */
public class TimelineRow extends Displayable {

    public String appIcon;
    public String appName;
    public String appFriend;
    public String userAvatar;

    public String repoName;
    public String md5sum;

    public TimelineRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected TimelineRow(Parcel in) {
        super(in);
        appIcon = in.readString();
        appName = in.readString();
        appFriend = in.readString();
        userAvatar = in.readString();
        repoName = in.readString();
        md5sum = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(appIcon);
        dest.writeString(appName);
        dest.writeString(appFriend);
        dest.writeString(userAvatar);
        dest.writeString(repoName);
        dest.writeString(md5sum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimelineRow> CREATOR = new Creator<TimelineRow>() {
        @Override
        public TimelineRow createFromParcel(Parcel in) {
            return new TimelineRow(in);
        }

        @Override
        public TimelineRow[] newArray(int size) {
            return new TimelineRow[size];
        }
    };
}
