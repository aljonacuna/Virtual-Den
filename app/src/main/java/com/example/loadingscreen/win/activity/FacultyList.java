package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.FacultyListAdapter;
import com.example.loadingscreen.win.model.UserList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.loadingscreen.R;

import java.util.ArrayList;

public class FacultyList extends AppCompatActivity {
    ArrayList<usersList_model> facults;
    RecyclerView profRecyclerView;
    FacultyListAdapter adapter;
    DatabaseReference userRef;
    EditText editSearchFaculty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_list);

        initView();
    }

    private void initView() {

        Toolbar profToolBar = (Toolbar) findViewById(R.id.facultyToolBar);
        editSearchFaculty = findViewById(R.id.editSearchFaculty);
        profRecyclerView = findViewById(R.id.facultyListRecyclerView);
        facults = new ArrayList<>();
        profRecyclerView.setLayoutManager(new LinearLayoutManager(getParent()));
        userRef = FirebaseDatabase.getInstance().getReference().child("Faculty");

        profToolBar.setNavigationIcon(R.drawable.arrow_back);
        profToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyList.this, AdminPanel.class);
                startActivity(intent);
            }
        });

        editSearchFaculty.addTextChangedListener(new TextWatcher() {
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
                        facults.add(user);
                    }
                }
                adapter = new FacultyListAdapter(FacultyList.this, facults);
                adapter.notifyDataSetChanged();
                profRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void filter(String text) {
        ArrayList<usersList_model> filteredFac = new ArrayList<>();
        for (usersList_model item : facults) {
            if (item.getFullname().toLowerCase().contains(text.toLowerCase())) {
                filteredFac.add(item);
            }

        }

        adapter.filterList(filteredFac);

    }
}