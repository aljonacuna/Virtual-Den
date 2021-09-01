package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.fragment.blis_class_sched;
import com.example.loadingscreen.fragment.class_sched;
import com.example.loadingscreen.fragment.faculty_sched;
import com.example.loadingscreen.fragment.search_class_faculty;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class class_faculty_sched extends AppCompatActivity implements View.OnClickListener {
    private Button add_schedbtn, viewclass, viewfaculty;
    private ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_sched);
        viewclass = findViewById(R.id.view_classschedbtn);
        viewfaculty = findViewById(R.id.view_facultyschedbtn);
        backbtn = findViewById(R.id.class_sched_backbtn);
        backbtn.setOnClickListener(this);
        viewfaculty.setOnClickListener(this);
        viewclass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == viewclass) {
            Intent intent = new Intent(this, class_schedule.class);
            startActivity(intent);
        } else if (view == viewfaculty) {
            Intent intent = new Intent(this, faculty_schedule.class);
            startActivity(intent);
        }else if (view == backbtn){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}