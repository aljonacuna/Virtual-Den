package com.example.loadingscreen.model;

public class roomsched_model {
    private String yearsectiongroup;
    private String instructor;
    private String daysofweek;
    private String timeperiod;
    private String subjectcode;
    private String key;
    public roomsched_model(){

    }
    public roomsched_model(String yearsectiongroup, String instructor, String daysofweek, String timeperiod,String subjectcode,String key) {
        this.yearsectiongroup = yearsectiongroup;
        this.instructor = instructor;
        this.daysofweek = daysofweek;
        this.timeperiod = timeperiod;
        this.subjectcode = subjectcode;
        this.key = key;
    }

    public String getYearsectiongroup() {
        return yearsectiongroup;
    }

    public void setYearsectiongroup(String yearsectiongroup) {
        this.yearsectiongroup = yearsectiongroup;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDaysofweek() {
        return daysofweek;
    }

    public void setDaysofweek(String daysofweek) {
        this.daysofweek = daysofweek;
    }

    public String getTimeperiod() {
        return timeperiod;
    }

    public void setTimeperiod(String timeperiod) {
        this.timeperiod = timeperiod;
    }

    public String getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(String subjectcode) {
        this.subjectcode = subjectcode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
