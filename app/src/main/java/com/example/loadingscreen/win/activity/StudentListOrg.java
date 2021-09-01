package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.OrgStudListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.loadingscreen.Utils.sharedpref.orgRef;

public class StudentListOrg extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private OrgStudListAdapter adapter;
    private ArrayList<usersList_model>orgsList;
    private ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list_org);
        backbtn = findViewById(R.id.orgs_backbtn);
        backbtn.setOnClickListener(this);
        recyclerView = findViewById(R.id.orgs_Rv);
        displayOrg();
    }
    public void displayOrg(){
        orgsList = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(orgRef);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    usersList_model newOrgs = dataSnapshot.getValue(usersList_model.class);
                    orgsList.add(newOrgs);
                }
                adapter = new OrgStudListAdapter(StudentListOrg.this,orgsList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(StudentListOrg.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn){
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}