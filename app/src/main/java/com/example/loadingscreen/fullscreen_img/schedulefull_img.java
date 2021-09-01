package com.example.loadingscreen.fullscreen_img;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.loadingscreen.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class schedulefull_img extends AppCompatActivity {
    private PhotoView img;
    String room, room_sched, class_sched, faculty_sched, room_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulefull_img);
        img = findViewById(R.id.imgFullscreen);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("img")) {
            room = getIntent().getExtras().getString("img");
            Picasso.with(this).load(room).into(img);
        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("imgschedclass")) {
            class_sched = getIntent().getExtras().getString("imgschedclass");
            Picasso.with(this).load(class_sched).into(img);
            try {
                int h = img.getDrawable().getIntrinsicHeight();
                if (h <= 500) {
                    img.getLayoutParams().height = 600;
                    img.requestLayout();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("imgfacultysched")) {
            faculty_sched = getIntent().getExtras().getString("imgfacultysched");
            Picasso.with(this).load(faculty_sched).into(img);
            try {
                int h = img.getDrawable().getIntrinsicHeight();
                if (h <= 500) {
                    img.getLayoutParams().height = 600;
                    img.requestLayout();
                }
            } catch (Exception e) {

            }
        }

        if (getIntent().getExtras() != null && getIntent().hasExtra("image")) {
            room_sched = getIntent().getExtras().getString("image");
            Picasso.with(this).load(room_sched).into(img);
            try {
                int h = img.getDrawable().getIntrinsicHeight();
                if (h <= 500) {
                    img.getLayoutParams().height = 600;
                    img.requestLayout();
                }
            } catch (Exception e) {

            }
        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("tag")) {
            room_map = getIntent().getExtras().getString("tag");
            if (room_map.equals("map1")) {
                img.setImageResource(R.drawable.img1);
            } else if (room_map.equals("map2")) {
                img.setImageResource(R.drawable.img3);
            } else if (room_map.equals("map3")) {
                img.setImageResource(R.drawable.img2);
            } else if (room_map.equals("map4")) {
                img.setImageResource(R.drawable.img4);
            } else if (room_map.equals("map5")) {
                img.setImageResource(R.drawable.img5);
            } else if (room_map.equals("map6")) {
                img.setImageResource(R.drawable.img6);
            }
        }
    }
}