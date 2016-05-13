package com.aptoide.dataprovider.webservices.json.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by rmateus on 23-02-2015.
 */

public class Review {

    public Integer id;
    @JsonProperty("repo_id")
    public Integer repoId;
    public Integer performance;
    public Integer stability;
    public Integer usability;
    public Integer addiction;
    public String status;
    @JsonProperty("reponame")
    public String repoName;
    public ReviewApk apk;
    public User user;
    @JsonProperty("added_timestamp")
    public String addedTimestamp;
    @JsonProperty("updated_timestamp")
    public String updatedTimestamp;
    public Number average;
    public String title;
    public List<String> pros;
    public List<String> cons;
    @JsonProperty("final_verdict")
    public String finalVerdict;
    public String lang;

    public Review() {}

    public String getAddedTimestamp() {
        return addedTimestamp;
    }

    public Integer getPerformance() {
        return performance;
    }

    public Integer getAddiction() {
        return addiction;
    }

    public Integer getUsability() {
        return usability;
    }

    public Integer getStability() {
        return stability;
    }

    public ReviewApk getApk() {
        return apk;
    }

    public String getFinalVerdict() {
        return finalVerdict;
    }

    public User getUser() {
        return user;
    }

    public List<String> getPros() {
        return pros;
    }

    public List<String> getCons() {
        return cons;
    }

    public static class User {
        public Number id;   // in the getReview but not on getReviewList
        public String name;
        public String avatar;

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return avatar;
        }
    }

    public static class ReviewApk {
        public Number id;
        @JsonProperty("package")
        public String packageName;
        public Number vercode;
        public String vername;
        public String title; // in the getReviewList but not on getReview
        public String icon;  // in the getReviewList but not on getReview
        public String name;
        public String avatar;
        public List<Screenshots> screenshots;

        public String getVername() {
            return vername;
        }

        public String getTitle() {
            return title;
        }

        public List<Screenshots> getScreenshots() {
            return screenshots;
        }

        public String getIcon() {
            return icon;
        }

        public Number getId() {
            return id;
        }


        public static class Screenshots {
            public String url;
            public String orient;

            public String getUrl() {
                return url;
            }
        }
    }


    /**
     * Needed because average value is only coming in the getReviewList webservice
     * @return
     */
    @JsonIgnore
    public float getRating() {
        return Math.round((performance * 10 + usability * 10 + addiction * 10 + stability * 10) / 4.0f) / 10f;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", repoId=" + repoId +
                ", performance=" + performance +
                ", stability=" + stability +
                ", usability=" + usability +
                ", addiction=" + addiction +
                ", status='" + status + '\'' +
                ", average='" + average + '\'' +
                ", apk=" + apk +
                ", user=" + user +
                ", addedTimestamp='" + addedTimestamp + '\'' +
                ", updatedTimestamp='" + updatedTimestamp + '\'' +
                ", title='" + title + '\'' +
                ", finalVerdict='" + finalVerdict + '\'' +
                ", lang='" + lang + '\'' +
                '}';

    }
}