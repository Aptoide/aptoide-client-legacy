package com.aptoide.amethyst.model.json;

import java.util.List;

import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Created by brutus on 09-12-2013.
 */
@Data
public class CheckUserCredentialsJson {
    @JsonProperty("status")
    public String status;

    @JsonProperty("token")
    public String token;

    @JsonProperty("id")
    public int id;

    @JsonProperty("repo")
    String repo;

    @JsonProperty("avatar")
    public String avatar;

    //default avatar
    @JsonProperty("ravatar_hd")
    public String repoAvatar;

    @JsonProperty("username")
    public String username;

    @JsonProperty("queueName")
    public String queueName;

    @JsonProperty("errors")
    public List<ErrorResponse> errors;

    @JsonProperty("settings")
     public Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public String getQueue() {
        return queueName;
    }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getRepo() { return repo; }

    public static class Settings {

        @JsonProperty("timeline")
        public String timeline;
        @JsonProperty("matureswitch")
        public String matureswitch;

        public String getMatureswitch() {
            return matureswitch;
        }

        public String getTimeline() {
            return timeline;
        }
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
