package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.displayables.Displayable;

/**
 * Created by neuro on 05-11-2015.
 */
public class SuggestedAppDisplayable extends Displayable {

    private ApkSuggestionJson apkSuggestionJson;
    private String label;
    private float size;
    private String description;
    private float rating;
    private String iconPath;
    private String store;

    public SuggestedAppDisplayable(ApkSuggestionJson apkSuggestionJson) {
        super(3);
        label = apkSuggestionJson.getAds().get(0).getData().name;
        size = apkSuggestionJson.getAds().get(0).getData().size.floatValue() / 1024 / 1024;
        description = apkSuggestionJson.getAds().get(0).getData().description;
        rating = apkSuggestionJson.getAds().get(0).getData().stars.floatValue();
        iconPath = apkSuggestionJson.getAds().get(0).getData().icon;
        store = apkSuggestionJson.getAds().get(0).getData().getRepo();
        this.apkSuggestionJson = apkSuggestionJson;
    }

    protected SuggestedAppDisplayable(Parcel in) {
        super(in);
        label = in.readString();
        size = in.readFloat();
        description = in.readString();
        rating = in.readFloat();
        iconPath = in.readString();
        store = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(label);
        dest.writeFloat(size);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeString(iconPath);
        dest.writeString(store);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SuggestedAppDisplayable> CREATOR = new Creator<SuggestedAppDisplayable>() {
        @Override
        public SuggestedAppDisplayable createFromParcel(Parcel in) {
            return new SuggestedAppDisplayable(in);
        }

        @Override
        public SuggestedAppDisplayable[] newArray(int size) {
            return new SuggestedAppDisplayable[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public float getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public String getIconPath() {
        return iconPath;
    }

    public ApkSuggestionJson getApkSuggestionJson() {
        return apkSuggestionJson;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

}
