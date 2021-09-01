package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.fragment.firstfloor;
import com.example.loadingscreen.fragment.fourthfloor;
import com.example.loadingscreen.fragment.otherbuilding;
import com.example.loadingscreen.fragment.secondfloor;
import com.example.loadingscreen.fragment.thirdfloor;
import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class schedule_roomassign extends AppCompatActivity implements View.OnClickListener {
    private firstfloor first_floor;
    private secondfloor second_floor;
    private thirdfloor third_floor;
    private fourthfloor fourth_floor;
    private otherbuilding other_building;
    private Button addroomBtn;
    private GridLayout gridLayout;
    private MaterialCardView mcv1, mcv2, mcv3, mcv4, mcv5, mcv6;
    private ImageView show_hide,backbtn;
    private TextView map_lbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_roomassign);

        TabLayout tabLayout = findViewById(R.id.tabLayoutSchedule);
        ViewPager viewPager = findViewById(R.id.viewpagerSchedule);
        viewPager.setOffscreenPageLimit(3);
        gridLayout = findViewById(R.id.mapGL);
        backbtn = findViewById(R.id.rooms_backbtn);
        mcv1 = findViewById(R.id.mcv1);
        mcv2 = findViewById(R.id.mcv2);
        mcv3 = findViewById(R.id.mcv3);
        mcv4 = findViewById(R.id.mcv4);
        mcv5 = findViewById(R.id.mcv5);
        mcv6 = findViewById(R.id.mcv6);
        map_lbl = findViewById(R.id.map_lbl);
        show_hide = findViewById(R.id.hide_map);


        first_floor = new firstfloor();
        third_floor = new thirdfloor();
        fourth_floor = new fourthfloor();
        other_building = new otherbuilding();
        mcv1.setOnClickListener(this);
        mcv2.setOnClickListener(this);
        mcv3.setOnClickListener(this);
        mcv4.setOnClickListener(this);
        mcv5.setOnClickListener(this);
        mcv6.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        show_hide.setOnClickListener(this);
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapterSchedule viewPagerAdapterSchedule = new ViewPagerAdapterSchedule(getSupportFragmentManager(), 0);
        viewPagerAdapterSchedule.addFragment(first_floor, "First Floor");
        viewPagerAdapterSchedule.addFragment(third_floor, "Third Floor");
        viewPagerAdapterSchedule.addFragment(fourth_floor, "Fourth Floor");
        viewPagerAdapterSchedule.addFragment(other_building, "Other Buildings");
        viewPager.setAdapter(viewPagerAdapterSchedule);
    }

    @Override
    public void onClick(View view) {
        if (view == mcv1) {
            Intent intent = new Intent(this, schedulefull_img.class);
            intent.putExtra("tag", "map1");
            startActivity(intent);
        } else if (view == mcv2) {
            Intent intent = new Intent(this, schedulefull_img.class);
            intent.putExtra("tag", "map2");
            startActivity(intent);
        } else if (view == mcv3) {
            Intent intent = new Intent(this, schedulefull_img.class);
            intent.putExtra("tag", "map3");
            startActivity(intent);
        } else if (view == mcv4) {
            Intent intent = new Intent(this, schedulefull_img.class);
            intent.putExtra("tag", "map4");
            startActivity(intent);
        } else if (view == mcv5) {
            Intent intent = new Intent(this, schedulefull_img.class);
            intent.putExtra("tag", "map5");
            startActivity(intent);
        } else if (view == mcv6) {
            Intent intent = new Intent(this, schedulefull_img.class);
            intent.putExtra("tag", "map6");
            startActivity(intent);
        } else if (view == show_hide) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.hide_map, popupMenu.getMenu());
            String lblmap = map_lbl.getText().toString();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.hidemap:
                            gridLayout.setVisibility(View.GONE);
                            map_lbl.setText("");
                            return true;
                        case R.id.showmap:
                            gridLayout.setVisibility(View.VISIBLE);
                            map_lbl.setText("Room map");
                            return true;
                        default:
                            return false;
                    }
                }
            });
            Menu menu = popupMenu.getMenu();
            if (lblmap.equals("")){
                menu.findItem(R.id.showmap).setVisible(true);
                menu.findItem(R.id.hidemap).setVisible(false);
            }
            else if (lblmap.equals("Room map")){
                menu.findItem(R.id.showmap).setVisible(false);
                menu.findItem(R.id.hidemap).setVisible(true);
            }
            popupMenu.show();
        }
        else if (view == backbtn){
            super.onBackPressed();
            finish();
        }
    }

    class ViewPagerAdapterSchedule extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> menus;

        public ViewPagerAdapterSchedule(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.fragments = new ArrayList<>();
            this.menus = new ArrayList<>();
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

        public void addFragment(Fragment fragment, String menu) {
            fragments.add(fragment);
            menus.add(menu);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return menus.get(position);
        }
    }

}