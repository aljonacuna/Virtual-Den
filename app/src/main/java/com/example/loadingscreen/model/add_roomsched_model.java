package com.example.loadingscreen.model;

public class add_roomsched_model {
    private String image;
    private String key;
    private String date;
    private String note;
    public add_roomsched_model(){

    }
    public add_roomsched_model(String image,String key,String date,String note) {
        this.image = image;
        this.key = key;
        this.date = date;
        this.note = note;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
