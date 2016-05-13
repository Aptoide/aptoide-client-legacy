package com.aptoide.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 15-07-2013
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class ApkPermission implements Parcelable{

    private String name;
    private String description;

    public ApkPermission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
