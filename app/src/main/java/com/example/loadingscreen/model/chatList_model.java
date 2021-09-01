package com.example.loadingscreen.model;

import com.google.firebase.database.Exclude;

public class chatList_model {
    private String chat;
    private String receiverId;
    private String senderId;
    private String seen;
    private String timeStamp;
    private String type;
    private String linkkey;
    private String chatkey;
    public chatList_model(){

    }
    public chatList_model(String chat, String receiverId, String senderId, String seen, String timeStamp, String type,String chatkey) {
        this.chat = chat;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.seen = seen;
        this.timeStamp = timeStamp;
        this.type = type;
        this.chatkey = chatkey;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @Exclude
    public String getLinkkey(){
        return linkkey;
    }
    @Exclude
    public void setLinkkey(String linkkey){
        this.linkkey = linkkey;
    }

    public String getChatkey() {
        return chatkey;
    }

    public void setChatkey(String chatkey) {
        this.chatkey = chatkey;
    }
}
