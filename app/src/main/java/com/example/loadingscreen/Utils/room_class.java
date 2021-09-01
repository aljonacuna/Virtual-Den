package com.example.loadingscreen.Utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.adapter.roomAdapter;
import com.example.loadingscreen.model.roomList_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class room_class {
    public roomAdapter room_adapter;
    public ArrayList<roomList_model>roomList;
    public void displayRoom(final RecyclerView recyclerView, final String place, final Context context) {
        roomList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    roomList_model rooms = dataSnapshot.getValue(roomList_model.class);
                    if(place.equals(rooms.getRoomplace())){
                        roomList.add(rooms);
                    }
                }
                room_adapter = new roomAdapter(context,roomList);
                recyclerView.setAdapter(room_adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
