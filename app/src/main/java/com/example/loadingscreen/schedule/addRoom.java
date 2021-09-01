package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.Utils.uriPathUtils;
import com.example.loadingscreen.model.roomList_model;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class addRoom extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout roomnameTv, roomplace;
    private AutoCompleteTextView roomplacedd;
    private TextInputEditText textInputEditText;
    private Button savebtn;
    private TextView successlbl;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<String> roomplaceList;
    private String place_room;
    DatabaseReference roomkeyref;
    private ArrayList<String> roomCheck;
    private ImageView cancelbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        roomnameTv = findViewById(R.id.room_nameTv);
        roomplace = findViewById(R.id.roomplace);
        roomplacedd = findViewById(R.id.roomplacedd);
        savebtn = findViewById(R.id.save_addedroombtn);
        textInputEditText = findViewById(R.id.room_txtEdit);
        progressBar = findViewById(R.id.addRoom_pb);
        successlbl = findViewById(R.id.succeslbl_addRoom);
        cancelbtn = findViewById(R.id.addroom_backbtn);
        databaseReference = FirebaseDatabase.getInstance().getReference("Room");
        spinner_itemRoom();
        savebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        roomplacedd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                successlbl.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                successlbl.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == savebtn) {
            add_room();
        }else if (view == cancelbtn){
            finish();
        }
    }

    public void spinner_itemRoom() {
        roomplaceList = new ArrayList<String>();
        roomplaceList.add(0, "Choose Floor in CICT");
        roomplaceList.add("First Floor");
        roomplaceList.add("Third Floor");
        roomplaceList.add("Fourth Floor");
        roomplaceList.add("Other Building");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownlayout, roomplaceList);
        roomplacedd.setAdapter(adapter);
    }


    public boolean isRoomname() {
        String roomname_txt = roomnameTv.getEditText().getText().toString();
        if (TextUtils.isEmpty(roomname_txt)) {
            roomnameTv.setError("Name of room is required");
            return false;
        } else {
            roomnameTv.setError(null);
            return true;
        }
    }

    public boolean isRoomplace() {
        String roomplace_txt = roomplace.getEditText().getText().toString();
        if (TextUtils.isEmpty(roomplace_txt) || roomplace_txt.equals("Choose Floor in CICT")) {
            roomplace.setError("Please select the place of this room");
            return false;
        } else {
            roomplace.setError(null);
            return true;
        }
    }

    public void add_room() {
        roomCheck = new ArrayList<>();
        if (!isRoomname() || !isRoomplace()) {
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            roomCheck = new ArrayList<>();
            final String roomname_txt = roomnameTv.getEditText().getText().toString();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Room");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String room = dataSnapshot.child("roomname").getValue().toString();
                            roomCheck.add(room);
                        }
                        if (roomCheck.contains(roomname_txt)) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(addRoom.this, "This room already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            textInputEditText.setText("");
                            progressBar.setVisibility(View.GONE);
                            successlbl.setText(roomname_txt+" room successfully added!");
                            successlbl.setVisibility(View.VISIBLE);
                            withoutImg(roomname_txt);
                        }
                    } else {
                        textInputEditText.setText("");
                        progressBar.setVisibility(View.GONE);
                        successlbl.setText(roomname_txt+" room successfully added!");
                        successlbl.setVisibility(View.VISIBLE);
                        withoutImg(roomname_txt);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void withoutImg(String roomname_txt) {
        roomkeyref = FirebaseDatabase.getInstance().getReference("Room").push();
        final String roomKey = roomkeyref.getKey();
        final String roomplace_txt = roomplace.getEditText().getText().toString();
        roomList_model room_list = new roomList_model(roomname_txt, roomplace_txt, roomKey);
        databaseReference.child(roomKey).setValue(room_list);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}