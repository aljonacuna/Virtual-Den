package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Revise.UpdateClassSched;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class facultypanel extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout addroombtn, myschedtebtn, addclassbtn;
    private ImageView backbtn;
    String label = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultypanel);
        addroombtn = findViewById(R.id.addroombtn);
        myschedtebtn = findViewById(R.id.updatemyschedbtn);
        addclassbtn = findViewById(R.id.addclassbtn);
        backbtn = findViewById(R.id.back_btnfacpanel);
        backbtn.setOnClickListener(this);
        addclassbtn.setOnClickListener(this);
        addroombtn.setOnClickListener(this);
        myschedtebtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == addroombtn) {
            Intent intent = new Intent(this, updateroomsched.class);
            startActivity(intent);
        } else if (view == addclassbtn) {
            Intent intent = new Intent(facultypanel.this, UpdateClassSched.class);
            startActivity(intent);
        } else if (view == myschedtebtn) {
            getLabel();
        }else if (view == backbtn){
            finish();
        }
    }

    private void getLabel() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference labelRef = FirebaseDatabase.getInstance().getReference("Faculty")
                .child(firebaseUser.getUid());
        labelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String schedCheck = snapshot.child("sched").getValue(String.class);
                if (schedCheck.equals(""))
                    label = "add";
                else
                    label = "update";
                Intent intent = new Intent(facultypanel.this, adding_faculty_sched.class);
                intent.putExtra("label", label);
                startActivity(intent);
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