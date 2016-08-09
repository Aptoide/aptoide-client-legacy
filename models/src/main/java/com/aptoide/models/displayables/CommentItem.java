package com.aptoide.models.displayables;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentItem extends Displayable {

    public Number votes;
    public Number id;
    public String username;
    public String timestamp;
    public String text;
    public String lang;
    public String appname;
    public String useravatar;
    public Number appid;
    public Number answerto;
    //0 if father, 1 if child and so on
    public int commentLevel;

    public CommentItem(@JsonProperty("BUCKETSIZE")int bucketSize) {
        super(bucketSize);
    }

    protected CommentItem(Parcel in) {
        super(in);
        votes = (Number) in.readSerializable();
        id = (Number) in.readSerializable();
        username = in.readString();
        timestamp = in.readString();
        text = in.readString();
        lang = in.readString();
        appname = in.readString();
        useravatar = in.readString();
        appid = (Number) in.readSerializable();
        answerto = (Number) in.readSerializable();
        commentLevel = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeSerializable(votes);
        dest.writeSerializable(id);
        dest.writeString(username);
        dest.writeString(timestamp);
        dest.writeString(text);
        dest.writeString(lang);
        dest.writeString(appname);
        dest.writeString(useravatar);
        dest.writeSerializable(appid);
        dest.writeSerializable(answerto);
        dest.writeInt(commentLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
        @Override
        public CommentItem createFromParcel(Parcel in) {
            return new CommentItem(in);
        }

        @Override
        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };
}
