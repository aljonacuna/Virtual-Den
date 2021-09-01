package com.example.loadingscreen.model;

public class class_sched_model {
    private String classSched;
    private String yearsection;
    private String group;
    private String course;
    private String key;
    private String date;
    private String postedBy;
    private String note;
    public class_sched_model(){

    }

    public class_sched_model(String classSched, String yearsection,
                             String group, String course,String key,String date,String postedBy,String note) {
        this.classSched = classSched;
        this.yearsection = yearsection;
        this.group = group;
        this.course = course;
        this.key = key;
        this.date = date;
        this.postedBy = postedBy;
        this.note = note;
    }

    public String getClassSched() {
        return classSched;
    }

    public void setClassSched(String classSched) {
        this.classSched = classSched;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
