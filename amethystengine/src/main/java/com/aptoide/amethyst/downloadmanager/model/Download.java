package com.aptoide.amethyst.downloadmanager.model;

import java.io.Serializable;

import com.aptoide.amethyst.downloadmanager.DownloadInfoRunnable;
import com.aptoide.amethyst.downloadmanager.state.EnumState;

/**
 * Created by rmateus on 11-12-2013.
 */
public class Download implements Serializable {

    private String name;
    private String version;
    /** hashCode of the {@link #md5} */
    private long id;
    private int progress;
    private long size;
    private long timeLeft;
    private double speed;
    /** url of the icon */
    private String icon;
    /** md5 checksum of the apk */
    private String md5;
    private boolean paid;
    private String referrer;
    private String cpiUrl;
    private String packageName;
    private DownloadInfoRunnable parent;

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getCpiUrl() {
        return cpiUrl;
    }

    public void setCpiUrl(String cpiUrl) {
        this.cpiUrl = cpiUrl;
    }

    public DownloadInfoRunnable getParent() {
        return parent;
    }

    public void setParent(DownloadInfoRunnable parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public EnumState getDownloadState() {
        return parent.getStatusState().getEnumState();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Download) {
            return this.getId() == ((Download) o).getId();
        } else {
            return super.equals(o);
        }
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}