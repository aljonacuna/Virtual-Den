package com.example.loadingscreen.model;

public class usersList_model {
    private String emailadd;
    private String password;
    private String idnum;
    private String userID;
    private String fullname;
    private String birthday;
    private String gender;
    private String bgimg;
    private String profileimg;
    private String status;
    private String userType;
    private String sched;
    private String schedDate;
    private String schedNote;
    private String position;
    private String about;
    private String isDisabled;
    private String orgMission;
    private String yearsection;
    private String group;
            public usersList_model(){

            }

    public usersList_model(String emailadd, String password, String idnum, String userID, String fullname,
                           String birthday, String gender, String bgimg, String profileimg, String status, String userType,
                           String sched, String schedDate,
                           String schedNote, String position, String about,
                           String isDisabled, String orgMission, String yearsection,String group) {
        this.emailadd = emailadd;
        this.password = password;
        this.idnum = idnum;
        this.userID = userID;
        this.fullname = fullname;
        this.birthday = birthday;
        this.gender = gender;
        this.bgimg = bgimg;
        this.profileimg = profileimg;
        this.status = status;
        this.userType = userType;
        this.sched = sched;
        this.schedDate = schedDate;
        this.schedNote = schedNote;
        this.position = position;
        this.about = about;
        this.isDisabled = isDisabled;
        this.orgMission = orgMission;
        this.yearsection = yearsection;
        this.group = group;
    }

    public String getEmailadd() {
        return emailadd;
    }

    public void setEmailadd(String emailadd) {
        this.emailadd = emailadd;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdnum() {
        return idnum;
    }

    public void setIdnum(String idnum) {
        this.idnum = idnum;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBgimg() {
        return bgimg;
    }

    public void setBgimg(String bgimg) {
        this.bgimg = bgimg;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSched() {
        return sched;
    }

    public void setSched(String sched) {
        this.sched = sched;
    }

    public String getSchedDate() {
        return schedDate;
    }

    public void setSchedDate(String schedDate) {
        this.schedDate = schedDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(String isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getOrgMission() {
        return orgMission;
    }

    public void setOrgMission(String orgMission) {
        this.orgMission = orgMission;
    }

    public String getSchedNote() {
        return schedNote;
    }

    public void setSchedNote(String schedNote) {
        this.schedNote = schedNote;
    }

    public String getYearsection() {
        return yearsection;
    }

    public void setYearsection(String yearsection) {
        this.yearsection = yearsection;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
