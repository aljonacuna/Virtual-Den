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
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.StudentListAdapter;
import com.example.loadingscreen.win.model.Students;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import com.example.loadingscreen.R;

public class AdminStudents extends AppCompatActivity {

    private DatabaseReference studentsRef;
    RecyclerView studentsRecyclerView;
    StudentListAdapter adapter;
    ArrayList<usersList_model> students;
    EditText editSearchStudent;
    TextView pendingStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_students);
        initView();
    }

    private void initView() {
        Toolbar studentsToolBar = (Toolbar) findViewById(R.id.studentsToolBar);
        studentsToolBar.setNavigationIcon(R.drawable.arrow_back);

        studentsRecyclerView = findViewById(R.id.studentListRecyclerView);
        editSearchStudent = findViewById(R.id.editSearchStudent);
        //pendingStudents=findViewById(R.id.pendingStudents);

        /**pendingStudents.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        Intent intent = new Intent(AdminStudents.this, PendingStudents.class);
        startActivity(intent);
        }
        });*///
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRef = FirebaseDatabase.getInstance().getReference().child("Student");

        students = new ArrayList<usersList_model>();
        studentsToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminStudents.this, AdminPanel.class);
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
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    students.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        usersList_model student = snap.getValue(usersList_model.class);
                        students.add(student);
                    }

                }
                adapter = new StudentListAdapter(AdminStudents.this, students);
                adapter.notifyDataSetChanged();
                studentsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminStudents.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void filter(String text) {
        ArrayList<usersList_model> filteredStud = new ArrayList<>();
        for (usersList_model item : students) {
            if (item.getFullname().toLowerCase().contains(text.toLowerCase())) {
                filteredStud.add(item);
            }
        }

        adapter.filterList(filteredStud);
    }
}