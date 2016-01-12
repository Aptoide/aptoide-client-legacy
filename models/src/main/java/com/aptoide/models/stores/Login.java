package com.aptoide.models.stores;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 19-11-2013
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Login {

    private String passwordSha1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;


    public String getPasswordSha1() {
        return passwordSha1;
    }

    public void setPasswordSha1(String passwordSha1) {
        this.passwordSha1 = passwordSha1;
    }
}
