package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.OrgUsers_model;
import com.example.loadingscreen.win.Adapter.OfficerOrgAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentViewOfficers extends AppCompatActivity {
    RecyclerView officerRecyclerView;
    LinearLayoutManager linearLayoutManager;
    private OfficerOrgAdapter adapter;
    private ArrayList<OrgUsers_model> users;
    private DatabaseReference orgUserRef,userRef,orgUsers1;;
    FirebaseAuth myAuth;
    String currentOrg;
    TextView officerCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_offficers);
        officerRecyclerView= findViewById(R.id.studViewOfficers_rv);
        officerCount= findViewById(R.id.officerCount);
        linearLayoutManager=new LinearLayoutManager(this);
        officerRecyclerView.setLayoutManager(linearLayoutManager);
        myAuth=FirebaseAuth.getInstance();
        currentOrg=myAuth.getCurrentUser().getUid();
        String id = getIntent().getExtras().getString("id");
        orgUserRef= FirebaseDatabase.getInstance().getReference().child("OrgUsers").child(id);
        userRef= FirebaseDatabase.getInstance().getReference().child("userstbl");
        orgUsers1=FirebaseDatabase.getInstance().getReference().child("OrgUsers").child(id);
    }
    @Override
    public void onStart() {
        super.onStart();
        users = new ArrayList<>();
        orgUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrgUsers_model orguser = dataSnapshot.getValue(OrgUsers_model.class);
                    users.add(orguser);
                }
                adapter = new OfficerOrgAdapter(StudentViewOfficers.this,users,"tag");
                officerRecyclerView.setAdapter(adapter);
                officerRecyclerView.setLayoutManager(new LinearLayoutManager(StudentViewOfficers.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}