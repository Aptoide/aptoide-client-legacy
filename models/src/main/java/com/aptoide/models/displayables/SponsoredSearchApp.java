package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

import com.aptoide.models.ApkSuggestionJson;

/**
 * Created by neuro on 05-11-2015.
 */
public class SponsoredSearchApp extends Displayable implements Parcelable {

    private long adId;
    private String cpcUrl;
    private String cpiUrl;
    private String partnerName;
    private String partnerClickUrl;
    private String description;
    private Number downloads;
    private String icon;
    private Number id;
    private String md5sum;
    private String name;
    private String packageName;
    private String repo;
    private Number versionCode;
    private String versionName;

    public SponsoredSearchApp(int bucketSize, long adId, String cpcUrl, String cpiUrl, String partnerName, String partnerClickUrl, String description, Number downloads, String icon, Number id, String md5sum, String name, String packageName, String repo, Number versionCode, String versionName) {
        super(bucketSize);
        this.adId = adId;
        this.cpcUrl = cpcUrl;
        this.cpiUrl = cpiUrl;
        this.partnerName = partnerName;
        this.partnerClickUrl = partnerClickUrl;
        this.description = description;
        this.downloads = downloads;
        this.icon = icon;
        this.id = id;
        this.md5sum = md5sum;
        this.name = name;
        this.packageName = packageName;
        this.repo = repo;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    protected SponsoredSearchApp(Parcel in) {
        super(in);
        adId = in.readLong();
        cpcUrl = in.readString();
        cpiUrl = in.readString();
        partnerName = in.readString();
        partnerClickUrl = in.readString();
        description = in.readString();
        downloads = (Number) in.readSerializable();
        id = (Number) in.readSerializable();
        icon = in.readString();
        md5sum = in.readString();
        name = in.readString();
        packageName = in.readString();
        repo = in.readString();
        versionCode = (Number) in.readSerializable();
        versionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(adId);
        dest.writeString(cpcUrl);
        dest.writeString(cpiUrl);
        dest.writeString(partnerName);
        dest.writeString(partnerClickUrl);
        dest.writeString(description);
        dest.writeSerializable(downloads);
        dest.writeSerializable(id);
        dest.writeString(icon);
        dest.writeString(md5sum);
        dest.writeString(name);
        dest.writeString(packageName);
        dest.writeString(repo);
        dest.writeSerializable(versionCode);
        dest.writeString(versionName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SponsoredSearchApp> CREATOR = new Creator<SponsoredSearchApp>() {
        @Override
        public SponsoredSearchApp createFromParcel(Parcel in) {
            return new SponsoredSearchApp(in);
        }

        @Override
        public SponsoredSearchApp[] newArray(int size) {
            return new SponsoredSearchApp[size];
        }
    };

    public long getAdId() {
        return adId;
    }

    public String getCpcUrl() {
        return cpcUrl;
    }

    public String getCpiUrl() {
        return cpiUrl;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerClickUrl() {
        return partnerClickUrl;
    }

    public String getDescription() {
        return description;
    }

    public Number getDownloads() {
        return downloads;
    }

    public String getIcon() {
        return icon;
    }

    public Number getId() {
        return id;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getRepo() {
        return repo;
    }

    public Number getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }
}
