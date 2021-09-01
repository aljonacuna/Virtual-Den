package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.adapter.roomschedToupdateAdapter;
import com.example.loadingscreen.model.roomList_model;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class updateroomsched extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout room_til;
    private AutoCompleteTextView act_room;
    private Button updateBtn;
    private ImageView backbtn;
    String roomname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateroomsched);
        act_room = findViewById(R.id.ACT_room);
        room_til = findViewById(R.id.room_TIL);
        updateBtn = findViewById(R.id.update_roomschedbtn);
        backbtn = findViewById(R.id.updtschedroombtn);
        backbtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        Roomlist();
    }
    @Override
    public void onClick(View view) {
        if (view == updateBtn){
            toUpdateRoomsched();
        }else if (view == backbtn){
            finish();
        }
    }
    public void Roomlist() {
        final ArrayList<String>roomList  = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String room_name = dataSnapshot.child("roomname").getValue(String.class);
                    roomList.add(room_name);
                }
                ArrayAdapter<String > adapter = new ArrayAdapter<>(updateroomsched.this,
                        R.layout.dropdownlayout,roomList);
                act_room.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void toUpdateRoomsched(){
        final String roomnameTxt = room_til.getEditText().getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Room");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String roomnameData = dataSnapshot.child("roomname").getValue(String.class);
                    if (roomnameData.equals(roomnameTxt)){
                        final String key = dataSnapshot.child("roomkey").getValue(String.class);
                        final arrfordd getdialog = new arrfordd();
                        String title = "Edit schedule";
                        String message = "Are you sure that this room of ";
                        String message1 = " need an update?";
                        getdialog.dialog_conf(updateroomsched.this, title,
                                message, roomnameTxt, message1,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(updateroomsched.this,add_room_sched.class);
                                        intent.putExtra("roomKey",key);
                                        intent.putExtra("roomname",roomnameTxt);
                                        startActivity(intent);
                                        getdialog.dialog.dismiss();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}