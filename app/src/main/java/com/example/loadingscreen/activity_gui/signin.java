package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.visitlogin.GuestLostAndFound;
import com.example.loadingscreen.win.activity.AdminPanel;
import com.example.loadingscreen.win.activity.ForgotPassword;
import com.example.loadingscreen.win.activity.Mainmenu;
import com.example.loadingscreen.win.activity.OrgUserMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import static com.example.loadingscreen.activity_gui.signup2.idAttemptFile;
import static com.example.loadingscreen.activity_gui.signup2.id_Attempt;


public class signin extends AppCompatActivity implements View.OnClickListener {
    private Button buttonLogin, registerbtn;
    private TextView forgotPass, signinguestbtn;
    private TextInputLayout textPass, textUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    SharedPreferences preferences;
    public static final String fileName = "Login";
    public static final String emailAdd = "EmailAddress";
    public static final String password = "Password";
    public static final String signup = "SignupEmailAddress";
    public static final String fileName_usertype = "userTypeFile";
    public static final String userTypeSP = "type_of_user";
    SharedPreferences prefUsertype;
    SharedPreferences idAttemptpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        progressDialog = new ProgressDialog(this);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        registerbtn = findViewById(R.id.register);
        textPass = findViewById(R.id.textPass);
        textUser = findViewById(R.id.textUser);
        signinguestbtn = findViewById(R.id.signinguest);
        signinguestbtn.setPaintFlags(signinguestbtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPass = findViewById(R.id.forgotPass);
        firebaseAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        idAttemptpref = getSharedPreferences(idAttemptFile, Context.MODE_PRIVATE);
        prefUsertype = getSharedPreferences(fileName_usertype, Context.MODE_PRIVATE);
        if (preferences.contains(emailAdd) || preferences.contains(signup)) {

        }
        buttonLogin.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        signinguestbtn.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            login();
        } else if (view == registerbtn) {
            long currenttime = System.currentTimeMillis();
            long timer = idAttemptpref.getLong(id_Attempt, 0);
            if (currenttime > timer) {
                SharedPreferences.Editor editor = idAttemptpref.edit();
                editor.remove(id_Attempt);
                editor.commit();
                Intent intent = new Intent(this, signup.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Wait for 30 mins, you have penalty for attempting to use wrong student number!", Toast.LENGTH_LONG).show();
            }
        } else if (view == signinguestbtn) {
            signinAnonymously();
        } else if (view == forgotPass) {
            Intent intent = new Intent(this, ForgotPassword.class);
            startActivity(intent);
        }
    }

    public Boolean username() {
        String user = textUser.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            textUser.setError("Email is required");
            textUser.requestFocus();
            return false;
        } else {
            textUser.setError(null);
            return true;
        }
    }

    public Boolean password() {
        String pass = textPass.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            textPass.setError("Password is required");
            textPass.requestFocus();
            return false;
        } else {
            textPass.setError(null);
            return true;
        }
    }

    public void login() {
        final String email = textUser.getEditText().getText().toString().toLowerCase().trim();
        final String pass = textPass.getEditText().getText().toString().trim();
        if (!username() || !password()) {
            return;
        } else {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialogforsignin);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getuserType(email,pass);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                },3000);


                            } else {
                                Toast.makeText(signin.this, "Email or Password Incorrect!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }


                        }
                    });
        }

    }

    private void getuserType(final String email, final String pass) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUser = firebaseUser.getUid();
        final DatabaseReference getusertypeRef = FirebaseDatabase.getInstance().getReference("userstbl")
                .child(currentUser);
        getusertypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String usertype = snapshot.child("userType").getValue().toString();
                    SharedPreferences.Editor editor = prefUsertype.edit();
                    editor.putString(userTypeSP, usertype);
                    editor.commit();
                    if (snapshot.hasChild("isVerify")) {
                        String isVerify = snapshot.child("isVerify").getValue().toString();
                        if (firebaseUser.isEmailVerified() || isVerify.equals("yes")) {
                            if (isVerify.equals("no")) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isVerify", "yes");
                                getusertypeRef.updateChildren(hashMap);
                            }
                            SharedPreferences.Editor editorSignin = preferences.edit();
                            editorSignin.putString(emailAdd, "email");
                            editorSignin.putString(password, "pass");
                            editorSignin.commit();
                            if (usertype.equals("Student") || usertype.equals("Faculty")) {
                                Intent intent = new Intent(signin.this, Mainmenu.class);
                                startActivity(intent);
                            } else if (usertype.equals("Organizations")) {
                                Intent Orgintent = new Intent(signin.this, OrgUserMenu.class);
                                startActivity(Orgintent);
                            }
                        } else {
                            Toast.makeText(signin.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(signin.this, ResendVerification.class);
                            startActivity(intent);

                        }
                    } else {

                        /**SharedPreferences.Editor editorSignin = preferences.edit();
                        editorSignin.putString(emailAdd, email);
                        editorSignin.putString(password, pass);
                        editorSignin.commit();
                        Intent Adminintent = new Intent(signin.this, AdminPanel.class);
                        startActivity(Adminintent);**/
                        try {
                            SharedPreferences.Editor editoradmin = sharedpref.saveAdminEncryptedData(signin.this).edit();
                            editoradmin.putString(emailAdd,email);
                            editoradmin.putString(password,pass);
                            editoradmin.commit();
                            Intent Adminintent = new Intent(signin.this, AdminPanel.class);
                            startActivity(Adminintent);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signinAnonymously() {
        Intent intent = new Intent(this, GuestLostAndFound.class);
        startActivity(intent);
    }
}
