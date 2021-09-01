package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateEmail extends AppCompatActivity implements View.OnClickListener {
    private EditText currentEmail,newEmail,pass;
    private ImageView closebtn;
    private Button savebtn;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences preferences;
    String userType;
    int errorCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        currentEmail = findViewById(R.id.currentEmail);
        newEmail = findViewById(R.id.newEmail);
        pass = findViewById(R.id.pass_changeemail);
        savebtn = findViewById(R.id.saveEmailBtn);
        progressBar = findViewById(R.id.updateEmailPb);
        closebtn = findViewById(R.id.closebtn_changeemail);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userType = sharedpref.spUsertype(this);
        databaseReference = FirebaseDatabase.getInstance().getReference(userType);
        savebtn.setOnClickListener(this);
        closebtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == savebtn){
            String current_email = currentEmail.getText().toString();
            String new_email = newEmail.getText().toString();
            String pword = pass.getText().toString();
            saveEmail(current_email.toLowerCase(),new_email.toLowerCase(),pword);

        }else if (view == closebtn){

        }
    }
    public boolean isEmailValid(String current_email, String new_email, String pword){
        if (TextUtils.isEmpty(current_email)){
            Toast.makeText(this, "Please enter your current email!", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!firebaseUser.getEmail().equals(current_email)){
            Toast.makeText(this, "Incorrect email address!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }
    public boolean isPassEmpty(String pword){
        if (TextUtils.isEmpty(pword)){
            Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    private void saveEmail(String current_email, final String new_email, String pword) {

        if (!isEmailValid(current_email,new_email,pword) || !isPassEmpty(pword)){
            return;
        }else {
            if (errorCounter < 4) {
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential authCredential = EmailAuthProvider
                        .getCredential(current_email, pword);
                firebaseUser.reauthenticate(authCredential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updateEmail(new_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("emailadd", new_email);
                                                databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap);
                                                Toast.makeText(UpdateEmail.this, "Email Successfully Updated",
                                                        Toast.LENGTH_SHORT).show();
                                                sharedpref.logout(UpdateEmail.this);
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                Toast.makeText(UpdateEmail.this, "Invalid email format!",
                                                        Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                } else {
                                    errorCounter = errorCounter + 1;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(UpdateEmail.this, "Incorrect password!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        if (errorCounter>3) {
                            Toast.makeText(UpdateEmail.this, "Error Message :" + e.getMessage()
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }
    }

}