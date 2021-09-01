package com.example.loadingscreen.win.model;

public class Organization {
    public Organization() {
    }

    private String orgMission,orgName,orgStatus,about,image,userid;

    public Organization(String orgMission, String orgName, String orgStatus, String about, String image, String userid) {
        this.orgMission = orgMission;
        this.orgName = orgName;
        this.orgStatus = orgStatus;
        this.about = about;
        this.image = image;
        this.userid = userid;
    }

    public String getOrgMission() {
        return orgMission;
    }

    public void setOrgMission(String orgMission) {
        this.orgMission = orgMission;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(String orgStatus) {
        this.orgStatus = orgStatus;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
