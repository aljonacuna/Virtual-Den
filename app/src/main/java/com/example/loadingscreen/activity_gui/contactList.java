package com.example.loadingscreen.activity_gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.contactAdapter;
import com.example.loadingscreen.model.contact_list;
import com.example.loadingscreen.model.usersList_model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class contactList extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ArrayList<contact_list> contactLists;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<usersList_model> user_list;
    private contactAdapter contact_adapter;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        recyclerView = findViewById(R.id.recycler_contact);
        floatingActionButton = findViewById(R.id.newmsgbtn);
        floatingActionButton.setOnClickListener(this);
        loadContact();

    }
    public void loadContact(){
        contactLists = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUser = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("ContactList").child(currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                contactLists.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        contact_list contact = dataSnapshot.getValue(contact_list.class);
                        contactLists.add(contact);
                    }
                    Collections.sort(contactLists,Collections.reverseOrder(new Comparator<contact_list>() {
                        @Override
                        public int compare(contact_list contact_list1, contact_list contact_list2) {
                            return contact_list1.getTimestamp().compareTo(contact_list2.getTimestamp());
                        }
                    }));
                    contact_adapter = new contactAdapter(contactList.this,contactLists);
                    recyclerView.setAdapter(contact_adapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(contactList.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, dimunadelete.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == floatingActionButton){
            Intent intent = new Intent(this,newmessage.class);
            startActivity(intent);
        }
    }
}