package com.example.loadingscreen.win.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.loadingscreen.R;

import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.activity_gui.EditProfile;
import com.example.loadingscreen.activity_gui.toUploadBgimg;
import com.example.loadingscreen.activity_gui.toUploadprofileimg;
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

public class ProfileMenuUser extends Fragment implements View.OnClickListener {
    private TextView gendertxt, sectiontxt, fullnametxt, nothingtoshowlbl, sectionlbl, statusTv, aboutTv;
    private Button lostbtn, foundbtn, editpofilebtn;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private ImageView profileBgimg, profileImg, profileBtn, bg_img, imageicon;
    private String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
    private Menu menuITEM;
    private userprofileAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<postList_model> postList;
    private String status = "Found";
    String visituserId, visitdispName, visitUserType;
    String usertype = "";
    String profileimg;
    String bgimg, about, yearsection, group, tag = "myprofile";
    private LinearLayoutManager linearLayoutManager;
    private ValueEventListener valueEventListener;
    private DatabaseReference query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_menu_user, container, false);

        recyclerView = view.findViewById(R.id.recyclerUserprofile);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        lostbtn = view.findViewById(R.id.lostbtn);
        foundbtn = view.findViewById(R.id.foundbtn);

        profileBgimg = view.findViewById(R.id.upload_bgimg);
        profileImg = view.findViewById(R.id.user_profileimg);
        profileBtn = view.findViewById(R.id.upload_profilebtn);
        bg_img = view.findViewById(R.id.bg_img);
        gendertxt = view.findViewById(R.id.gendertxt);
        sectiontxt = view.findViewById(R.id.sectiontxt);
        aboutTv = view.findViewById(R.id.aboutTv);
        editpofilebtn = view.findViewById(R.id.msgbtn);
        fullnametxt = view.findViewById(R.id.fullname);
        nothingtoshowlbl = view.findViewById(R.id.nothingtoshowlbl);
        sectionlbl = view.findViewById(R.id.sectionlbl);
        imageicon = view.findViewById(R.id.imageicon);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileBtn.setOnClickListener(this);
        profileBgimg.setOnClickListener(this);
        editpofilebtn.setOnClickListener(this);
        bg_img.setOnClickListener(this);
        usertype = sharedpref.spUsertype(getContext());
        if (firebaseUser.getPhotoUrl() != null) {
            loadInfo();
            Picasso.with(getContext()).load(firebaseUser.getPhotoUrl().toString()).into(profileImg);
            profile_fullscreen(firebaseUser.getPhotoUrl().toString());
        } else {
            loadInfo();
            Picasso.with(getContext()).load(noProfileimg).into(profileImg);
        }
        foundbtn.setOnClickListener(this);
        lostbtn.setOnClickListener(this);
        loadfound();
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == profileBtn) {
            Intent i = new Intent(getContext(), toUploadprofileimg.class);
            i.putExtra("usertype", usertype);
            i.putExtra("tag1", tag);
            startActivity(i);
        } else if (view == profileBgimg) {
            Intent intent = new Intent(getContext(), toUploadBgimg.class);
            intent.putExtra("usertype", usertype);
            intent.putExtra("tag1", tag);
            startActivity(intent);
        } else if (view == lostbtn) {
            status = "Lost";
            loadfound();
            lostbtn.setTextColor(Color.parseColor("#4B8BBE"));
            foundbtn.setTextColor(Color.parseColor("#000000"));
            nothingtoshowlbl.setText(status);
            nothingtoshowlbl.setVisibility(View.VISIBLE);
        } else if (view == foundbtn) {
            status = "Found";
            loadfound();
            foundbtn.setTextColor(Color.parseColor("#4B8BBE"));
            lostbtn.setTextColor(Color.parseColor("#000000"));
            nothingtoshowlbl.setText(status);
            nothingtoshowlbl.setVisibility(View.VISIBLE);
        } else if (view == editpofilebtn) {
            Intent intent = new Intent(getContext(), EditProfile.class);
            intent.putExtra("tag1",tag);
            startActivity(intent);
        } else if (view == bg_img) {
            if (bgimg.equals("none")) {

            } else {
                Intent intent = new Intent(getContext(), chat_userimg_fullscreen.class);
                intent.putExtra("userimg", bgimg);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        bg_img, ViewCompat.getTransitionName(bg_img));
                startActivity(intent, optionsCompat.toBundle());
            }
        }
    }

    public void profile_fullscreen(final String img) {
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img.equals("none")) {
                } else {
                    Intent intent = new Intent(getContext(), chat_userimg_fullscreen.class);
                    intent.putExtra("userimg", img);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            profileImg, ViewCompat.getTransitionName(profileImg));
                    startActivity(intent, optionsCompat.toBundle());
                }
            }
        });
    }

    public void loadInfo() {
        final String userID = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference(usertype).child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullname").getValue(String.class);
                    fullnametxt.setText(fullName);
                    String gender = snapshot.child("gender").getValue(String.class);
                    gendertxt.setText(gender);
                    about = snapshot.child("about").getValue().toString();
                    aboutTv.setText(about);
                    bgimg = snapshot.child("bgimg").getValue(String.class);
                    if (bgimg.equals("none")) {
                        //default bgimg
                    } else {
                        Picasso.with(getContext()).load(bgimg).into(bg_img);
                    }
                    if (usertype.equals("Student")) {
                        sectiontxt.setVisibility(View.VISIBLE);
                        imageicon.setVisibility(View.VISIBLE);
                        sectionlbl.setVisibility(View.VISIBLE);
                        yearsection = snapshot.child("yearsection").getValue(String.class);
                        group = snapshot.child("group").getValue(String.class);
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


    public void loadfound() {
        postList = new ArrayList<>();
        query = FirebaseDatabase.getInstance().getReference("Post").child(status);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (getActivity() != null) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            postList_model post = dataSnapshot.getValue(postList_model.class);
                            if (firebaseUser.getUid().equals(dataSnapshot.child("userID").getValue().toString())) {
                                postList.add(post);
                            }
                        }
                        adapter = new userprofileAdapter(getContext(), postList, tag);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setVisibility(View.VISIBLE);
                        nothingtoshowlbl.setText(status);
                        if (postList.isEmpty()) {
                            nothingtoshowlbl.setText("Nothing to show here");
                            recyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        nothingtoshowlbl.setText("Nothing to show here");
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //end
}