package com.example.loadingscreen.win.model;

public class Announcements {
    String content, announceFile, type,orgkey,postkey;
    long timestamp;

    public Announcements() {
    }

    public Announcements(String content, String announceFile, long timestamp, String type,String orgkey,String postkey) {
        this.content = content;
        this.announceFile = announceFile;
        this.timestamp = timestamp;
        this.type = type;
        this.orgkey = orgkey;
        this.postkey = postkey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnnounceFile() {
        return announceFile;
    }

    public void setAnnounceFile(String announceFile) {
        this.announceFile = announceFile;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrgkey() {
        return orgkey;
    }

    public void setOrgkey(String orgkey) {
        this.orgkey = orgkey;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }
}