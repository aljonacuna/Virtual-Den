package com.example.loadingscreen.activity_gui;

import android.content.Intent;
import android.os.Bundle;

import com.example.loadingscreen.Utils.checkIfThereAreNewData;
import com.example.loadingscreen.fragment.claim;
import com.example.loadingscreen.fragment.found;
import com.example.loadingscreen.fragment.lost;
import com.example.loadingscreen.win.activity.Mainmenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;

import com.example.loadingscreen.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class lostandfound extends AppCompatActivity implements View.OnClickListener {
    private lost lostFragment;
    private found foundFragment;
    private claim claimFragment;
    private ImageView backbtn;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostandfound);
        floatingActionButton = findViewById(R.id.fab);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        backbtn = findViewById(R.id.backlostfoundbtn);
        lostFragment = new lost();
        foundFragment = new found();
        claimFragment = new claim();
        try {
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
            viewPagerAdapter.addfragment(foundFragment,"Found");
            viewPagerAdapter.addfragment(lostFragment,"Lost");
            viewPagerAdapter.addfragment(claimFragment,"Cleared");
            viewPager.setAdapter(viewPagerAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        backbtn.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == floatingActionButton){
            Intent intent = new Intent(this,createcontent.class);
            startActivity(intent);
            finish();
        }else if (view == backbtn){
            Intent intent = new Intent(this, Mainmenu.class);
            startActivity(intent);
            finish();

        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> options;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.fragments = new ArrayList<>();
            this.options = new ArrayList<>();
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addfragment(Fragment fragment, String option) {
            fragments.add(fragment);
            options.add(option);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return options.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}