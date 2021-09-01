package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.classSearchAdapter;
import com.example.loadingscreen.model.class_sched_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class toEditclass extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<class_sched_model> classList;
    private classSearchAdapter adapter;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SearchView searchView;
    String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_editclass);
        recyclerView = findViewById(R.id.rv_toeditclass);
        radioGroup = findViewById(R.id.rg_toeditclass);
        searchView = findViewById(R.id.sv_toeditclass);
        searchView.requestFocus();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchClass(s);
                return false;
            }
        });
    }

    public void get_course(View view) {
        int id = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(id);
        course = radioButton.getText().toString();
    }

    public void searchClass(final String text) {
        if (course != null) {
            classList = new ArrayList<>();
            final String tag = "toedit";
            Query query = FirebaseDatabase.getInstance().getReference("Schedule").child("Class")
                    .child(course)
                    .orderByChild("yearsection")
                    .startAt(text)
                    .endAt(text + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            class_sched_model sched = dataSnapshot.getValue(class_sched_model.class);
                            classList.add(sched);
                        }
                        adapter = new classSearchAdapter(toEditclass.this,tag);
                        adapter.filteredList(classList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(toEditclass.this));
                    } else {
                        Toast.makeText(toEditclass.this, "No result found", Toast.LENGTH_LONG).show();
                        classList.clear();
                        adapter = new classSearchAdapter(toEditclass.this,tag);
                        adapter.filteredList(classList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(toEditclass.this));
                    }
                    if (text.length() == 0){
                        classList.clear();
                        adapter = new classSearchAdapter(toEditclass.this,tag);
                        adapter.filteredList(classList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(toEditclass.this));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(this, "BSIT or BLIS?", Toast.LENGTH_SHORT).show();
        }
    }
}