package com.aptoide.models.displayables;

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

    @Deprecated
    public SuggestedAppDisplayable(int bucketSize) {
        super(bucketSize);
    }

    @Deprecated
    public SuggestedAppDisplayable(int bucketSize, String label, float size, String description, float rating, String iconPath) {
        super(bucketSize);
        this.label = label;
        this.size = size;
        this.description = description;
        this.rating = rating;
        this.iconPath = iconPath;
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public ApkSuggestionJson getApkSuggestionJson() {
        return apkSuggestionJson;
    }

    public void setApkSuggestionJson(ApkSuggestionJson apkSuggestionJson) {
        this.apkSuggestionJson = apkSuggestionJson;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
