package com.example.loadingscreen.model;

import android.content.Intent;

import java.util.Date;

public class postList_model {
    String name;
    String photo;
    String desc;
    String postKey;
    String userID;
    String dateTime;
    String status;
    String item;
    public postList_model(){

    }

    public postList_model(String name, String photo, String caption, String postKey, String userID,String dateTime, String status,String item) {
        this.name = name;
        this.photo = photo;
        this.desc = caption;
        this.postKey = postKey;
        this.userID = userID;
        this.dateTime = dateTime;
        this.status = status;
        this.item = item;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String  dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
