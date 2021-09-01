package com.example.loadingscreen.model;

public class claimList_model {
    private String claimername;
    private String dateclaim;
    private String claimerId;
    private String status;
    private String item;
    private String authorid;
    private String key;
    private String authorname;
    private String dateposted;
    private String postimg;
    public claimList_model(){

    }

    public claimList_model(String claimername, String dateclaim, String claimerId, String status,
                          String item, String authorid,String key,String authorname,String dateposted,String postimg) {
        this.claimername = claimername;
        this.dateclaim = dateclaim;
        this.claimerId = claimerId;
        this.status = status;
        this.item = item;
        this.authorid = authorid;
        this.key = key;
        this.authorname = authorname;
        this.dateposted = dateposted;
        this.postimg = postimg;
    }

    public String getClaimername() {
        return claimername;
    }

    public void setClaimername(String claimername) {
        this.claimername = claimername;
    }

    public String getDateclaim() {
        return dateclaim;
    }

    public void setDateclaim(String dateclaim) {
        this.dateclaim = dateclaim;
    }

    public String getClaimerId() {
        return claimerId;
    }

    public void setClaimerId(String claimerId) {
        this.claimerId = claimerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getDateposted() {
        return dateposted;
    }

    public void setDateposted(String dateposted) {
        this.dateposted = dateposted;
    }

    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }
}
