package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.OrgListAdapter;
import com.example.loadingscreen.win.model.UserList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.example.loadingscreen.R;

import java.util.ArrayList;

public class OrgList extends AppCompatActivity {

    RecyclerView orgRecyclerView;
    OrgListAdapter adapter;
    ArrayList<usersList_model> orgs1;
    DatabaseReference userRef;
    EditText editSearchOrg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_list);
        initView();
    }

    private void initView() {

        Toolbar orgToolbar = findViewById(R.id.orgToolBarList);


        userRef = FirebaseDatabase.getInstance().getReference().child("Organizations");
        orgRecyclerView = findViewById(R.id.orgListRecyclerView);
        editSearchOrg = findViewById(R.id.editSearchOrg);
        orgs1 = new ArrayList<>();
        orgRecyclerView.setLayoutManager(new LinearLayoutManager(getParent()));

        orgToolbar.setNavigationIcon(R.drawable.arrow_back);
        orgToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrgList.this, AdminPanel.class);
                startActivity(intent);
            }
        });

        editSearchOrg.addTextChangedListener(new TextWatcher() {
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
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        usersList_model user = snap.getValue(usersList_model.class);
                        orgs1.add(user);
                    }

                    adapter = new OrgListAdapter(OrgList.this, orgs1);
                    adapter.notifyDataSetChanged();
                    orgRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void filter(String text) {
        ArrayList<usersList_model> filteredOrg = new ArrayList<>();
        for (usersList_model item : orgs1) {
            if (item.getFullname().toLowerCase().contains(text.toLowerCase())) {
                filteredOrg.add(item);
            }
        }

        adapter.filterList(filteredOrg);
    }
}