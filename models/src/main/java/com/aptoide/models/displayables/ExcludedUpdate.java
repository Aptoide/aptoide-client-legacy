package com.aptoide.models.displayables;

import android.os.Parcel;

/**
 * Created by hsousa on 26-06-2015.
 */
public class ExcludedUpdate extends Displayable {

    private String name;
    private int vercode;

    private String versionName;
    private String apkid;
    private boolean checked;
    private String icon;

    public ExcludedUpdate(int bucketSize) {
        super(bucketSize);
    }

    public ExcludedUpdate(String name, String apkid, String icon, int vercode, String versionName, int bucketSize) {
        super(bucketSize);
        this.name = name;
        this.apkid = apkid;
        this.vercode = vercode;
        this.icon = icon;
        this.versionName = versionName;
    }

    protected ExcludedUpdate(Parcel in) {
        super(in);
        name = in.readString();
        vercode = in.readInt();
        versionName = in.readString();
        apkid = in.readString();
        checked = in.readByte() != 0;
        icon = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeInt(vercode);
        dest.writeString(versionName);
        dest.writeString(apkid);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExcludedUpdate> CREATOR = new Creator<ExcludedUpdate>() {
        @Override
        public ExcludedUpdate createFromParcel(Parcel in) {
            return new ExcludedUpdate(in);
        }

        @Override
        public ExcludedUpdate[] newArray(int size) {
            return new ExcludedUpdate[size];
        }
    };

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public int getVercode() {
        return vercode;
    }

    public String getApkid() {
        return apkid;
    }

    public String getVersionName() {
        return versionName;
    }

    public String toString() {
        return "Name: " + name + ", vercode: " + vercode + ", apkid: " + apkid;
    }

    public String getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVercode(int vercode) {
        this.vercode = vercode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}