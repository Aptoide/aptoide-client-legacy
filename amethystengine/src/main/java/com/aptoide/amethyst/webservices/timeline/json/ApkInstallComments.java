package com.aptoide.amethyst.webservices.timeline.json;

import com.aptoide.dataprovider.webservices.json.GenericResponseV2;

import java.util.List;

/**
 * Created by fabio on 14-10-2015.
 */
public class ApkInstallComments extends GenericResponseV2 {
    public List<Comment> getComments() {
        return comments;
    }
    List<Comment> comments;

    public static class Comment {
        public Number getId() {
            return id;
        }
        public String getUsername() {
            return username;
        }
        public String getText() {
            return text;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public boolean isOwned() {
            return owned;
        }

        Number id;

        String username;

        String text;

        String timestamp;

        boolean owned;

        public String getAvatar() {
            return avatar;
        }


        String avatar;
    }
}