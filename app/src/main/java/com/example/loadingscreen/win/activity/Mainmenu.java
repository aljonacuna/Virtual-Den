package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.loadingscreen.R;

import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.activity_gui.signin;
import com.example.loadingscreen.win.fragment.HomeMenuUser;
import com.example.loadingscreen.win.fragment.MessageMenuUser;
import com.example.loadingscreen.win.fragment.ProfileMenuUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.loadingscreen.activity_gui.signin.emailAdd;
import static com.example.loadingscreen.activity_gui.signin.fileName;

public class Mainmenu extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    boolean pressbackTwice = false;
    boolean clearIntent = false;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences preferences;
    sharedpref getuserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        Window window = getWindow();
        bottomNavigationView = findViewById(R.id.menuBotNav);
        getuserType = new sharedpref();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        if (getIntent().getExtras() != null && getIntent().hasExtra("profile")) {
            if (clearIntent) {
                bottomNavigationView.setSelectedItemId(R.id.menuHome);
                bottomNavigationView.setOnNavigationItemSelectedListener(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_user_panel
                        , new HomeMenuUser()).commit();
            } else {
                bottomNavigationView.setSelectedItemId(R.id.menuProfile);
                bottomNavigationView.setOnNavigationItemSelectedListener(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_user_panel, new ProfileMenuUser()).commit();
            }
        } else {
            if (getuserType.spUsertype(this).equals("Student")) {
                firebaseUser = firebaseAuth.getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(firebaseUser.getUid());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String isDisabled = snapshot.child("isDisabled").getValue().toString();
                        if (isDisabled.equals("Yes")) {
                            final Dialog alertDialog = new Dialog(Mainmenu.this);
                            alertDialog.setContentView(R.layout.alert_acc_disabled);
                            TextView textView = alertDialog.findViewById(R.id.message_alert);
                            Button btn = alertDialog.findViewById(R.id.okaybtn);
                            String msg = "Your account has been disabled. Read" + "<font color=\"#4B8BBE\">" + " terms and conditions" + "</font>";
                            textView.setText(Html.fromHtml(msg), TextView.BufferType.SPANNABLE);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final Dialog termsDialog = new Dialog(Mainmenu.this);
                                    termsDialog.setContentView(R.layout.termspopup);
                                    ImageView btnclose = termsDialog.findViewById(R.id.close_terms);
                                    btnclose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            termsDialog.dismiss();
                                        }
                                    });
                                    termsDialog.setCancelable(false);
                                    termsDialog.show();
                                }
                            });
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    firebaseAuth = FirebaseAuth.getInstance();
                                    preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.remove(emailAdd);
                                    editor.remove(signin.signup);
                                    editor.commit();
                                    firebaseAuth.signOut();
                                    Intent intent = new Intent(Mainmenu.this, signin.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertDialog.setCancelable(false);
                            alertDialog.show();

                        } else {
                           showmenu();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                showmenu();
            }
        }

    }
    private void showmenu(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    bottomNavigationView.setSelectedItemId(R.id.menuHome);
                    bottomNavigationView.setOnNavigationItemSelectedListener(Mainmenu.this);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_user_panel
                            , new HomeMenuUser()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Mainmenu.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);
    }
    @Override
    public void onBackPressed() {
        if (pressbackTwice) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Fragment back = null;
            back = new HomeMenuUser();
            bottomNavigationView.setSelectedItemId(R.id.menuHome);
            Toast.makeText(this, "Please click back button again to exit", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.container_user_panel, back).commit();
            pressbackTwice = true;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.menuHome:
                selectedFragment = new HomeMenuUser();
                break;
            case R.id.menuMessage:
                selectedFragment = new MessageMenuUser();
                pressbackTwice = false;
                clearIntent = true;
                break;
            case R.id.menuProfile:
                selectedFragment = new ProfileMenuUser();
                pressbackTwice = false;
                clearIntent = true;
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container_user_panel, selectedFragment).commit();
        return true;
    }
}