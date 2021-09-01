package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.activity_gui.newmessage;
import com.example.loadingscreen.adapter.newmsgAdapter;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.UserListAdapter;
import com.example.loadingscreen.win.fragment.OfficerOrgFragment;
import com.example.loadingscreen.win.model.UserList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import com.example.loadingscreen.R;

public class AddOfficer extends AppCompatActivity {
    private DatabaseReference userRef, userOrgRef;
    private RecyclerView userRecyclerView;
    private UserListAdapter adapter;
    private ArrayList<usersList_model> users;
    EditText editSearchName;
    FirebaseAuth myAuth;
    String currentOrg;
    String user_type = "";
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ArrayList<usersList_model> filteredList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_officer);
        initView();
    }

    private void initView() {
        myAuth = FirebaseAuth.getInstance();
        currentOrg = myAuth.getCurrentUser().getUid();
        editSearchName = findViewById(R.id.editSearchName);
        userRecyclerView = findViewById(R.id.userSearchRecyclerView);
        userOrgRef = FirebaseDatabase.getInstance().getReference().child(currentOrg);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        radioGroup = findViewById(R.id.rg_typeofuser);
        filteredList = new ArrayList<>();
//search text
        editSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });

//end search text
    }

    public void filter(final String text) {
        Query query;
        if (user_type.equals(""))
            query = FirebaseDatabase.getInstance().getReference("Student")
                    .orderByChild("fullname")
                    .startAt(text)
                    .endAt(text + "\uf8ff");
        else
            query = FirebaseDatabase.getInstance().getReference(user_type)
                    .orderByChild("fullname")
                    .startAt(text)
                    .endAt(text + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren() || snapshot.exists()) {
                    filteredList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        usersList_model newuser = dataSnapshot.getValue(usersList_model.class);
                        filteredList.add(newuser);
                    }
                    adapter = new UserListAdapter(AddOfficer.this, users);
                    adapter.filterList(filteredList);
                    userRecyclerView.setAdapter(adapter);
                    userRecyclerView.setLayoutManager(new LinearLayoutManager(AddOfficer.this));

                } else {
                    if (filteredList != null) {
                        filteredList.clear();
                        adapter = new UserListAdapter(AddOfficer.this, users);
                        adapter.filterList(filteredList);
                        userRecyclerView.setAdapter(adapter);
                        userRecyclerView.setLayoutManager(new LinearLayoutManager(AddOfficer.this));
                        Toast.makeText(AddOfficer.this, "Please enter correct  name", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddOfficer.this, "Please enter correct  name", Toast.LENGTH_SHORT).show();
                    }
                }
                if (text.length() == 0) {
                    filteredList.clear();
                    adapter = new UserListAdapter(AddOfficer.this, users);
                    adapter.filterList(filteredList);
                    userRecyclerView.setAdapter(adapter);
                    userRecyclerView.setLayoutManager(new LinearLayoutManager(AddOfficer.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void user_type(View view) {
        int id = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(id);
        user_type = radioButton.getText().toString();
    }
}