package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.signin;

public class SuccessSignup extends AppCompatActivity {

    TextView emailText;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_signup);
        emailText = findViewById(R.id.yourEmail);
        progressDialog = new ProgressDialog(this);
        if (getIntent().getExtras() != null) {
            String email = getIntent().getExtras().getString("Email");
            emailText.setText("Email: "+email);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SuccessSignup.this, signin.class);
                startActivity(intent);
            }
        },5000);
    }
}