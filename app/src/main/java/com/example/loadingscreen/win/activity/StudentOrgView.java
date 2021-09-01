package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.chat;
import com.example.loadingscreen.win.Adapter.AnnouncementAdapter;
import com.example.loadingscreen.win.model.Announcements;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class StudentOrgView extends AppCompatActivity implements View.OnClickListener {
    TextView orgAboutBtn1, orgName, orgSendmsg, viewOfficial, orgMission, count_post;
    ImageView orgImg;
    private RecyclerView postRecyclerView;
    String id = "", countPost = "", img = "", name = "";
    DatabaseReference announceRef, orgAnnounceRef, orgRef, announceRef1, orgAnnounceRef1, userRef;
    LinearLayoutManager announceLayoutManager;
    private ArrayList<Announcements>announceList;
    private AnnouncementAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_org_view);
        postRecyclerView = findViewById(R.id.rv_studorgview);
        orgImg = findViewById(R.id.orgImage);
        orgName = findViewById(R.id.orgName);
        orgMission = findViewById(R.id.orgDesc);
        orgSendmsg = findViewById(R.id.sendMSGORG);
        viewOfficial = findViewById(R.id.sendPm);
        count_post = findViewById(R.id.postCount);
        orgAboutBtn1 = findViewById(R.id.orgAboutBtn1);
        if (getIntent().hasExtra("count")) {
            id = getIntent().getExtras().getString("id");
            countPost = getIntent().getExtras().getString("count");
            name = getIntent().getExtras().getString("name");
            img = getIntent().getExtras().getString("img");

        } else {
            id = getIntent().getExtras().getString("id");
            name = getIntent().getExtras().getString("name");
            img = getIntent().getExtras().getString("img");
        }
        orgAboutBtn1.setOnClickListener(this);
        viewOfficial.setOnClickListener(this);
        orgSendmsg.setOnClickListener(this);
        postCount();
        displayAnnouncement();
    }

    @Override
    public void onClick(View view) {
        if (view == orgAboutBtn1) {
            Intent intent = new Intent(StudentOrgView.this, OrgAbout.class);
            intent.putExtra("id", id);
            intent.putExtra("img", img);
            intent.putExtra("name", name);
            intent.putExtra("count", countPost);
            startActivity(intent);
        } else if (view == viewOfficial) {
            Intent intent = new Intent(StudentOrgView.this, StudentViewOfficers.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (view == orgSendmsg) {
            Intent intent = new Intent(this, chat.class);
            intent.putExtra("receiverid", id);
            intent.putExtra("receivername", name);
            intent.putExtra("receiverimg", img);
            startActivity(intent);
        }
    }

    public void displayAnnouncement(){
        announceList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Announcements");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (id.equals(dataSnapshot.child("orgkey").getValue().toString())){
                        Announcements announcements = dataSnapshot.getValue(Announcements.class);
                        announceList.add(announcements);
                    }
                }
                adapter = new AnnouncementAdapter(StudentOrgView.this,announceList);
                postRecyclerView.setAdapter(adapter);
                postRecyclerView.setLayoutManager(new LinearLayoutManager(StudentOrgView.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void postCount(){
        if (!countPost.equals("")) {
            if (countPost.equals("0")) {
                count_post.setText("This organization have not post yet");
            } else {
                if (countPost.equals("1")){
                    count_post.setText("This organization has "+countPost+" post.");
                }else{
                    count_post.setText("This organization has "+countPost+" posts.");
                }
            }
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrgAnnounces").child(id);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Long postcount = snapshot.getChildrenCount();
                        String strCount = String.valueOf(postcount);
                        if (postcount>1){
                            count_post.setText("This organization have "+strCount+" post.");
                        }else{
                            count_post.setText("This organization have "+strCount+" posts.");
                        }
                    } else {

                        count_post.setText("This organization have not post yet");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        announceRef = FirebaseDatabase.getInstance().getReference().child("Announcements");
        announceRef1 = FirebaseDatabase.getInstance().getReference().child("Announcements");

        orgAnnounceRef1 = FirebaseDatabase.getInstance().getReference().child("OrgAnnounces").child(id);
        userRef = FirebaseDatabase.getInstance().getReference().child("Organizations").child(id);
        orgAnnounceRef = FirebaseDatabase.getInstance().getReference().child("OrgAnnounces").child(id);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String orgDesc = snapshot.child("about").getValue().toString();
                    String orgNameTxt = snapshot.child("fullname").getValue().toString();
                    String orgImage = snapshot.child("profileimg").getValue().toString();
                    orgMission.setText(orgDesc);
                    orgName.setText(orgNameTxt);
                    Picasso.with(StudentOrgView.this).load(orgImage).resize(150, 150).
                            transform(new CropCircleTransformation()).into(orgImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}