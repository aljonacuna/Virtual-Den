package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.win.activity.SuccessSignup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class signup2 extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button finishbtn, calendar;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private TextInputLayout fnameText, bod, studno;
    private TextInputEditText bodEt;
    public static final String signup = "SignupEmailAddress";
    public static final String filename = "Login";
    public static final String idAttemptFile = "studid";
    public static final String id_Attempt = "idattempt";
    private TextView errorMsgGender, termstv, loginbtn;
    private String bg_imgtemp = "none";
    private String profilepic_temp = "none";
    SharedPreferences preferences;
    SharedPreferences idattemptpref;
    String genderText = "Male";
    String studnum, email, pass, userType, monthstr, daystr;
    private ArrayList<String> idnumList;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        progressDialog = new ProgressDialog(this);
        fnameText = findViewById(R.id.fullname);
        studno = findViewById(R.id.studnos);
        bod = findViewById(R.id.bod);

        termstv = findViewById(R.id.terms);
        termstv.setPaintFlags(termstv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        finishbtn = findViewById(R.id.finishbtn);
        bodEt = findViewById(R.id.bodET);
        preferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
        idattemptpref = getSharedPreferences(idAttemptFile, Context.MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference("userstbl");
        radioGroup = findViewById(R.id.radioGroup);
        errorMsgGender = findViewById(R.id.errorMsgGender);
        checkBox = findViewById(R.id.checkBox);
        loginbtn = findViewById(R.id.login_btnsecond);
        loginbtn.setPaintFlags(loginbtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        calendar = findViewById(R.id.calendar_button);
        email = getIntent().getExtras().getString("email");
        pass = getIntent().getExtras().getString("pass");
        userType = getIntent().getExtras().getString("usertype");
        finishbtn.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        finishbtn.setEnabled(false);
        checkBox.setOnClickListener(this);
        calendar.setOnClickListener(this);
        loginbtn.setOnClickListener(this);
        termstv.setOnClickListener(this);

    }

    public void genderbtn(View view) {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        genderText = radioButton.getText().toString();
    }

    private boolean errorTrappingGender() {
        if (genderText.equals("")) {
            errorMsgGender.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorMsgGender.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean errorTrappingName() {
        String f_name = fnameText.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(f_name)) {
            fnameText.setError("Required Name");
            fnameText.requestFocus();
            return false;
        } else {
            fnameText.setError(null);
            return true;
        }
    }

    private boolean errorTrappingBod() {
        String inputBod = bod.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(inputBod)) {
            bod.setError("Required Birth of date");
            bod.requestFocus();
            return false;
        } else {
            bod.setError(null);
            return true;
        }
    }

    private boolean errorTrappingStudno() {
        String studnos = studno.getEditText().getText().toString();
        if (TextUtils.isEmpty(studnos)) {
            studno.setError("Please enter your student number");
            return false;
        } else {
            studno.setError(null);
            return true;
        }
    }

    public void signupStud() {
        if (counter < 3) {
            if (!errorTrappingName() || !errorTrappingBod() || !errorTrappingGender() || !errorTrappingStudno()) {
                if (!genderText.equals("")) {
                    errorMsgGender.setVisibility(View.GONE);
                }
                return;
            } else {
                final String status = "";
                final String fname = fnameText.getEditText().getText().toString().trim();
                final String birth = bod.getEditText().getText().toString();
                studnum = studno.getEditText().getText().toString();
                idnumList = new ArrayList<>();
                Query ifidexist = FirebaseDatabase.getInstance().getReference("Student")
                        .orderByChild("idnum").equalTo(studnum);
                ifidexist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(signup2.this, "Student number already used", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference checkIdnumref = FirebaseDatabase.getInstance().getReference("Idnum");
                            checkIdnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String idnum = dataSnapshot.child("idnum").getValue(String.class);
                                        idnumList.add(idnum);
                                    }
                                    if (idnumList.contains(studnum)) {
                                        createUser(status, fname, birth);
                                    } else {
                                        counter = counter + 1;
                                        Toast.makeText(signup2.this, "Please enter valid id number!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        } else {
            long timer = System.currentTimeMillis() + 1800000;
            SharedPreferences.Editor editor = idattemptpref.edit();
            editor.putLong(id_Attempt, timer);
            editor.commit();
            Toast.makeText(this, "Consecutive error : Please use your original student number, " +
                    "you will be unable to signup for 30 mins ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(signup2.this, signin.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    public void createUser(final String status, final String fname, final String birth) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialogforsignup);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(signup2.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest dispname = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fname)
                                    .build();
                            firebaseUser.updateProfile(dispname);
                            final String userID = firebaseUser.getUid();
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("userType", "Student");
                            userData.put("isVerify","no");
                            FirebaseDatabase.getInstance().getReference("userstbl").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    userData(fname, birth, status, userID);
                                    emailVerify();

                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(signup2.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }


                    }
                });
    }

    public void emailVerify() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            progressDialog.dismiss();
                            Intent intent = new Intent(signup2.this, SuccessSignup.class);
                            intent.putExtra("Email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(signup2.this, "Error: " + task.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void userData(String fname, String bod, String status, String userID) {
        DatabaseReference datarefernce = FirebaseDatabase.getInstance().getReference("Student")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String imgDefault = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
        HashMap<String, Object> user_Data = new HashMap<>();
        user_Data.put("emailadd", email);
        user_Data.put("idnum", studnum);
        user_Data.put("userID", userID);
        user_Data.put("fullname", fname);
        user_Data.put("birthday", bod);
        user_Data.put("gender", genderText);
        user_Data.put("bgimg", bg_imgtemp);
        user_Data.put("about", "");
        user_Data.put("profileimg", imgDefault);
        user_Data.put("status", status);
        user_Data.put("isDisabled", "No");
        user_Data.put("userType", "Student");
        user_Data.put("yearsection", "");
        user_Data.put("group", "");
        user_Data.put("sname",fname.toLowerCase());
        datarefernce.setValue(user_Data);
    }

    @Override
    public void onClick(View view) {
        if (view == finishbtn) {
            signupStud();
        } else if (view == loginbtn) {
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
            finish();
        } else if (view == termstv) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.termspopup);
            ImageView btnclose = dialog.findViewById(R.id.close_terms);
            btnclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if (view == checkBox) {
            if (checkBox.isChecked()) {
                finishbtn.setEnabled(true);
                finishbtn.setBackgroundResource(R.drawable.buttonshape_yellow);
            } else {
                finishbtn.setEnabled(false);
                finishbtn.setBackgroundResource(R.drawable.buttondisable);
            }
        } else if (view == calendar) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                    , new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    int month = i1 + 1;
                    if (month >= 10 && i2 < 10) {
                        bodEt.setText(month + "/" + "0" + i2 + "/" + i);
                    } else if (month < 10 && i2 >= 10) {
                        bodEt.setText("0" + month + "/" + i2 + "/" + i);
                    } else if (month < 10 && i2 < 10) {
                        bodEt.setText("0" + month + "/" + "0" + i2 + "/" + i);
                    } else {
                        bodEt.setText(month + "/" + i2 + "/" + i);
                    }
                }
            },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    Calendar.getInstance().get(Calendar.MONTH));
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();

        }
    }
}