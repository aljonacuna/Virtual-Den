package com.example.loadingscreen.win.model;

public class LetterDean {
    private String letter;
    private String userID;
    private String dateSubmit;
    private String letterID;
    public LetterDean(){

    }

    public LetterDean(String letter, String userID, String dateSubmit, String letterID) {
        this.letter = letter;
        this.userID = userID;
        this.dateSubmit = dateSubmit;
        this.letterID = letterID;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDateSubmit() {
        return dateSubmit;
    }

    public void setDateSubmit(String dateSubmit) {
        this.dateSubmit = dateSubmit;
    }

    public String getLetterID() {
        return letterID;
    }

    public void setLetterID(String letterID) {
        this.letterID = letterID;
    }
}
