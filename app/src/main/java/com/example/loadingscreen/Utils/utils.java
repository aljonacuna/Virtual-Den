package com.example.loadingscreen.Utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.loadingscreen.model.chatList_model;
import com.example.loadingscreen.model.usersList_model;

import java.util.ArrayList;
import java.util.List;

public class utils {
    public static List<usersList_model> DataCache = new ArrayList<>();
    public static ArrayList<chatList_model>chatCache = new ArrayList<>();

    public static Boolean facultyExist(String key){
        for (usersList_model users_list: DataCache){
            if (users_list.getUserID().equals(key)){
                return true;
            }
        }
        return false;
    }
    public static Boolean chatExist(String key){
        for (chatList_model chatList : chatCache){
            if (chatList.getChatkey().equals(key)){
                return true;
            }
        }
        return false;
    }
}
