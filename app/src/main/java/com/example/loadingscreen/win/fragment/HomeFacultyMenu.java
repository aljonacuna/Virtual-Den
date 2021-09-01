package com.example.loadingscreen.win.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.activity_gui.lostandfound;
import com.example.loadingscreen.schedule.class_faculty_sched;
import com.example.loadingscreen.schedule.schedule_roomassign;
import com.example.loadingscreen.win.activity.AdminSettings;
import com.example.loadingscreen.win.activity.AnnouncementMenu;
import com.example.loadingscreen.win.activity.LetterforDean;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.loadingscreen.Utils.sharedpref.user_type_get;

public class HomeFacultyMenu extends Fragment implements View.OnClickListener {

    private CardView announcementMenu, viewSched, findRoom, lost_found, settings, letterdean, f_a_menu;
    private TextView menutxt;
    private ImageView menuimg;
    public static final String fileName_permission = "Permission";
    public static final String tag_permission = "Deny";
    public static final String tag_granted = "Granted";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    String usertypeviasp = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home_faculty_menu, container, false);
        announcementMenu = view.findViewById(R.id.announcementMenu);
        viewSched = view.findViewById(R.id.scheduleMenu);
        findRoom = view.findViewById(R.id.findRoomMenu);
        lost_found = view.findViewById(R.id.lostAndFoundMenu);
        settings = view.findViewById(R.id.settingsMenu);
        letterdean = view.findViewById(R.id.letterDeanMenu);
        f_a_menu = view.findViewById(R.id.announcementMenu);
        menuimg = view.findViewById(R.id.menu_img);
        menutxt = view.findViewById(R.id.menu_txt);
        bottomNavigationView = view.findViewById(R.id.menuBotNav);
        sharedPreferences = this.getActivity().getSharedPreferences(fileName_permission, Context.MODE_PRIVATE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (sharedPreferences != null && sharedPreferences.contains(tag_permission)) {

        } else {
            permissionUtils.isPermissionGranted(getActivity());
        }
        getUsertype();
        announcementMenu.setOnClickListener(this);
        viewSched.setOnClickListener(this);
        findRoom.setOnClickListener(this);
        lost_found.setOnClickListener(this);
        settings.setOnClickListener(this);
        letterdean.setOnClickListener(this);
        f_a_menu.setOnClickListener(this);
        usertypeviasp = sharedpref.spUsertype(getContext());
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == announcementMenu) {
            Intent intent = new Intent(getContext(), AnnouncementMenu.class);
            startActivity(intent);
        } else if (view == viewSched) {
            Intent intent = new Intent(getContext(), class_faculty_sched.class);
            startActivity(intent);
        } else if (view == findRoom) {
            Intent intent = new Intent(getContext(), schedule_roomassign.class);
            startActivity(intent);
        } else if (view == lost_found) {
            Intent intent = new Intent(getContext(), lostandfound.class);
            startActivity(intent);
        } else if (view == settings) {
            Intent intent = new Intent(getContext(), AdminSettings.class);
            startActivity(intent);
        } else if (view == letterdean) {
            Intent intent = new Intent(getContext(), LetterforDean.class);
            startActivity(intent);
        }
    }
    public void getUsertype() {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference user_type = FirebaseDatabase.getInstance().getReference("userstbl").child(userId.getUid());
        user_type.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usertype = snapshot.child("userType").getValue().toString();
                user_type_get = usertype;
                onlineStatus(usertype);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tag_granted, "granted");
            editor.commit();
            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tag_permission, "deny");
            editor.commit();
            Toast.makeText(getContext(), "External storage permission required", Toast.LENGTH_SHORT).show();
        }
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

}