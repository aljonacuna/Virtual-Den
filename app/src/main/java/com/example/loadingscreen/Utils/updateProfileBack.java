package com.example.loadingscreen.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.loadingscreen.activity_gui.userprofile;
import com.example.loadingscreen.win.activity.Mainmenu;

public class updateProfileBack {
    public static void onBack(Context context,String tag,String visitdispname,String visitusertype,String visitid){
        if (tag.equals("myprofile")) {
            Intent intent = new Intent(context, Mainmenu.class);
            intent.putExtra("profile", "profile");
            context.startActivity(intent);
            ((Activity)context).finish();
        } else {
            Intent intent = new Intent(context, userprofile.class);
            intent.putExtra("displayname", visitdispname);
            intent.putExtra("userType", visitusertype);
            intent.putExtra("userid", visitid);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }
}
