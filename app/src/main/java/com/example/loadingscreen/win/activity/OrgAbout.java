package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.loadingscreen.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrgAbout extends AppCompatActivity {

    DatabaseReference aboutRef;
    FirebaseAuth myAuth;
    String currentUser;
    TextView aboutTextView;
    Button saveeditbtn;
    EditText aboutEdit;
    String id, name, img, count;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_about);
        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser().getUid();
        aboutTextView = findViewById(R.id.aboutTextView);
        aboutEdit = findViewById(R.id.aboutEdit);
        saveeditbtn = findViewById(R.id.saveeditbtn);
        Toolbar aboutToolBar = findViewById(R.id.aboutToolBar);
        aboutToolBar.setNavigationIcon(R.drawable.arrow_back);
        if (getIntent().getExtras() != null) {
            saveeditbtn.setVisibility(View.GONE);
            id = getIntent().getExtras().getString("id");
            count = getIntent().getExtras().getString("count");
            name = getIntent().getExtras().getString("name");
            img = getIntent().getExtras().getString("img");
            aboutRef = FirebaseDatabase.getInstance().getReference().child("Organizations").child(id);
        } else {
            saveeditbtn.setVisibility(View.VISIBLE);
            aboutRef = FirebaseDatabase.getInstance().getReference().child("Organizations").child(currentUser);
        }

        aboutToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.equals(id)) {
                    finish();
                } else {
                    finish();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aboutRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String aboutStr = snapshot.child("orgMission").getValue().toString().trim();
                            aboutEdit.setText(aboutStr);
                            aboutTextView.setText(aboutStr);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrgAbout.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 1000);


        saveeditbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String btnText = saveeditbtn.getText().toString();
                if (btnText.equals("SAVE")) {
                    try {
                        saveData();
                        saveeditbtn.setText("EDIT");
                        saveeditbtn.setBackgroundResource(R.drawable.buttonshapeclear);
                        saveeditbtn.setTextColor(Color.parseColor("#4B8BBE"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        aboutTextView.setVisibility(View.GONE);
                        aboutEdit.setVisibility(View.VISIBLE);
                        saveeditbtn.setText("SAVE");
                        saveeditbtn.setTextColor(Color.parseColor("#FFFFFF"));
                        saveeditbtn.setBackgroundResource(R.drawable.butttonradiusstaff);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void saveData() {
        aboutRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    aboutRef.child("orgMission").setValue(aboutEdit.getText().toString().trim());
                    aboutTextView.setVisibility(View.VISIBLE);
                    aboutEdit.setVisibility(View.GONE);
                    Toast.makeText(OrgAbout.this, "Saved!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrgAbout.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}