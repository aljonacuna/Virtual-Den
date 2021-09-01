package com.example.loadingscreen.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.createcontent;
import com.example.loadingscreen.fragment.class_sched;

public class add_front_sched extends AppCompatActivity implements View.OnClickListener {
    private Button add_btn, update_btn;
    private ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_front_sched);
        add_btn = findViewById(R.id.class_button_add);
        update_btn = findViewById(R.id.faculty_button_add);
        backbtn = findViewById(R.id.toUpdatesched_backbtn);
        add_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == add_btn) {
            Intent intent = new Intent(this, adding_class_sched.class);
            startActivity(intent);
            finish();
        } else if (view == update_btn) {
            Intent intent = new Intent(this, toEditclass.class);
            startActivity(intent);
        }
    }
}