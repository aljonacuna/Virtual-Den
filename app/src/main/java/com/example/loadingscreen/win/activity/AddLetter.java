package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.checkIfThereAreNewData;
import com.example.loadingscreen.win.model.LetterDean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddLetter extends AppCompatActivity implements View.OnClickListener {
    private EditText letterinput;
    private Button postBtn;
    private ImageView backbtn;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_letter);
        letterinput = findViewById(R.id.letterEdit);
        postBtn = findViewById(R.id.addLetterBtn);
        backbtn = findViewById(R.id.backbtn_letter);
        reference = FirebaseDatabase.getInstance().getReference("LetterDean");
        postBtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == postBtn){
            submitLetter();
        }else if (view == backbtn){
            onback();
        }
    }

    public void submitLetter(){
        String letterTxt = letterinput.getText().toString().trim();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd ,yyyy hh:mm aaa");
        String timestamp = String.valueOf(9999999999999L + (-1 * new Date().getTime()));
        String dateSubmit = simpleDateFormat.format(Calendar.getInstance().getTime());
        if (TextUtils.isEmpty(letterTxt)){
            Toast.makeText(this, "Please write your letter", Toast.LENGTH_SHORT).show();
            return;
        }else{
            LetterDean letterDean = new LetterDean(letterTxt,firebaseUser.getUid(),dateSubmit,timestamp);
            reference.child(timestamp).setValue(letterDean);
            Toast.makeText(this, "Your letter for dean submitted successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,LetterforDean.class);
            startActivity(intent);
            finish();
        }
    }
    private void onback(){
        Intent intent = new Intent(this,LetterforDean.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        onback();
    }
}