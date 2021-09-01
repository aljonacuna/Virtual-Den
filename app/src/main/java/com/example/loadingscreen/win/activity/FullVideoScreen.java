package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.loadingscreen.R;
import com.squareup.picasso.Picasso;


public class FullVideoScreen extends AppCompatActivity {

    VideoView fullVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        fullVideo=findViewById(R.id.fullVideo);
        Intent video=getIntent();
        String videouri = video.getStringExtra("video");

        Uri videouri1=Uri.parse(videouri);
        fullVideo.setVideoURI(videouri1);
        fullVideo.start();
    }
}