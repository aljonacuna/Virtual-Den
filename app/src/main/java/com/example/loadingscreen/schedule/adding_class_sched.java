package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;

import com.example.loadingscreen.Utils.permissionUtils;

import com.example.loadingscreen.model.class_sched_model;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class adding_class_sched extends AppCompatActivity implements View.OnClickListener {
    private Button save_btn;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private StorageReference storageReference;
    private DatabaseReference reference;
    String course = "BSIT", group = "G1";
    private TextInputLayout yearsectionTIL;
    private TextInputEditText textInputEditText;
    private TextView successlbl;
    private ProgressBar progressBar;
    private RadioButton gRb;
    private RadioGroup gRg;
    private TextInputEditText yearsectionACTV;
    private ImageView closebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_class_sched);
        save_btn = findViewById(R.id.save_classschedbtn);
        yearsectionTIL = findViewById(R.id.yearsection);
        yearsectionACTV = findViewById(R.id.ysACTV);
        textInputEditText = findViewById(R.id.ysACTV);
        gRg = findViewById(R.id.gRG);
        closebtn = findViewById(R.id.adding_classclosebtn);
        progressBar = findViewById(R.id.add_class_pb);
        successlbl = findViewById(R.id.successlbl_addclass);
        radioGroup = findViewById(R.id.adding_class_schedRG);
        permissionUtils.isPermissionGranted(this);
        save_btn.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference("ClassSched");
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                successlbl.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void courserb(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        course = radioButton.getText().toString().toUpperCase();
        successlbl.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        if (view == save_btn) {
            saveclass_sched();
        }else if (view == closebtn){
            finish();
        }
    }

    public void groupRB(View view) {
        int radioID = gRg.getCheckedRadioButtonId();
        gRb = findViewById(radioID);
        group = gRb.getText().toString().toUpperCase();
        successlbl.setVisibility(View.GONE);
    }

    public boolean isYS() {
        String year_section = yearsectionTIL.getEditText().getText().toString();
        if (TextUtils.isEmpty(year_section)) {
            Toast.makeText(this, "Please enter year and section", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void saveclass_sched() {
        if (!isYS()) {
            return;
        } else {
            final ArrayList<String>classList = new ArrayList<>();
            final String year_section = yearsectionTIL.getEditText().getText().toString().toUpperCase();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child(course);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot :snapshot.getChildren()) {
                        String groupData = dataSnapshot.child("group").getValue(String.class);
                        String yearsectionData = dataSnapshot.child("yearsection").getValue(String.class);
                        String yeargroup = yearsectionData + " " + groupData;
                        if (groupData.equals(group) && yearsectionData.equals(year_section)) {
                            classList.add(yeargroup);
                        }
                    }
                        String compareYearGroup = year_section+" "+group;
                        if (classList.contains(compareYearGroup)){
                            Toast.makeText(adding_class_sched.this, "This room already exist!", Toast.LENGTH_SHORT).show();
                            successlbl.setVisibility(View.GONE);
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                            String sched = "";
                            String date = simpleDateFormat.format(Calendar.getInstance().getTime());
                            String label = "dateposted";
                            DatabaseReference getKey = FirebaseDatabase.getInstance().getReference("Schedule")
                                    .child("Class").child(course).push();
                            String classKey = getKey.getKey();
                            reference = FirebaseDatabase.getInstance().getReference("Schedule").child("Class")
                                    .child(course).child(classKey);
                            class_sched_model classSave = new class_sched_model(sched, year_section,
                                    group, course, classKey, label + "/" + date, "",
                                    "No schedule update for this class schedule");
                            textInputEditText.setText("");
                            reference.setValue(classSave);
                            progressBar.setVisibility(View.GONE);
                            successlbl.setText(course + " " + year_section + "-" + group + " successfully added!");
                            successlbl.setVisibility(View.VISIBLE);
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

