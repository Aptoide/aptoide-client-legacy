package com.aptoide.models.stores;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rmateus on 17/06/15.
 */
public class Store implements Parcelable {

    private String name;
    private String downloads;
    private String avatar;
    private String description;
    private String theme;
    private String view;
    private String items;
    private String baseUrl;
    private long id;
    private Login login;
    private long topTimestamp = 0;

    public Store() {

    }

    protected Store(Parcel in) {
        name = in.readString();
        downloads = in.readString();
        avatar = in.readString();
        description = in.readString();
        theme = in.readString();
        view = in.readString();
        items = in.readString();
        baseUrl = in.readString();
        id = in.readLong();
        login = in.readParcelable(Login.class.getClassLoader());
        topTimestamp = in.readLong();
        latestTimestamp = in.readLong();
        delta = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(downloads);
        dest.writeString(avatar);
        dest.writeString(description);
        dest.writeString(theme);
        dest.writeString(view);
        dest.writeString(items);
        dest.writeString(baseUrl);
        dest.writeLong(id);
        dest.writeParcelable(login, flags);
        dest.writeLong(topTimestamp);
        dest.writeLong(latestTimestamp);
        dest.writeString(delta);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public long getTopTimestamp() {
        return topTimestamp;
    }

    public long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }

    private long latestTimestamp = 0;
    private String delta;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLatestXmlUrl() {
        return baseUrl + "latest.xml";
    }

    public String getTopXmlUrl() {
        return baseUrl + "top.xml";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getItems() {
        return items;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public void setTopTimestamp(long topTimestamp) {
        this.topTimestamp = topTimestamp;
    }
}