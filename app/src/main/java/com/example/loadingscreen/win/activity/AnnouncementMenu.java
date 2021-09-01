package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.win.Adapter.AnnouncementAdapter;
import com.example.loadingscreen.win.model.Announcements;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnnouncementMenu extends AppCompatActivity {
    TextView orgBrought;
    private RecyclerView recyclerView;
    private AnnouncementAdapter announcementAdapter;
    private ArrayList<Announcements> announcementsList;
    private DatabaseReference databaseReference;
    private ImageView backbtn,nonet;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView lbl;
    check_network check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_menu);
        orgBrought = findViewById(R.id.orgBrought);
        recyclerView = findViewById(R.id.announcementsRecyclerView);
        backbtn = findViewById(R.id.backbtnAnnouncemnt);
        nonet = findViewById(R.id.no_net_announce);
        shimmerFrameLayout = findViewById(R.id.shimmer_announce);
        lbl = findViewById(R.id.lbl_announce_net);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayPost();
            }
        },1000);

        check = new check_network();
        orgBrought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnnouncementMenu.this, StudentListOrg.class);
                startActivity(intent);
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void displayPost(){
        announcementsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Announcements");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Announcements announcements = dataSnapshot.getValue(Announcements.class);
                        announcementsList.add(announcements);
                    }
                    check.hideshimmer(shimmerFrameLayout,null,null);
                    nonet.setVisibility(View.GONE);
                    lbl.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    announcementAdapter = new AnnouncementAdapter(AnnouncementMenu.this, announcementsList);
                    recyclerView.setAdapter(announcementAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(AnnouncementMenu.this));
                }else {
                    check.hideshimmer(shimmerFrameLayout,null,null);
                    lbl.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        check.isConnected(this,nonet,lbl,recyclerView,shimmerFrameLayout,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        check.hideshimmer(shimmerFrameLayout,null,null);
    }
}