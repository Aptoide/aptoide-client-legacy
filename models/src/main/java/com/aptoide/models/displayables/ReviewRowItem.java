package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hsousa on 24/09/15.
 */
public class ReviewRowItem extends Displayable {
    public String appIcon;
    public String appName;
    public String description;
    public float rating;
    public String reviewer;
    public String avatar;
    public Integer reviewId;

    public ReviewRowItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }

    protected ReviewRowItem(Parcel in) {
        super(in);
        appIcon = in.readString();
        appName = in.readString();
        description = in.readString();
        rating = in.readFloat();
        reviewer = in.readString();
        avatar = in.readString();
        reviewId = (Integer) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(appIcon);
        dest.writeString(appName);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeString(reviewer);
        dest.writeString(avatar);
        dest.writeSerializable(reviewId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReviewRowItem> CREATOR = new Creator<ReviewRowItem>() {
        @Override
        public ReviewRowItem createFromParcel(Parcel in) {
            return new ReviewRowItem(in);
        }

        @Override
        public ReviewRowItem[] newArray(int size) {
            return new ReviewRowItem[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }
}
