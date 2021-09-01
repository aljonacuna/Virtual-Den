package com.example.loadingscreen.fullscreen_img;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.squareup.picasso.Picasso;

public class lostandfound_IMG extends AppCompatActivity {
    private ImageView postimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostandfound__i_m_g);
        postimg = findViewById(R.id.post_IMG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(0);
            window.setStatusBarColor(Color.BLACK);
        }
        String imgurl = getIntent().getExtras().getString("imgpost");
        Picasso.with(this).load(imgurl).into(postimg);
        try {
            int h = postimg.getDrawable().getIntrinsicHeight();
            if (h <= 500) {
                postimg.getLayoutParams().height = 600;
                postimg.requestLayout();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}