package com.aptoide.amethyst.webservices.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rmateus on 16-02-2015.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
        "status",
        "subscription"
})
public class GetUserRepoSubscriptionJson {

    @JsonProperty("status")
    private String status;
    @JsonProperty("subscription")
    private List<RepoInfo> subscription = new ArrayList<RepoInfo>();

    /**
     * @return The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The subscription
     */
    @JsonProperty("subscription")
    public List<RepoInfo> getSubscription() {
        return subscription;
    }

    /**
     * @param subscription The subscription
     */
    @JsonProperty("subscription")
    public void setSubscription(List<RepoInfo> subscription) {
        this.subscription = subscription;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)

    @JsonPropertyOrder({
            "name",
            "avatar",
            "downloads",
            "theme",
            "description",
            "items",
            "view",
            "avatar_hd"
    })
    public static class RepoInfo {

        @JsonProperty("id")
        private Number id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("avatar")
        private String avatar;
        @JsonProperty("downloads")
        private String downloads;
        @JsonProperty("theme")
        private String theme;
        @JsonProperty("description")
        private String description;
        @JsonProperty("items")
        private String items;
        @JsonProperty("view")
        private String view;
        @JsonProperty("avatar_hd")
        private String avatarHd;

        /**
         * @return The name
         */
        @JsonProperty("name")
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return The avatar
         */
        @JsonProperty("avatar")
        public String getAvatar() {

            if (avatarHd != null) {
                return avatarHd;
            }

            return avatar;
        }

        /**
         * @param avatar The avatar
         */
        @JsonProperty("avatar")
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        /**
         * @return The downloads
         */
        @JsonProperty("downloads")
        public String getDownloads() {
            return downloads;
        }

        /**
         * @param downloads The downloads
         */
        @JsonProperty("downloads")
        public void setDownloads(String downloads) {
            this.downloads = downloads;
        }

        /**
         * @return The theme
         */
        @JsonProperty("theme")
        public String getTheme() {
            return theme;
        }

        /**
         * @param theme The theme
         */
        @JsonProperty("theme")
        public void setTheme(String theme) {
            this.theme = theme;
        }

        /**
         * @return The description
         */
        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        /**
         * @param description The description
         */
        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return The items
         */
        @JsonProperty("items")
        public String getItems() {
            return items;
        }

        /**
         * @param items The items
         */
        @JsonProperty("items")
        public void setItems(String items) {
            this.items = items;
        }

        /**
         * @return The view
         */
        @JsonProperty("view")
        public String getView() {
            return view;
        }

        /**
         * @param view The view
         */
        @JsonProperty("view")
        public void setView(String view) {
            this.view = view;
        }

        /**
         * @return The avatarHd
         */
        @JsonProperty("avatar_hd")
        public String getAvatarHd() {
            return avatarHd;
        }

        /**
         * @param avatarHd The avatar_hd
         */
        @JsonProperty("avatar_hd")
        public void setAvatarHd(String avatarHd) {
            this.avatarHd = avatarHd;
        }

        public Number getId() {
            return id;
        }

        public void setId(Number id) {
            this.id = id;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "repo_info"
    })
    public static class Subscription {

        @JsonProperty("repo_info")
        private RepoInfo repoInfo;

        /**
         * @return The repoInfo
         */
        @JsonProperty("repo_info")
        public RepoInfo getRepoInfo() {
            return repoInfo;
        }

        /**
         * @param repoInfo The repo_info
         */
        @JsonProperty("repo_info")
        public void setRepoInfo(RepoInfo repoInfo) {
            this.repoInfo = repoInfo;
        }

    }

}



