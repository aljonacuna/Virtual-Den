package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.clearUtils;
import com.example.loadingscreen.fullscreen_img.lostandfound_IMG;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class userpostdetail extends AppCompatActivity implements View.OnClickListener {
    private ImageView imgpost;
    private TextView desc,itemname,status,name;
    private Button optionbtn;
    String postkey;
    String post_status;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpostdetail);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        imgpost = findViewById(R.id.imgpost);
        optionbtn = findViewById(R.id.option_btndetailuser);
        desc = findViewById(R.id.desc_userTV);
        itemname = findViewById(R.id.itemname_userTv);
        status = findViewById(R.id.status_userTv);
        name = findViewById(R.id.name_author);
        optionbtn.setOnClickListener(this);
        if (getIntent().getExtras()!=null && getIntent().hasExtra("caption") && getIntent().hasExtra("item")
        && getIntent().hasExtra("idprofile")){
            final String imgurl = getIntent().getExtras().getString("imgurl");
            if (imgurl.equals("none")) {
                imgpost.setImageResource(R.drawable.noimage);
            }
            else {
                Picasso.with(this).load(imgurl).into(imgpost);
                imgpost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(userpostdetail.this, lostandfound_IMG.class);
                        intent.putExtra("imgpost",imgurl);
                        startActivity(intent);
                    }
                });
            }
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            id = getIntent().getExtras().getString("idprofile");
            if (id.equals(firebaseUser.getUid())){
                optionbtn.setVisibility(View.VISIBLE);
            }else {
                optionbtn.setVisibility(View.GONE);
            }
            String desctxt = getIntent().getExtras().getString("caption");
            desc.setText(desctxt);
            String itemTxt = getIntent().getExtras().getString("item");
            itemname.setText(itemTxt);
            post_status = getIntent().getExtras().getString("status");
            status.setText(post_status);
            String authornameTxt = getIntent().getExtras().getString("name");
            name.setText(authornameTxt);
            postkey = getIntent().getExtras().getString("postkey");
        }else{
            if (getIntent().getExtras() != null && getIntent().hasExtra("postkey")) {
                postkey = getIntent().getExtras().getString("postkey");
                post_status = getIntent().getExtras().getString("status");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(post_status).child(postkey);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String imagePost = snapshot.child("photo").getValue().toString();
                        String authorName = snapshot.child("name").getValue().toString();
                        String desctxt = snapshot.child("desc").getValue().toString();
                        String itemTxt = snapshot.child("item").getValue().toString();
                        getAuthor();
                        if (imagePost.equals("none")) {
                            imgpost.setImageResource(R.drawable.noimage);
                        } else {
                            Picasso.with(userpostdetail.this).load(imagePost).into(imgpost);
                        }
                        name.setText(authorName);
                        desc.setText(desctxt);
                        itemname.setText(itemTxt);
                        status.setText(post_status);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
    private void getAuthor(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post")
                .child(post_status).child(postkey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String authorID = snapshot.child("userID").getValue(String.class);
                id = getIntent().getExtras().getString("id");
                if (id.equals(authorID)){
                    optionbtn.setVisibility(View.VISIBLE);
                }else {
                    optionbtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view == optionbtn){
            clearUtils.clear(post_status,postkey,userpostdetail.this,"");
        }
    }
}