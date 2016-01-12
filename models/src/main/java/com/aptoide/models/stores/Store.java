package com.aptoide.models.stores;

/**
 * Created by rmateus on 17/06/15.
 */
public class Store {

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