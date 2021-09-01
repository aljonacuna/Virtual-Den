package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.Utils.sharedpref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.loadingscreen.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class RegisterProf extends AppCompatActivity implements View.OnClickListener {
    EditText editProfFullName, editProfEmail, editProfPass, editConfirmPass, idNumTv;
    DatabaseReference facultyRef, usersRef;
    Button registerProfBtn;
    FirebaseAuth myAuth;
    ProgressDialog progressDialog;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView successlbl;
    ImageView backbtn;
    String gender = "Male";
    sharedpref admin_reauth;
    ProgressDialog PDreauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_prof);
        progressDialog = new ProgressDialog(this);
        editProfFullName = findViewById(R.id.editFullNameProf);
        editProfEmail = findViewById(R.id.editEmailProf);
        editProfPass = findViewById(R.id.editPassProf);
        radioGroup = findViewById(R.id.rg_gender_prof);
        editConfirmPass = findViewById(R.id.editConfirmPassProf);
        registerProfBtn = findViewById(R.id.registerProfBtn);
        idNumTv = findViewById(R.id.editEmployeeNum);
        successlbl = findViewById(R.id.successlbl_prof);
        backbtn = findViewById(R.id.closebtn_create_faccount);
        PDreauth = new ProgressDialog(this);
        admin_reauth = new sharedpref();
        backbtn.setOnClickListener(this);
        myAuth = FirebaseAuth.getInstance();
        facultyRef = FirebaseDatabase.getInstance().getReference().child("Faculty");
        /**if (getIntent().getExtras()!=null){
         String name = getIntent().getExtras().getString("name");
         successlbl.setVisibility(View.VISIBLE);
         successlbl.setText(name+" successfully registered!");
         }*/
        editProfFullName.addTextChangedListener(new TextWatcher() {
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
        registerProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerProf();
            }
        });
    }

    private void registerProf() {
        final String profFullName = editProfFullName.getText().toString().trim();
        final String profEmail = editProfEmail.getText().toString().trim();
        String profPass = editProfPass.getText().toString().trim();
        String confirmPass = editConfirmPass.getText().toString().trim();
        final String idNum = idNumTv.getText().toString();
        final String imageDefault = "https://firebasestorage.googleapis.com/v0/b/virtual-den2020.appspot.com/o/uploads%2Favatardef." +
                "png?alt=media&token=b719c984-64ed-4d4b-9c78-1431615fecb4";
        final String status = "";
        if (!TextUtils.isEmpty(profFullName)) {
            if (!TextUtils.isEmpty(profEmail)) {
                if (confirmPass.equals(profPass)) {
                    if (profPass.length() >= 6) {
                        myAuth.createUserWithEmailAndPassword(profEmail, profPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.show();
                                    progressDialog.setContentView(R.layout.progressdialogforsignup);
                                    progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                    final FirebaseUser firebaseUser = myAuth.getCurrentUser();
                                    HashMap<String, Object> user_data = new HashMap<>();
                                    user_data.put("fullname", profFullName);
                                    user_data.put("emailadd", profEmail);
                                    user_data.put("status", status);
                                    user_data.put("profileimg", imageDefault);
                                    user_data.put("bgimg", "none");
                                    user_data.put("sname", profFullName.toLowerCase());
                                    user_data.put("position", "professor");
                                    user_data.put("sched", "");
                                    user_data.put("gender", gender);
                                    user_data.put("idnum", idNum);
                                    user_data.put("schedDate", "");
                                    user_data.put("schedNote", "No schedule update for this professor");
                                    user_data.put("userType", "Faculty");
                                    user_data.put("about", "");
                                    user_data.put("userID", firebaseUser.getUid());
                                    facultyRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user_data)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        UserProfileChangeRequest dispname = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(profFullName)
                                                                .build();
                                                        firebaseUser.updateProfile(dispname);
                                                        HashMap<String, Object> userData = new HashMap<>();
                                                        userData.put("userType", "Faculty");
                                                        userData.put("isVerify", "no");
                                                        FirebaseDatabase.getInstance().getReference("userstbl").
                                                                child(myAuth.getCurrentUser().getUid())
                                                                .setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    editProfFullName.setText("");
                                                                    editProfPass.setText("");
                                                                    editConfirmPass.setText("");
                                                                    editProfEmail.setText("");
                                                                    progressDialog.dismiss();
                                                                    idNumTv.setText("");
                                                                    successlbl.setVisibility(View.VISIBLE);
                                                                    successlbl.setText(profFullName + " successfully registered");
                                                                    emailVerify();
                                                                }

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(this, "Password Too short", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Password not match!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
        }


    }

    public void emailVerify() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.signOut();
                        } else {
                            Toast.makeText(RegisterProf.this, "Error: " + task.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void gender_btn(View view) {
        int radioid = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);
        gender = radioButton.getText().toString();
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn) {
            try {
                admin_reauth.adminReauth(this,PDreauth);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {
        try {
            admin_reauth.adminReauth(this,PDreauth);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}