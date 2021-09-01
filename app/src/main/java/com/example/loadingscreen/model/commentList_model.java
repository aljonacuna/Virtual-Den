package com.example.loadingscreen.model;

public class commentList_model {
    String name;
    String userid;
    String authorid;
    String postkey;
    String commenttxt;
    String user_profilephoto;
    String dateTime;

    public commentList_model() {

    }

    public commentList_model(String name, String userid, String authorid, String postkey, String commenttxt, String user_profilephoto, String dateTime) {
        this.name = name;
        this.userid = userid;
        this.authorid = authorid;
        this.postkey = postkey;
        this.commenttxt = commenttxt;
        this.user_profilephoto = user_profilephoto;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getCommenttxt() {
        return commenttxt;
    }

    public void setCommenttxt(String commenttxt) {
        this.commenttxt = commenttxt;
    }

    public String getUser_profilephoto() {
        return user_profilephoto;
    }

    public void setUser_profilephoto(String user_profilephoto) {
        this.user_profilephoto = user_profilephoto;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
