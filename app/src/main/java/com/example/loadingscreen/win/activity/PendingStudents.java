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
import android.widget.Toast;

import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.PendingStudAdapter;
import com.example.loadingscreen.win.model.Students;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.loadingscreen.R;

import java.util.ArrayList;

public class PendingStudents extends AppCompatActivity {

    private DatabaseReference pendstudentsRef;
    RecyclerView pendingStudRecyclerView;
    PendingStudAdapter adapter;
    ArrayList<usersList_model> pendstudents;
    EditText editSearchStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_students);

        initView();
    }

    private void initView() {
        Toolbar pendingToolBar = (Toolbar) findViewById(R.id.pendingToolBar);
        pendingToolBar.setNavigationIcon(R.drawable.arrow_back);

        pendingStudRecyclerView = findViewById(R.id.pendingStudentsRecyclerView);
        editSearchStudent = findViewById(R.id.editSearchStudent);
        pendingStudRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendstudentsRef = FirebaseDatabase.getInstance().getReference().child("Student");

        pendstudents = new ArrayList<usersList_model>();
        pendingToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PendingStudents.this, AdminStudents.class);
                startActivity(intent);
            }
        });

        editSearchStudent.addTextChangedListener(new TextWatcher() {
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
        pendstudentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        usersList_model student1 = snap.getValue(usersList_model.class);
                        pendstudents.add(student1);
                    }
                }
                adapter = new PendingStudAdapter(PendingStudents.this, pendstudents);
                adapter.notifyDataSetChanged();
                pendingStudRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingStudents.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void filter(String text) {
        ArrayList<usersList_model> filteredStud = new ArrayList<>();
        for (usersList_model item : pendstudents) {
            if (item.getFullname().toLowerCase().contains(text.toLowerCase()) ||
                    item.getIdnum().toLowerCase().contains(text.toLowerCase())) {
                filteredStud.add(item);
            }
        }

        adapter.filterList(filteredStud);
    }
}