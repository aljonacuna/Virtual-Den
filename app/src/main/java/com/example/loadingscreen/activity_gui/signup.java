package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout emailText, passText, studno, confirmPass;
    private Button button;
    private TextView textView,loginbtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String hint = "";
    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        button = (Button) findViewById(R.id.nextbtn);
        emailText = findViewById(R.id.email);
        passText = findViewById(R.id.pass);
        loginbtn = findViewById(R.id.login_btn);
        confirmPass = findViewById(R.id.confirmpass);
        loginbtn.setPaintFlags(loginbtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        button.setOnClickListener(this);
        loginbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == button) {
            signup1();
        } else if (view == loginbtn) {
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
        }
    }


    private boolean errorTrappingEmail() {
        final String email_add = emailText.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(email_add)) {
            emailText.setError("Email Address is Required");
            emailText.requestFocus();
            return false;
        } else {
            emailText.setError(null);
            return true;
        }
    }
    public boolean isEmailValid() {
        String email = emailText.getEditText().getText().toString().trim();
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean errorTrappingPass() {
        String password = passText.getEditText().getText().toString().trim();
        String confirmpass = confirmPass.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            passText.setError("Password is Empty");
            passText.requestFocus();
            return false;
        } else if (!password.equals(confirmpass)) {
            confirmPass.setError("Passwords - do not matched");
            return false;
        } else {
            passText.setError(null);
            confirmPass.setError(null);
            return true;
        }
    }


    public void signup1() {
        final String email = emailText.getEditText().getText().toString().toLowerCase().trim();
        final String password = passText.getEditText().getText().toString().trim();

        if (!errorTrappingEmail() || !errorTrappingPass()) {
            return;
        } else {
            final String comStr = email.substring(email.length()-3);
            if (isEmailValid() && comStr.equals("com")) {
                firebaseAuth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if (isNewUser) {
                                    if (password.length() < 6) {
                                        Toast.makeText(signup.this, "Invalid password(Min 6 chars)!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(signup.this, signup2.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("pass", password);
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(signup.this, "Email is already taken!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(signup.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            }


        }
    }


}