package com.example.loadingscreen.Utils;

import com.example.loadingscreen.model.class_sched_model;
import com.example.loadingscreen.model.postList_model;

import java.util.ArrayList;

public class classSchedUtils {
    public static ArrayList<class_sched_model>bsitCache= new ArrayList<>();
    public static ArrayList<class_sched_model>blisCache = new ArrayList<>();

    public static Boolean bsitList(String key){
        for (class_sched_model bsit_model : bsitCache){
            if (bsit_model.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }
    public static Boolean blisList(String key){
        for (class_sched_model blis_model:blisCache){
            if (blis_model.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }
}
