package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.loadingscreen.R;

public class LoginActivity extends AppCompatActivity {

    TextView forgotPw,progressText;
    Button signInBtn,signUpBtn,visitBtn;
    EditText loginEditEmail,loginEditPassword;
    DatabaseReference userTypeRef;
    FirebaseAuth myAuth;
    ProgressBar loginProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEditEmail=findViewById(R.id.loginEditEmail);
        loginEditPassword=findViewById(R.id.loginEditPassword);
        loginProgressBar=findViewById(R.id.progressBarLogin);
        progressText=findViewById(R.id.progressText);
        forgotPw=findViewById(R.id.forgotPw);
        visitBtn=findViewById(R.id.visitAppBtn);
        signInBtn=findViewById(R.id.signInBtn);
        signUpBtn=findViewById(R.id.signUpBtn);
        forgotPw.getPaint().setUnderlineText(true);

    }
    //Login ACCOUNT METHOD
    private void loginAccount() {
        myAuth = FirebaseAuth.getInstance();

        userTypeRef = FirebaseDatabase.getInstance().getReference().child("Users");
        String userEmail = loginEditEmail.getText().toString().trim();
        String userPassword = loginEditPassword.getText().toString().trim();
        loginProgressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        if (myAuth != null){
            if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)) {
                myAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginProgressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            String userId = myAuth.getCurrentUser().getUid();
                            userTypeRef.child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        String userType = snapshot.child("type").getValue().toString();
                                        if (userType.equals("Student")) {
                                            Intent intent = new Intent(LoginActivity.this,Mainmenu.class);
                                            startActivity(intent);
                                        } else if (userType.equals("Organization")) {
                                            Intent intent = new Intent(LoginActivity.this, OrgUserMenu.class);
                                            startActivity(intent);
                                        }
                                        else if (userType.equals("Admin")) {
                                            Intent intent = new Intent(LoginActivity.this, AdminPanel.class);
                                            startActivity(intent);
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "User logout", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Email or Password Incorrect!", Toast.LENGTH_LONG).show();
                            loginProgressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                Toast.makeText(LoginActivity.this, "Complete sign in fields!", Toast.LENGTH_LONG).show();
                loginProgressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
            }
        }
    }
    //END LOGIN METHOD

}