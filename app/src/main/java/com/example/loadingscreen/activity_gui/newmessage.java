package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.claim_getuserAdapter;
import com.example.loadingscreen.adapter.newmsgAdapter;
import com.example.loadingscreen.model.usersList_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class newmessage extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<usersList_model> userList;
    private newmsgAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ImageView backbtn;
    String userType = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmessage);
        recyclerView = findViewById(R.id.rv_newmsg);
        searchView = findViewById(R.id.sv_newmsg);
        radioGroup = findViewById(R.id.rg_usertype);
        backbtn = findViewById(R.id.backbtn_newmsg);
        backbtn.setOnClickListener(this);
        searchView.requestFocus();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                resultSearch(s.toLowerCase());
                return false;
            }
        });
    }

    public void resultSearch(final String text) {
        Query query;
            query = FirebaseDatabase.getInstance().getReference(userType)
                    .orderByChild("sname")
                    .startAt(text)
                    .endAt(text + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren() || snapshot.exists()) {
                        userList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            usersList_model newuser = dataSnapshot.getValue(usersList_model.class);
                            userList.add(newuser);
                        }
                        adapter = new newmsgAdapter(newmessage.this, userType);
                        adapter.filtered(userList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(newmessage.this));
                    } else {
                        if (userList != null) {
                            userList.clear();
                            adapter = new newmsgAdapter(newmessage.this, userType);
                            adapter.filtered(userList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(newmessage.this));
                            Toast.makeText(newmessage.this, "Please enter correct  name", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(newmessage.this, "Please enter correct  name", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (text.length() == 0) {
                        userList.clear();
                        adapter = new newmsgAdapter(newmessage.this, userType);
                        adapter.filtered(userList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(newmessage.this));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    public void usertype(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        userType = radioButton.getText().toString();
        searchView.setQuery("",false);
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn){
            finish();
        }
    }
}