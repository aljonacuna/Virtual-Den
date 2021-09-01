package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.signin;
import com.example.loadingscreen.win.fragment.HomeOrgFragment;
import com.example.loadingscreen.win.fragment.MessageMenuUser;
import com.example.loadingscreen.win.fragment.MessageOrgFragment;
import com.example.loadingscreen.win.fragment.OfficerOrgFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.loadingscreen.activity_gui.signin.emailAdd;
import static com.example.loadingscreen.activity_gui.signin.fileName;
import static com.example.loadingscreen.activity_gui.signin.fileName_usertype;
import static com.example.loadingscreen.activity_gui.signin.userTypeSP;

public class OrgUserMenu extends AppCompatActivity {
    private DrawerLayout drawer;
    private FirebaseAuth myAuth;
    String currentUser;
    ImageView settingsBtn;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    SharedPreferences preferences;
    SharedPreferences spUsertype;
    boolean back = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_user_menu);
        myAuth=FirebaseAuth.getInstance();
        currentUser=myAuth.getCurrentUser().getUid();
        settingsBtn=findViewById(R.id.logoutBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrgUserMenu.this, AdminSettings.class);
                intent.putExtra("tagset","org");
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavOrg);
        if(savedInstanceState==null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeOrgFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeOrgFragment()).commit();
                        return true;
                    case R.id.messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageMenuUser()).commit();
                        back = false;
                        return true;
                    case R.id.officers:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfficerOrgFragment()).commit();
                        back = false;
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (back) {
            super.onBackPressed();
            finishAffinity();
        }else{
            bottomNavigationView.setSelectedItemId(R.id.home);
            Toast.makeText(this, "Please click back button again to exit", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.container_user_panel,new HomeOrgFragment());
            back = true;
        }
    }
}