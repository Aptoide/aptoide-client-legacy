package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class to hold data used to display an AppItem
 *
 * Created by rmateus on 04/06/15.
 */
public class AppItem extends Displayable {

    public long id;
    public String appName;
    public String packageName;
    public long fileSize;
    public String icon;
    public String featuredGraphic;
    public String uptype;
    public String path;
    public String path_alt;
    public long storeId;
    public String storeName;
    public String versionName;
    public String versionCode;
    public String md5sum;
    public long downloads;
    public float rating;
    public String modified;
    public String updated;
    public String category;
    public String bundleCateg;
    public String bundleSubCateg;

    public AppItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected AppItem(Parcel in) {
        super(in);
        id = in.readLong();
        appName = in.readString();
        packageName = in.readString();
        fileSize = in.readLong();
        icon = in.readString();
        featuredGraphic = in.readString();
        uptype = in.readString();
        path = in.readString();
        path_alt = in.readString();
        storeId = in.readLong();
        storeName = in.readString();
        versionName = in.readString();
        versionCode = in.readString();
        md5sum = in.readString();
        downloads = in.readLong();
        rating = in.readFloat();
        modified = in.readString();
        updated = in.readString();
        category = in.readString();
        bundleCateg = in.readString();
        bundleSubCateg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeLong(fileSize);
        dest.writeString(icon);
        dest.writeString(featuredGraphic);
        dest.writeString(uptype);
        dest.writeString(path);
        dest.writeString(path_alt);
        dest.writeLong(storeId);
        dest.writeString(storeName);
        dest.writeString(versionName);
        dest.writeString(versionCode);
        dest.writeString(md5sum);
        dest.writeLong(downloads);
        dest.writeFloat(rating);
        dest.writeString(modified);
        dest.writeString(updated);
        dest.writeString(category);
        dest.writeString(bundleCateg);
        dest.writeString(bundleSubCateg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
        @Override
        public AppItem createFromParcel(Parcel in) {
            return new AppItem(in);
        }

        @Override
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };
}