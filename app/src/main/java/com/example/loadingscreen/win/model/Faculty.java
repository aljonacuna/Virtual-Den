package com.example.loadingscreen.win.model;

public class Faculty {
    public Faculty() {
    }
    String profFullName,profEmail,position,schedule;

    public Faculty(String profFullName, String profEmail, String position, String schedule) {
        this.profFullName = profFullName;
        this.profEmail = profEmail;
        this.position = position;
        this.schedule = schedule;
    }

    public String getProfFullName() {
        return profFullName;
    }

    public void setProfFullName(String profFullName) {
        this.profFullName = profFullName;
    }

    public String getProfEmail() {
        return profEmail;
    }

    public void setProfEmail(String profEmail) {
        this.profEmail = profEmail;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
