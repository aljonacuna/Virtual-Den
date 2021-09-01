package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.loadingscreen.R;

import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.example.loadingscreen.model.add_roomsched_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class roomsched extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private TextView textView, datepostedlbl, note;
    private ImageView room_sched, backbtn;
    private List<add_roomsched_model> roomschedList = new ArrayList<>();
    private DatabaseReference reference;
    private ProgressBar progressBar;
    private String roomkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomsched);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(roomsched.this);
        textView = findViewById(R.id.notyetlbl_roomsched);
        note = findViewById(R.id.noteRoomSched);
        room_sched = findViewById(R.id.room_schedimg);
        backbtn = findViewById(R.id.roomschedule_backbtn);
        datepostedlbl = findViewById(R.id.dateposted_roomsched);
        backbtn.setOnClickListener(this);
        if (getIntent().getExtras() != null) {
            roomkey = getIntent().getExtras().getString("roomkey");
            getUsers();
        }

    }

    public void getUsers() {
        reference = FirebaseDatabase.getInstance().getReference("Schedule").child("Room").child(roomkey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren() && snapshot.exists()) {
                    add_roomsched_model addroomsched = snapshot.getValue(add_roomsched_model.class);
                    final String imgurl = addroomsched.getImage();
                    Picasso.with(roomsched.this).load(imgurl).into(room_sched);
                    String[] str = addroomsched.getDate().split("/");
                    String noteText = addroomsched.getNote();
                    String label = str[0];
                    String date = str[1];
                    if (label.equals("dateposted")) {
                        datepostedlbl.setText("Date Posted: ");
                        note.setText(noteText);
                        textView.setText(date);
                        textView.setTextColor(ContextCompat.getColor(roomsched.this, R.color.black));
                    } else if (label.equals("updated")) {
                        datepostedlbl.setText("Last Updated: ");
                        note.setText(noteText);
                        textView.setText(date);
                        textView.setTextColor(ContextCompat.getColor(roomsched.this, R.color.black));
                    }
                    room_sched.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(roomsched.this, schedulefull_img.class);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(roomsched.this, room_sched,
                                        ViewCompat.getTransitionName(room_sched));
                                intent.putExtra("image", imgurl);
                                startActivity(intent, options.toBundle());
                            }

                        }
                    });

                } else {
                    room_sched.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    datepostedlbl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == backbtn) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}