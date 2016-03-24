package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
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
}
