package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;

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