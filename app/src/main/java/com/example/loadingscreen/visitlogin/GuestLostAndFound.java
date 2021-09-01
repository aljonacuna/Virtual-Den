package com.example.loadingscreen.visitlogin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.lostandfound;
import com.example.loadingscreen.fragment.claim;
import com.example.loadingscreen.fragment.found;
import com.example.loadingscreen.fragment.lost;
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

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class GuestLostAndFound extends AppCompatActivity implements View.OnClickListener {
    private GuestViewFound foundFragment;
    private GuestViewLost lostFragment;
    private GuestViewClear claimFragment;
    private ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_lost_and_found);
        TabLayout tabLayout = findViewById(R.id.tablayoutGuest);
        ViewPager viewPager = findViewById(R.id.viewpagerGuest);
        backbtn = findViewById(R.id.backlostfoundbtn);
        backbtn.setOnClickListener(this);
        foundFragment = new GuestViewFound();
        lostFragment = new GuestViewLost();
        claimFragment = new GuestViewClear();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        GuestLostAndFound.ViewPagerAdapter viewPagerAdapter = new GuestLostAndFound.ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addfragment(foundFragment,"Found");
        viewPagerAdapter.addfragment(lostFragment,"Lost");
        viewPagerAdapter.addfragment(claimFragment,"Cleared");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn){
            finish();
        }
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment>fragments;
        private ArrayList<String>titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
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

        public void addfragment(Fragment fragment ,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}