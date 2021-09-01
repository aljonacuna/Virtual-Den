package com.example.loadingscreen.win.model;

public class UserList {
    String name,type,userId,about,image;

    public UserList() {
    }

    public UserList(String name,String type,String userId,String about,String image) {
        this.name = name;
        this.type=type;
        this.userId=userId;
        this.about=about;
        this.image=image;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
