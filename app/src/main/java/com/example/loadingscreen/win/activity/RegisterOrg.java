package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegisterOrg extends AppCompatActivity implements View.OnClickListener {
    TextView successlbl;
    EditText editOrgName, editOrgEmail, editOrgPass, editConfirmPassOrg;
    DatabaseReference orgRef, usersRef;
    Button registerOrgBtn;
    FirebaseAuth myAuth;
    ProgressDialog progressDialog;
    ImageView backbtn;
    sharedpref admin_reauth;
    ProgressDialog PDreauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_org);
        editOrgName = findViewById(R.id.editNameOrg);
        editOrgEmail = findViewById(R.id.editEmailOrg);
        editOrgPass = findViewById(R.id.editPassOrg);
        successlbl = findViewById(R.id.successlbl);
        editConfirmPassOrg = findViewById(R.id.editConfirmPassOrg);
        registerOrgBtn = findViewById(R.id.registerOrgBtn);
        backbtn = findViewById(R.id.closebtn_create_orgacc);
        admin_reauth = new sharedpref();
        progressDialog = new ProgressDialog(this);
        PDreauth = new ProgressDialog(this);
        backbtn.setOnClickListener(this);
        myAuth = FirebaseAuth.getInstance();
        orgRef = FirebaseDatabase.getInstance().getReference().child("Organizations");
        usersRef = FirebaseDatabase.getInstance().getReference().child("userstbl");
        registerOrgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerProf();
            }
        });

        editOrgName.addTextChangedListener(new TextWatcher() {
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

    private void registerProf() {
        final String orgName = editOrgName.getText().toString().trim();
        final String orgEmail = editOrgEmail.getText().toString().trim();
        String orgPass = editOrgPass.getText().toString().trim();
        String confirmPass = editConfirmPassOrg.getText().toString().trim();
        final String imageDefault = "https://firebasestorage.googleapis.com/v0/b/virtual-den2020.appspot.com/o/uploads%2Favatardef." +
                "png?alt=media&token=b719c984-64ed-4d4b-9c78-1431615fecb4";
        if (!TextUtils.isEmpty(orgName)) {
            if (!TextUtils.isEmpty(orgEmail)) {
                if (confirmPass.equals(orgPass)) {
                    if (orgPass.length() >= 6) {
                        myAuth.createUserWithEmailAndPassword(orgEmail, orgPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.show();
                                    progressDialog.setContentView(R.layout.progressdialogforsignup);
                                    progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                    final FirebaseUser firebaseUser = myAuth.getCurrentUser();
                                    HashMap<String, Object> user_data = new HashMap<>();
                                    user_data.put("fullname", orgName);
                                    user_data.put("emailadd", orgEmail);
                                    user_data.put("about", "none");
                                    user_data.put("status", "");
                                    user_data.put("orgMission", "none");
                                    user_data.put("sname",orgName.toLowerCase());
                                    user_data.put("userType", "Organization");
                                    user_data.put("profileimg", imageDefault);
                                    user_data.put("userID", firebaseUser.getUid());
                                    orgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user_data).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        UserProfileChangeRequest dispname = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(orgName)
                                                                .build();
                                                        firebaseUser.updateProfile(dispname);
                                                        HashMap<String, Object> userData = new HashMap<>();
                                                        userData.put("userType", "Organizations");
                                                        userData.put("isVerify","no");
                                                        usersRef.child(FirebaseAuth.getInstance()
                                                                .getCurrentUser().getUid()).setValue(userData)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            new Handler().postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    progressDialog.dismiss();
                                                                                    editConfirmPassOrg.setText("");
                                                                                    editOrgEmail.setText("");
                                                                                    editOrgName.setText("");
                                                                                    editOrgPass.setText("");
                                                                                    successlbl.setText(orgName+" successfully registered!");
                                                                                    successlbl.setVisibility(View.VISIBLE);
                                                                                    emailVerify();
                                                                                }
                                                                            }, 1000);

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
    public void emailVerify(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                           firebaseAuth.signOut();
                        }else{
                            Toast.makeText(RegisterOrg.this, "Error: "+task.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn){
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