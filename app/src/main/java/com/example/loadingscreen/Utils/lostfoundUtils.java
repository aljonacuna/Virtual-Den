package com.example.loadingscreen.Utils;

import com.example.loadingscreen.model.claimList_model;
import com.example.loadingscreen.model.postList_model;

import java.util.ArrayList;
import java.util.List;

public class lostfoundUtils {
    public static ArrayList<postList_model>FoundCache = new ArrayList<>();
    public static ArrayList<postList_model>LostCache = new ArrayList<>();
    public static ArrayList<postList_model>sortFound = new ArrayList<>();
    public static ArrayList<claimList_model>claimCache = new ArrayList<>();

    public static Boolean claimList(String key){
        for (claimList_model claim_model : claimCache){
            if (claim_model.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }

    public static Boolean foundList(String key){
        for (postList_model found_model :FoundCache){
            if (found_model.getPostKey().equals(key)){
                return true;
            }
        }
        return false;
    }
    public static Boolean lostList(String key){
        for (postList_model lost_model:LostCache){
            if (lost_model.getPostKey().equals(key)){
                return true;
            }
        }
        return false;
    }
    public static Boolean sortFound(String key){
        for (postList_model sortfound :sortFound){
            if (sortfound.getPostKey().equals(key)){
                return true;
            }
        }
        return false;
    }
}
