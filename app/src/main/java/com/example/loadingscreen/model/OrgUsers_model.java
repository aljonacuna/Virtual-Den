package com.example.loadingscreen.model;

public class OrgUsers_model {
    private String userID;
    public OrgUsers_model(){

    }
    public OrgUsers_model(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
