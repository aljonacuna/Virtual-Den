package com.example.loadingscreen.model;

public class roomList_model {
    private String roomname;
    private String roomplace;
    private String roomkey;

    public roomList_model() {
    }

    public roomList_model(String roomname, String roomplace, String roomkey) {
        this.roomname = roomname;
        this.roomplace = roomplace;
        this.roomkey = roomkey;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getRoomplace() {
        return roomplace;
    }

    public void setRoomplace(String roomplace) {
        this.roomplace = roomplace;
    }

    public String getRoomkey() {
        return roomkey;
    }

    public void setRoomkey(String roomkey) {
        this.roomkey = roomkey;
    }
}
