package com.example.loadingscreen.fullscreen_img;

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

public class chat_userimg_fullscreen extends AppCompatActivity {
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userimg_fullscreen);
        image = findViewById(R.id.user_imgfull);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("userimg")) {
            String imgurl = getIntent().getExtras().getString("userimg");
            Picasso.with(this).load(imgurl).into(image);
            try {
                int h = image.getDrawable().getIntrinsicHeight();
                if (h <= 500) {
                    image.getLayoutParams().height = 600;
                    image.requestLayout();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("chatimg")) {
            String imgurl = getIntent().getExtras().getString("chatimg");
            Picasso.with(this).load(imgurl).into(image);
            try {
                int h = image.getDrawable().getIntrinsicHeight();
                if (h <= 500) {
                    image.getLayoutParams().height = 600;
                    image.requestLayout();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}