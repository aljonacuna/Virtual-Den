package com.example.loadingscreen.activity_gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.loadingscreen.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.loadingscreen.activity_gui.signin.emailAdd;
import static com.example.loadingscreen.activity_gui.signin.fileName;

public class userSetting extends AppCompatActivity implements View.OnClickListener {
    private Button logoutbtn;
    private FirebaseAuth firebaseAuth;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        logoutbtn = findViewById(R.id.logout_btn);
        logoutbtn.setOnClickListener(this);
        preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        if (view == logoutbtn){
            firebaseAuth = FirebaseAuth.getInstance();
            preferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(emailAdd);
            editor.remove(signin.signup);
            editor.commit();
            firebaseAuth.signOut();
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
            finish();
        }
    }
}