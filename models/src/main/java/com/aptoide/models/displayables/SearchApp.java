package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchApp extends Displayable implements Parcelable {

    public static final Creator<SearchApp> CREATOR = new Creator<SearchApp>() {
        @Override
        public SearchApp createFromParcel(Parcel in) {
            return new SearchApp(in);
        }

        @Override
        public SearchApp[] newArray(int size) {
            return new SearchApp[size];
        }
    };

    private boolean fromSubscribedStore;
    private int position;
    private String name;
    private String repo;
    private String packageName;
    private String versionName;
    private Integer versionCode;
    private String md5sum;
    private String timestamp;
    private Integer malwareRank;
    private String icon;
    private boolean otherVersions;
    private Number stars;
    private String repoTheme;
    private long downloads;

    public SearchApp(int bucketSize, boolean fromSubscribedStore, int position, String name, String repo, String packageName, String versionName, Integer versionCode, String md5sum, String timestamp, Integer malwareRank, String icon, boolean otherVersions, Number stars, String repoTheme, long downloads) {
        super(bucketSize);
        this.fromSubscribedStore = fromSubscribedStore;
        this.position = position;
        this.name = name;
        this.repo = repo;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.md5sum = md5sum;
        this.timestamp = timestamp;
        this.malwareRank = malwareRank;
        this.icon = icon;
        this.otherVersions = otherVersions;
        this.stars = stars;
        this.repoTheme = repoTheme;
        this.downloads = downloads;
    }

    protected SearchApp(Parcel in) {
        super(in);
        fromSubscribedStore = in.readByte() != 0;
        position = in.readInt();
        name = in.readString();
        repo = in.readString();
        packageName = in.readString();
        versionName = in.readString();
        versionCode = (Integer) in.readSerializable();
        md5sum = in.readString();
        timestamp = in.readString();
        malwareRank = (Integer) in.readSerializable();
        icon = in.readString();
        otherVersions = in.readByte() != 0;
        stars = (Number) in.readSerializable();
        repoTheme = in.readString();
        downloads = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (fromSubscribedStore ? 1 : 0));
        dest.writeInt(position);
        dest.writeString(name);
        dest.writeString(repo);
        dest.writeString(packageName);
        dest.writeString(versionName);
        dest.writeSerializable(versionCode);
        dest.writeString(md5sum);
        dest.writeString(timestamp);
        dest.writeSerializable(malwareRank);
        dest.writeString(icon);
        dest.writeByte((byte) (otherVersions ? 1 : 0));
        dest.writeSerializable(stars);
        dest.writeString(repoTheme);
        dest.writeLong(downloads);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isFromSubscribedStore() {
        return fromSubscribedStore;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getRepo() {
        return repo;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getMalwareRank() {
        return malwareRank;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isOtherVersions() {
        return otherVersions;
    }

    public Number getStars() {
        return stars;
    }

    public String getRepoTheme() {
        return repoTheme;
    }

    public long getDownloads() {
        return downloads;
    }

}