package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 08-10-2015.
 */
public class HomeStoreItem extends Displayable {

    public long id;
    public String repoName;
    public String avatar;
    public String added;
    public String modified;

    public String description;
    public String theme;
    public String view;

    public long storeApps;
    public long storeDwnNumber;
    public long storeSubscribers;

    public HomeStoreItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected HomeStoreItem(Parcel in) {
        super(in);
        id = in.readLong();
        repoName = in.readString();
        avatar = in.readString();
        added = in.readString();
        modified = in.readString();
        description = in.readString();
        theme = in.readString();
        view = in.readString();
        storeApps = in.readLong();
        storeDwnNumber = in.readLong();
        storeSubscribers = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(repoName);
        dest.writeString(avatar);
        dest.writeString(added);
        dest.writeString(modified);
        dest.writeString(description);
        dest.writeString(theme);
        dest.writeString(view);
        dest.writeLong(storeApps);
        dest.writeLong(storeDwnNumber);
        dest.writeLong(storeSubscribers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HomeStoreItem> CREATOR = new Creator<HomeStoreItem>() {
        @Override
        public HomeStoreItem createFromParcel(Parcel in) {
            return new HomeStoreItem(in);
        }

        @Override
        public HomeStoreItem[] newArray(int size) {
            return new HomeStoreItem[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW / 2;
    }
}
