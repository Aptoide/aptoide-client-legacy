package com.aptoide.amethyst.webservices.json;

import java.util.List;

/**
 * Created by fabio on 09-11-2015.
 */
public class TimelineActivityJson {
    public String status;

    public ActivityGroup new_installs;

    public ActivityGroup owned_activity;

    public ActivityGroup related_activity;

    public String getStatus() {
        return status;
    }

    public ActivityGroup getNew_installs() {
        return new_installs;
    }

    public ActivityGroup getOwned_activity() {
        return owned_activity;
    }

    public ActivityGroup getRelated_activity() {
        return related_activity;
    }

    public static class ActivityGroup{


        public List<Friend> friends;

        public Number total;

        public Number total_likes;

        public Number total_comments;

        public List<Friend> getFriends() {
            return friends;
        }

        public Number getTotal() {
            return total;
        }

        public Number getTotal_likes() {
            return total_likes;
        }

        public Number getTotal_comments() {
            return total_comments;
        }
    }


    public static class Friend{

        public String avatar;

        public String getAvatar() {
            return avatar;
        }
    }

}
