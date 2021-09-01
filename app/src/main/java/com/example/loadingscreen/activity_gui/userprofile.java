package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.userprofileAdapter;
import com.example.loadingscreen.fullscreen_img.chat_userimg_fullscreen;
import com.example.loadingscreen.model.postList_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userprofile extends AppCompatActivity implements View.OnClickListener {
    private TextView gendertxt,fullnametxt, nothingtoshowlbl, sectionlbl, statusTv, aboutTv, sectiontxt;
    private Button lostbtn, foundbtn, msgbtn;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private ImageView profileBgimg, profileImg, profileBtn, bg_img, imageicon,backbtn;
    private String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
    private Menu menuITEM;
    private userprofileAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<postList_model> postList;
    private String status = "Found";
    String visituserId, visitdispName, visitUserType;
    String usertype = "";
    String profileimg;
    String bgimg, about, yearsection, group,tag="visitprofile";
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference query;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        recyclerView = findViewById(R.id.recyclerUserprofile);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lostbtn = findViewById(R.id.lostbtn);
        foundbtn = findViewById(R.id.foundbtn);
        profileBgimg = findViewById(R.id.upload_bgimg);
        profileImg = findViewById(R.id.user_profileimg);
        profileBtn = findViewById(R.id.upload_profilebtn);
        bg_img = findViewById(R.id.bg_img);
        gendertxt = findViewById(R.id.gendertxt);
        sectiontxt = findViewById(R.id.sectiontxt);
        msgbtn = findViewById(R.id.msgbtn);
        fullnametxt = findViewById(R.id.fullname);
        nothingtoshowlbl = findViewById(R.id.nothingtoshowlbl);
        sectionlbl = findViewById(R.id.sectionlbl);
        aboutTv = findViewById(R.id.aboutTv);
        imageicon = findViewById(R.id.imageicon);
        backbtn = findViewById(R.id.backbtn_user_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileBtn.setOnClickListener(this);
        profileBgimg.setOnClickListener(this);
        msgbtn.setOnClickListener(this);
        bg_img.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        //If null i load currentuser data
        if (getIntent().getExtras() != null && getIntent().hasExtra("displayname")) {
            visitdispName = getIntent().getExtras().getString("displayname");
            visituserId = getIntent().getExtras().getString("userid");
            visitUserType = getIntent().getExtras().getString("userType");
            fullnametxt.setText(visitdispName);
            getDataVisit();
            if (!visituserId.equals(firebaseUser.getUid())) {
                msgbtn.setText("MESSAGE");
                profileBtn.setVisibility(View.GONE);
                profileBgimg.setVisibility(View.GONE);
            } else {
                profileBgimg.setVisibility(View.VISIBLE);
                profileBtn.setVisibility(View.VISIBLE);

            }
        }
        foundbtn.setOnClickListener(this);
        lostbtn.setOnClickListener(this);
        foundAdapterRv();

    }//end on create

    public void profile_fullscreen(final String img) {
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img.equals("none")) {
                } else {
                    Intent intent = new Intent(userprofile.this, chat_userimg_fullscreen.class);
                    intent.putExtra("userimg", img);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(userprofile.this,
                            profileImg, ViewCompat.getTransitionName(profileImg));
                    startActivity(intent, optionsCompat.toBundle());
                }
            }
        });
    }


    public void getDataVisit() {
        //without usertype from intent
        if (visitUserType == null) {
            DatabaseReference getusertype = FirebaseDatabase.getInstance().getReference("userstbl").child(visituserId);
            getusertype.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    visitUserType = snapshot.child("userType").getValue().toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(visitUserType).child(visituserId);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String gender = snapshot.child("gender").getValue().toString();
                                gendertxt.setText(gender);
                                bgimg = snapshot.child("bgimg").getValue().toString();
                                profileimg = snapshot.child("profileimg").getValue().toString();
                                about = snapshot.child("about").getValue().toString();

                                aboutTv.setText(about);
                                if (bgimg.equals("none")) {
                                    //do nothing
                                } else {
                                    Picasso.with(userprofile.this).load(bgimg).into(bg_img);
                                }
                                if (profileimg.equals("none")) {
                                    profile_fullscreen(profileimg);
                                } else {
                                    Picasso.with(userprofile.this).load(profileimg).into(profileImg);
                                    profile_fullscreen(profileimg);
                                }
                                if (visitUserType.equals("Student")) {
                                    yearsection = snapshot.child("yearsection").getValue().toString();
                                    group = snapshot.child("group").getValue().toString();
                                    sectiontxt.setVisibility(View.VISIBLE);
                                    imageicon.setVisibility(View.VISIBLE);
                                    sectionlbl.setVisibility(View.VISIBLE);
                                    if (yearsection.equals("") && group.equals("")) {
                                        sectiontxt.setText("Not set");
                                    } else {
                                        sectiontxt.setText(yearsection + " - " + group);
                                    }
                                } else {

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //with usertype from intent
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(visitUserType).child(visituserId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String gender = snapshot.child("gender").getValue().toString();
                        if (!gender.equals("")) {
                            gendertxt.setText(gender);
                        }
                        bgimg = snapshot.child("bgimg").getValue().toString();
                        profileimg = snapshot.child("profileimg").getValue().toString();
                        about = snapshot.child("about").getValue().toString();
                        aboutTv.setText(about);
                        if (bgimg.equals("none")) {
                            //do nothing
                        } else {
                            Picasso.with(userprofile.this).load(bgimg).into(bg_img);
                        }
                        if (profileimg.equals("none")) {
                            profile_fullscreen(profileimg);
                        } else {
                            Picasso.with(userprofile.this).load(profileimg).into(profileImg);
                            profile_fullscreen(profileimg);
                        }
                        if (visitUserType.equals("Student")) {
                            yearsection = snapshot.child("yearsection").getValue().toString();
                            group = snapshot.child("group").getValue().toString();
                            sectiontxt.setVisibility(View.VISIBLE);
                            imageicon.setVisibility(View.VISIBLE);
                            sectionlbl.setVisibility(View.VISIBLE);
                            if (yearsection.equals("") && group.equals("")) {
                                sectiontxt.setText("Not set");
                            } else {
                                sectiontxt.setText(yearsection + " - " + group);
                            }
                        } else {

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


    public void foundAdapterRv() {
        loadfound();
    }

    public void loadfound() {
        postList = new ArrayList<>();
        query = FirebaseDatabase.getInstance().getReference("Post").child(status);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (getIntent().hasExtra("userid") || getIntent().getExtras()!=null) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            postList_model post = dataSnapshot.getValue(postList_model.class);
                            if (visituserId.equals(dataSnapshot.child("userID").getValue().toString())) {
                                postList.add(post);
                            }
                        }
                        adapter = new userprofileAdapter(userprofile.this, postList,tag);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        nothingtoshowlbl.setText(status);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (postList.isEmpty()) {
                            nothingtoshowlbl.setText("Nothing to show here");
                            recyclerView.setVisibility(View.GONE);
                        }

                    } else {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            postList_model post = dataSnapshot.getValue(postList_model.class);
                            if (firebaseUser.getUid().equals(dataSnapshot.child("userID").getValue().toString())) {
                                postList.add(post);
                            }
                        }
                        adapter = new userprofileAdapter(userprofile.this, postList,tag);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setVisibility(View.VISIBLE);
                        nothingtoshowlbl.setText(status);
                    }
                    if (postList.isEmpty()) {
                        nothingtoshowlbl.setText("Nothing to show here");
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    nothingtoshowlbl.setText("Nothing to show here");
                    recyclerView.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == profileBtn) {
            Intent i = new Intent(this, toUploadprofileimg.class);
            i.putExtra("tag1",tag);
            i.putExtra("displayname",visitdispName);
            i.putExtra("userType",visitUserType);
            i.putExtra("userid",visituserId);
            startActivity(i);
            finish();
        } else if (view == profileBgimg) {
            Intent intent = new Intent(this, toUploadBgimg.class);
            intent.putExtra("tag1",tag);
            intent.putExtra("displayname",visitdispName);
            intent.putExtra("userType",visitUserType);
            intent.putExtra("userid",visituserId);
            startActivity(intent);
            finish();
        } else if (view == lostbtn) {
            status = "Lost";
            foundAdapterRv();
            lostbtn.setTextColor(Color.parseColor("#4B8BBE"));
            foundbtn.setTextColor(Color.parseColor("#000000"));
            nothingtoshowlbl.setText(status);
            nothingtoshowlbl.setVisibility(View.VISIBLE);
        } else if (view == foundbtn) {
            status = "Found";
            foundAdapterRv();
            foundbtn.setTextColor(Color.parseColor("#4B8BBE"));
            lostbtn.setTextColor(Color.parseColor("#000000"));
            nothingtoshowlbl.setText(status);
            nothingtoshowlbl.setVisibility(View.VISIBLE);
        } else if (view == msgbtn) {
            String msgbtn_txt = msgbtn.getText().toString();
            if (msgbtn_txt.equals("EDIT PROFILE")) {
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("tag1",tag);
                intent.putExtra("displayname",visitdispName);
                intent.putExtra("userType",visitUserType);
                intent.putExtra("userid",visituserId);
                startActivity(intent);
                finish();
            } else if (msgbtn_txt.equals("MESSAGE")) {
                if (view == msgbtn) {
                    Intent intent = new Intent(this, chat.class);
                    intent.putExtra("receiverid", visituserId);
                    intent.putExtra("receivername", visitdispName);
                    intent.putExtra("receiverimg", profileimg);
                    startActivity(intent);
                }
            }
        } else if (view == bg_img) {
            if (bgimg.equals("none")) {

            } else {
                Intent intent = new Intent(this, chat_userimg_fullscreen.class);
                intent.putExtra("userimg", bgimg);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        bg_img, ViewCompat.getTransitionName(bg_img));
                startActivity(intent, optionsCompat.toBundle());
            }
        }else if (view == backbtn){
            finish();
        }

    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
