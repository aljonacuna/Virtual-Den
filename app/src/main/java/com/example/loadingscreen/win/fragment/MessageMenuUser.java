package com.example.loadingscreen.win.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.loadingscreen.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.loadingscreen.activity_gui.contactList;
import com.example.loadingscreen.activity_gui.newmessage;
import com.example.loadingscreen.adapter.contactAdapter;
import com.example.loadingscreen.model.contact_list;
import com.example.loadingscreen.model.usersList_model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MessageMenuUser extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ArrayList<contact_list> contactLists;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<usersList_model> user_list;
    private contactAdapter contact_adapter;
    private FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_message_menu_user, container, false);

        recyclerView = view.findViewById(R.id.recycler_contact);
        floatingActionButton = view.findViewById(R.id.newmsgbtn);
        floatingActionButton.setOnClickListener(this);
        loadContact();
       return view;
    }

    @Override
    public void onClick(View view) {
        if (view == floatingActionButton){
            Intent intent = new Intent(getContext(), newmessage.class);
            startActivity(intent);
        }
    }
    public void loadContact(){
        contactLists = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUser = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("ContactList").child(currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                contactLists.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        contact_list contact = dataSnapshot.getValue(contact_list.class);
                        contactLists.add(contact);
                    }
                    Collections.sort(contactLists,Collections.reverseOrder(new Comparator<contact_list>() {
                        @Override
                        public int compare(contact_list contact_list1, contact_list contact_list2) {
                            return contact_list1.getTimestamp().compareTo(contact_list2.getTimestamp());
                        }
                    }));
                    contact_adapter = new contactAdapter(getContext(),contactLists);
                    recyclerView.setAdapter(contact_adapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}