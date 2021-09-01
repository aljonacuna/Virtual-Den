package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loadingscreen.win.model.Students;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import com.example.loadingscreen.R;

public class Registration extends AppCompatActivity {
    Button signUpNow,signInBtnReg,calendarBtn;
    EditText editFullName,editEmail,editPassword,editConfirmPassword,editStudentNumber,editBirthday;
    CheckBox checkBoxTerms;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatabaseReference studentReference,studNumberRef;
    private FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        myAuth=FirebaseAuth.getInstance();
        studentReference= FirebaseDatabase.getInstance().getReference().child("Students");
        studNumberRef=FirebaseDatabase.getInstance().getReference().child("Students");
        editFullName=findViewById(R.id.editFullName);
        editStudentNumber=findViewById(R.id.editStudentNumber);
        editBirthday=findViewById(R.id.editBirthday);

        calendarBtn=findViewById(R.id.calendarBtn);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        signUpNow=findViewById(R.id.signUpNowBtn);
        signUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, SuccessSignup.class);
                startActivity(intent);
                signUpMethod();
            }
        });
    }
    //sign up method
    private void signUpMethod(){
        checkBoxTerms=findViewById(R.id.checkBoxTerms);
        final String fullName=editFullName.getText().toString().trim();
        final String email=editEmail.getText().toString().trim();
        final String password=editPassword.getText().toString().toLowerCase().trim();
        final String imgDefault="https://firebasestorage.googleapis.com/v0/b/virtual-den2020.appspot.com/o/" +
                "uploads%2Fuser.png?alt=media&token=86ca1800-2289-4860-8972-d000c47ae661";
        final String confirmPassword=editConfirmPassword.getText().toString().toLowerCase().trim();
        final String studentNumber=editStudentNumber.getText().toString().trim();
        final String birthday=editBirthday.getText().toString().trim();

        if(!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(studentNumber)
                && !TextUtils.isEmpty(birthday)) {
            if(password.length()<6){
                Toast.makeText(this,"Password too short!",Toast.LENGTH_LONG).show();
            }
            else{
                studNumberRef.orderByChild("studentNumber").equalTo(studentNumber).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Toast.makeText(Registration.this,"Student number alread used!",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    if(checkBoxTerms.isChecked()) {
                                        if (confirmPassword.equals(password)) {
                                            myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                                                    (new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {

                                                                Students student = new Students(fullName, email, birthday,
                                                                        imgDefault, " ", studentNumber, " ",
                                                                        " ", " ", " ",
                                                                        "No",
                                                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                studentReference.child(FirebaseAuth.getInstance().
                                                                        getCurrentUser().getUid()).setValue(student)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Intent intent = new Intent(Registration.this,
                                                                                            SuccessSignup.class);
                                                                                    intent.putExtra("Email", email);
                                                                                    startActivity(intent);
                                                                                } else {
                                                                                    Toast.makeText(Registration.this, "Failed to register!",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });

                                                            } else {
                                                                Toast.makeText(Registration.this, "Invalid or taken email!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(Registration.this, "Password not match!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(Registration.this, "Check agree to terms and conditions!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }
        else{
            Toast.makeText(this,"Please complete fields!",Toast.LENGTH_SHORT).show();
        }

    }//end sign up method

    private void pickDate(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editBirthday.setText( (month + 1) + "/"+dayOfMonth  + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}