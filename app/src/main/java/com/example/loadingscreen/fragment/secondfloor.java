package com.example.loadingscreen.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.roomAdapter;
import com.example.loadingscreen.model.roomList_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class secondfloor extends Fragment {
    private RecyclerView recyclerView;
    private roomAdapter room_adapter;
    private ArrayList<roomList_model> roomList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_secondfloor, container, false);
        recyclerView = view.findViewById(R.id.recycleviewsecond);
        roomList = new ArrayList<>();
        displayRoom();
        return view;
    }
    public void displayRoom() {
        final String firsfloor = "Second Floor";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    roomList_model rooms = dataSnapshot.getValue(roomList_model.class);
                    if(firsfloor.equals(rooms.getRoomplace())){
                        roomList.add(rooms);
                    }
                }
                room_adapter = new roomAdapter(getContext(),roomList);
                recyclerView.setAdapter(room_adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}