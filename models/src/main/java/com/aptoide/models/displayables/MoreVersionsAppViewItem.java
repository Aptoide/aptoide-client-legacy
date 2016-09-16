package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 03-11-2015.
 */
public class MoreVersionsAppViewItem extends Displayable {

    public long id;
    public String appName;
    public String packageName;
    public String versionName;
    public String icon;
    public String storeName;
    public String storeAvatar;
    public String storeTheme;
    public long storeId;
    public int versionCode;


    public MoreVersionsAppViewItem(@JsonProperty("BUCKETSIZE") int numColumns) {
        super(numColumns);
        FULL_ROW = numColumns;
        setSpanSize(1);
    }

    protected MoreVersionsAppViewItem(Parcel in) {
        super(in);
        id = in.readLong();
        appName = in.readString();
        packageName = in.readString();
        versionName = in.readString();
        icon = in.readString();
        storeName = in.readString();
        storeAvatar = in.readString();
        storeTheme = in.readString();
        storeId = in.readLong();
        versionCode = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeString(versionName);
        dest.writeString(icon);
        dest.writeString(storeName);
        dest.writeString(storeAvatar);
        dest.writeString(storeTheme);
        dest.writeLong(storeId);
        dest.writeInt(versionCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MoreVersionsAppViewItem> CREATOR = new Creator<MoreVersionsAppViewItem>() {
        @Override
        public MoreVersionsAppViewItem createFromParcel(Parcel in) {
            return new MoreVersionsAppViewItem(in);
        }

        @Override
        public MoreVersionsAppViewItem[] newArray(int size) {
            return new MoreVersionsAppViewItem[size];
        }
    };

    @Override
    public int getSpanSize() {
        return 1;
    }
}
