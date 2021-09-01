package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.loadingscreen.activity_gui.signin.fileName;

public class AdminSettings extends AppCompatActivity {

    TextView updateEmailAdmin, changePwAdmin, logout;
    private FirebaseAuth firebaseAuth;
    SharedPreferences preferences;
    SharedPreferences spUsertype;
    String tag = "";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);
        sharedPreferences = getSharedPreferences(fileName,Context.MODE_PRIVATE);
        updateEmailAdmin = findViewById(R.id.updateEmailAdmin);
        changePwAdmin = findViewById(R.id.changePwAdmin);
        logout = findViewById(R.id.logoutAdmin);
        Toolbar settingsToolBar = findViewById(R.id.adminSettingsToolBar);
        if (getIntent().getExtras() != null) {
            tag = getIntent().getExtras().getString("tagset");
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder verifyDialog = new AlertDialog.Builder(AdminSettings.this);
                verifyDialog.setMessage("Are you sure you want to logout?");
                verifyDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                verifyDialog.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedpref.logout(AdminSettings.this);

                    }
                });
                AlertDialog verifyDialog1 = verifyDialog.create();
                verifyDialog1.show();

            }
        });
        updateEmailAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSettings.this, UpdateEmail.class);
                startActivity(intent);
            }
        });
        changePwAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSettings.this, ChangePassword.class);
                startActivity(intent);
            }
        });
        settingsToolBar.setNavigationIcon(R.drawable.arrow_back);
        settingsToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}