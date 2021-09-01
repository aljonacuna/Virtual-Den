package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.schedule.addRoom;
import com.example.loadingscreen.schedule.adding_class_sched;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.loadingscreen.activity_gui.signin.emailAdd;
import static com.example.loadingscreen.activity_gui.signin.fileName;
import static com.example.loadingscreen.activity_gui.signin.password;

public class AdminPanel extends AppCompatActivity {

    TextView settingsBtn, studlbl, unverifiedlbl, facultylbl, orglbl;
    CardView studentsCardView, registerProf, registerOrg,
            profList, orgList, addclassched,addroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        settingsBtn = findViewById(R.id.settingsBtn);
        studlbl = findViewById(R.id.stud_count);
        studentsCardView = findViewById(R.id.studentsCardView);
        registerProf = findViewById(R.id.registerProf);
        profList = findViewById(R.id.profList);
        orgList = findViewById(R.id.orgList);
        registerOrg = findViewById(R.id.registerOrg);
        addroom = findViewById(R.id.roomAdd);
        facultylbl = findViewById(R.id.facultyCount);
        orglbl = findViewById(R.id.orgCount);
        addclassched = findViewById(R.id.schedAdd);

        getStudentAndPendingStudentCount();
        getFacultycount();
        getOrgcount();
        addroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPanel.this, addRoom.class);
                startActivity(intent);
            }
        });
        studentsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, AdminStudents.class);
                startActivity(intent);
            }
        });

        profList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, FacultyList.class);
                startActivity(intent);
            }
        });
        orgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, OrgList.class);
                startActivity(intent);
            }
        });
        registerProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, RegisterProf.class);
                startActivity(intent);
                finish();
            }
        });
        registerOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, RegisterOrg.class);
                startActivity(intent);
                finish();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser.getDisplayName().equals("admin")) {
                    Intent intent = new Intent(AdminPanel.this, AdminSettings.class);
                    intent.putExtra("tagset", "admin");
                    startActivity(intent);
                }else{
                    firebaseAuth.signOut();
                    Intent intent = new Intent(AdminPanel.this, AdminSettings.class);
                    intent.putExtra("tagset", "admin");
                    startActivity(intent);
                }
            }
        });
        addclassched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPanel.this, adding_class_sched.class);
                startActivity(intent);
            }
        });
    }

    private void getStudentAndPendingStudentCount() {
        final ArrayList<String> listofPending = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Student");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int studcount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    studcount = studcount + 1;
                }
                String tostrCount = String.valueOf(studcount);
                studlbl.setText(tostrCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOrgcount() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Organizations");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long countorg = snapshot.getChildrenCount();
                String strcountorg = String.valueOf(countorg);
                orglbl.setText(strcountorg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFacultycount() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Faculty");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long countfaculty = snapshot.getChildrenCount();
                String strcountfaculty = String.valueOf(countfaculty);
                facultylbl.setText(strcountfaculty);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}