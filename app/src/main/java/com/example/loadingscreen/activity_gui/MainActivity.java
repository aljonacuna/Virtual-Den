package com.example.loadingscreen.activity_gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.win.activity.AdminPanel;
import com.example.loadingscreen.win.activity.Mainmenu;
import com.example.loadingscreen.win.activity.OrgUserMenu;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String fileName = "Login";
    public static final String emailAdd = "EmailAddress";
    public static final String password = "Password";
    public static final String signup = "SignupEmailAddress";
    private static int TIME_OUT = 1000;
    private TextView textView;
    SharedPreferences prefUsertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                load();
            }
        }, TIME_OUT);

    }

    public void load() {
        preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String usertype = sharedpref.spUsertype(this);
        if (preferences.contains(signup) || preferences.contains(emailAdd)) {
            if (usertype.equals("Student") || usertype.equals("Faculty")) {
                Intent intent = new Intent(this, Mainmenu.class);
                startActivity(intent);
                finish();
            } else if (usertype.equals("Organizations")) {
                Intent intent = new Intent(this, OrgUserMenu.class);
                startActivity(intent);
                finish();
            }
        } else if (usertype.equals("Admin")) {
            try {
                if (sharedpref.saveAdminEncryptedData(this).contains(emailAdd)) {
                    Intent intent = new Intent(this, AdminPanel.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(this, signin.class);
                    startActivity(intent);
                    finish();
                }
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
            finish();
        }

    }


}