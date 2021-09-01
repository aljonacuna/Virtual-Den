package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private ImageView closebtn;
    private EditText currentPass,newPass,confPass;
    private Button savebtn;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    int errorCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        closebtn = findViewById(R.id.change_p_closebtn);
        savebtn = findViewById(R.id.savePasswordBtn);
        currentPass = findViewById(R.id.currentPassword);
        newPass = findViewById(R.id.newPassword);
        confPass = findViewById(R.id.confirmNewPassword);
        progressBar = findViewById(R.id.updatePassPb);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        closebtn.setOnClickListener(this);
        savebtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == closebtn){

        }else if (view == savebtn){
            String current_pass = currentPass.getText().toString();
            String conf_pass = confPass.getText().toString();
            String new_pass = newPass.getText().toString();
            savePass(current_pass,conf_pass,new_pass);
        }
    }
    public boolean isPasswordValid(String current_pass, String conf_pass, String new_pass){

        if (TextUtils.isEmpty(current_pass)){
            Toast.makeText(this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!new_pass.equals(conf_pass)){
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show();
            return false;
        }else if (new_pass.length()<6){
            Toast.makeText(this, "Password length required 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    public void savePass(String current_pass, String conf_pass, final String new_pass){
        if (!isPasswordValid(current_pass,conf_pass,new_pass )){
            return;
        }else {
            if (errorCounter < 3) {
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential authCredential = EmailAuthProvider
                        .getCredential(firebaseUser.getEmail(), current_pass);
                firebaseUser.reauthenticate(authCredential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(new_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ChangePassword.this, "Password Successfully Updated",
                                                        Toast.LENGTH_SHORT).show();
                                                sharedpref.logout(ChangePassword.this);
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                //error
                                            }
                                        }
                                    });
                                } else {
                                    errorCounter = errorCounter + 1;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ChangePassword.this, "Please enter your correct password!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (errorCounter>3) {
                            Toast.makeText(ChangePassword.this, "Error Message: " + e.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}