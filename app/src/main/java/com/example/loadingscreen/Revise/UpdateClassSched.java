package com.example.loadingscreen.Revise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.loadingscreen.Utils.arrfordd.dialog;

public class UpdateClassSched extends AppCompatActivity implements View.OnClickListener {
    private Button update_btn;
    private ImageView backbtn;
    private AutoCompleteTextView group_ACT, year_section_ACT;
    private TextInputLayout choose_yearsection, choose_group;
    private RadioGroup rg_choose_course;
    private RadioButton radioButton;
    String course = "BSIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_class_sched);
        year_section_ACT = findViewById(R.id.year_section_ACT);
        rg_choose_course = findViewById(R.id.rgChooseCourse);
        choose_yearsection = findViewById(R.id.choose_year_section);
        update_btn = findViewById(R.id.update_btn_class);
        backbtn = findViewById(R.id.update_class_backbtn);
        backbtn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        AdapterForDropDownYearSection();

    }

    public void choose_course_update(View view) {
        int id = rg_choose_course.getCheckedRadioButtonId();
        radioButton = findViewById(id);
        course = radioButton.getText().toString();
        AdapterForDropDownYearSection();
    }

    @Override
    public void onClick(View view) {
        if (view == update_btn) {
            update_class();
        }else if (view == backbtn){
            finish();
        }
    }

    private void AdapterForDropDownYearSection() {
        final ArrayList<String> yearsectionList = new ArrayList<>();
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Schedule")
                .child("Class").child(course);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String yearsection = dataSnapshot.child("yearsection").getValue(String.class);
                    String group = dataSnapshot.child("group").getValue(String.class);
                    String ysg = yearsection + "-" + group;
                    yearsectionList.add(ysg);
                }
                ArrayAdapter<String> ysAdapter = new ArrayAdapter<>(UpdateClassSched.this,
                        R.layout.dropdownlayout, yearsectionList);
                year_section_ACT.setAdapter(ysAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void update_class() {
        final String ysgText = choose_yearsection.getEditText().getText().toString();
        String[] str = ysgText.split("-");
        final String yearsectionText = str[0];
        final String groupText = str[1];
        DatabaseReference databaseReference;
        if (course.equals("")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child("BSIT");
            course = "BSIT";
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child(course);
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String groupData = dataSnapshot.child("group").getValue(String.class);
                    String yearsectionData = dataSnapshot.child("yearsection").getValue(String.class);
                    if (groupData.equals(groupText) && yearsectionText.equals(yearsectionData)) {
                        final String id = dataSnapshot.child("key").getValue(String.class);
                        arrfordd getdialog = new arrfordd();
                        String title = "Edit schedule";
                        String message = "Are you sure that ";
                        String message1 = " is one of your advisory class?";
                        getdialog.dialog_conf(UpdateClassSched.this, title, message, ysgText, message1,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(UpdateClassSched.this, UpdateClassSchedSecond.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("group",groupText);
                                        intent.putExtra("yearsec",yearsectionText);
                                        intent.putExtra("course", course);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                    }

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
        finish();
    }
}