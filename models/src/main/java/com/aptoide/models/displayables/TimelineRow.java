package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rmateus on 04/06/15.
 */
public class TimelineRow extends Displayable {

    public String appIcon;
    public String appName;
    public String appFriend;
    public String userAvatar;

    public String repoName;
    public String md5sum;

    public TimelineRow(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }
}
