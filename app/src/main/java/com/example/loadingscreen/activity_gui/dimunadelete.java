package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.schedule.class_faculty_sched;
import com.example.loadingscreen.schedule.facultypanel;
import com.example.loadingscreen.schedule.schedule_roomassign;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.loadingscreen.Utils.sharedpref.schoolyear;
import static com.example.loadingscreen.Utils.sharedpref.sems;
import static com.example.loadingscreen.Utils.sharedpref.user_type_get;

public class dimunadelete extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseUser firebaseUser;
    public static final String emailAdd = "EmailAddress";
    public static final String fileName = "Login";
    public static final String guest = "guest";
    public static final String signup = "SignupEmailAddress";
    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;
    String usertype = "";
    SharedPreferences sharedPreferences;
    public static final String fileName_permission = "Permission";
    public static final String tag_permission = "Deny";
    public static final String tag_granted = "Granted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        drawerLayout = findViewById(R.id.constraintLayout);
        navigationView = findViewById(R.id.nav_menu);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawerOpen, R.string.nav_draweClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        sharedPreferences = getSharedPreferences(fileName_permission, Context.MODE_PRIVATE);
        loadsy();
        getUsertype();
        if (sharedPreferences!=null && sharedPreferences.contains(tag_permission)){

        }else{
            permissionUtils.isPermissionGranted(this);
        }
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.newsfeed);
        preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public void setMenu(String usertype) {
        if (usertype.equals("Admin")) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.room).setVisible(true);
            menu.findItem(R.id.deansforum).setVisible(true);
            menu.findItem(R.id.sched).setVisible(true);
            menu.findItem(R.id.pap).setVisible(true);
            menu.findItem(R.id.messages).setVisible(true);
            menu.findItem(R.id.admin).setVisible(true);
        }
        else if (usertype.equals("Faculty")){
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.room).setVisible(true);
            menu.findItem(R.id.deansforum).setVisible(true);
            menu.findItem(R.id.sched).setVisible(true);
            menu.findItem(R.id.pap).setVisible(true);
            menu.findItem(R.id.messages).setVisible(true);
            menu.findItem(R.id.facultypanel).setVisible(true);
        }
        else if (usertype.equals("Student")){
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.room).setVisible(true);
            menu.findItem(R.id.deansforum).setVisible(true);
            menu.findItem(R.id.sched).setVisible(true);
            menu.findItem(R.id.pap).setVisible(true);
            menu.findItem(R.id.messages).setVisible(true);
        }

    }

/** @Override
    public boolean onCreateOptionsMenu(final Menu menu1) {
       MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toEditmenu, menu1);
        MenuItem search = menu1.findItem(R.id.search_user);
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(announcement.this, searchUser.class);
                startActivity(intent);
                return false;
            }
        });

        return true;
    }*/

    public void Signout() {
        firebaseAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(emailAdd);
        editor.remove(guest);
        editor.remove(signup);
        editor.commit();
        firebaseAuth.signOut();
        Intent intent = new Intent(this, signin.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tag_granted, "granted");
            editor.commit();
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tag_permission, "deny");
            editor.commit();
            Toast.makeText(this, "External storage permission required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lostandfound:
                Intent intent = new Intent(this, lostandfound.class);
                startActivity(intent);
                break;
            case R.id.logout:
                Signout();
                break;
            case R.id.profile:
                Intent intent1 = new Intent(this, userprofile.class);
                startActivity(intent1);
                break;
            case R.id.messages:
                Intent i = new Intent(this, contactList.class);
                startActivity(i);
                break;
            case R.id.room:
                Intent room = new Intent(this, schedule_roomassign.class);
                startActivity(room);
                break;
            case R.id.sched:
                Intent sched = new Intent(this, class_faculty_sched.class);
                startActivity(sched);
                break;
            case R.id.admin:
            case R.id.facultypanel:
                Intent fp = new Intent(this, facultypanel.class);
                startActivity(fp);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onlineStatus(String usertype) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , hh:mm aaa");
        final String dateTime = dateFormat.format(Calendar.getInstance().getTime());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(usertype).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference.child("status").setValue("online");
                    reference.child("status").onDisconnect().setValue(dateTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUsertype() {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference user_type = FirebaseDatabase.getInstance().getReference("userstbl").child(userId.getUid());
        user_type.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usertype = snapshot.child("userType").getValue().toString();
                user_type_get = usertype;
                onlineStatus(usertype);
                setMenu(usertype);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadsy() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schoolyear");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String sy = snapshot.child("schoolyear").getValue().toString();
                    schoolyear = sy;
                    String sem = snapshot.child("semester").getValue().toString();
                    sems = sem;

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    public void onClick(View view) {

    }
}