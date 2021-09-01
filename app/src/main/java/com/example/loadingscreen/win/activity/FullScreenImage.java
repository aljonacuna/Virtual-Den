package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.loadingscreen.R;
import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {

    ImageView fullImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullImage=findViewById(R.id.fullImage);
        Intent image=getIntent();
        String imguri = image.getStringExtra("image");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.fullImageToolBar);
        mToolbar.setNavigationIcon(R.drawable.arrow_back);
        //back button
        Uri imguri1=Uri.parse(imguri);
        Glide.with(this).load(imguri1).into(fullImage);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}