package io.app.musicfun.Models;

import android.net.Uri;
public class Status {
    private String statusUrl;
    private String userName;
    private String userId;

    public Status(String statusUrl, String userName, String userId) {
        this.statusUrl = statusUrl;
        this.userName = userName;
        this.userId = userId;
    }

    public Status() {
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public Status(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
