package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.searchAdapter;
import com.example.loadingscreen.model.usersList_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchUser extends AppCompatActivity implements View.OnClickListener {
    private EditText searchbar;
    private SearchView searchView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ImageView backbtn;
    private RecyclerView recyclerView;
    private List<usersList_model> list_user;
    private DatabaseReference reference;
    private LinearLayoutManager layoutManager;
    private searchAdapter adapter;
    private String userStatus = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        searchbar = (EditText) findViewById(R.id.search_bar);
        searchView = findViewById(R.id.searchView);
        backbtn = findViewById(R.id.back_btn);
        radioGroup = findViewById(R.id.searchUserRg);
        recyclerView = findViewById(R.id.recycleviewSearch);
        layoutManager = new LinearLayoutManager(this);
        backbtn.setOnClickListener(this);
        displaUser();
        /**searchbar.addTextChangedListener(new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override public void afterTextChanged(Editable editable) {
        search_user(editable.toString());
        }
        });*/
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search_user(s);
                return false;
            }
        });
    }

    private void search_user(String text) {
        ArrayList<usersList_model> userList = new ArrayList<>();
        userList.clear();
        for (usersList_model data : list_user) {
            if (data.getFullname().toLowerCase().indexOf(text.toLowerCase()) == 0) {
                userList.add(data);
            }
        }
        adapter.filteredList(userList, text);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void displaUser() {
        reference = FirebaseDatabase.getInstance().getReference(userStatus);
        list_user = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_user.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    usersList_model userData = new usersList_model();
                    userData.setProfileimg(dataSnapshot.child("profileimg").getValue().toString());
                    userData.setFullname(dataSnapshot.child("fullname").getValue().toString());
                    userData.setUserID(dataSnapshot.child("userID").getValue().toString());
                    userData.setAbout(dataSnapshot.child("about").getValue().toString());
                    list_user.add(userData);
                }
                adapter = new searchAdapter(searchUser.this, list_user,userStatus);
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

    public void searchuser(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        userStatus = radioButton.getText().toString();
        searchView.setQuery("",false);
        displaUser();
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn){
            finish();
        }
    }
}