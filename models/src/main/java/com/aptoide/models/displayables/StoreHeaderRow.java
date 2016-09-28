package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by hsousa on 13/08/15.
 */
public class StoreHeaderRow extends Displayable {

    public long id;
    public String name;
    public String avatar;
    public String description;
    public long apps;
    public long subscribers;
    public long downloads;

    public StoreHeaderRow(int bucketSize) {
        super(bucketSize);
    }

    protected StoreHeaderRow(Parcel in) {
        super(in);
        id = in.readLong();
        name = in.readString();
        avatar = in.readString();
        description = in.readString();
        apps = in.readLong();
        subscribers = in.readLong();
        downloads = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(description);
        dest.writeLong(apps);
        dest.writeLong(subscribers);
        dest.writeLong(downloads);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoreHeaderRow> CREATOR = new Creator<StoreHeaderRow>() {
        @Override
        public StoreHeaderRow createFromParcel(Parcel in) {
            return new StoreHeaderRow(in);
        }

        @Override
        public StoreHeaderRow[] newArray(int size) {
            return new StoreHeaderRow[size];
        }
    };

    public int getSpanSize() {
        return FULL_ROW;
    }

}
