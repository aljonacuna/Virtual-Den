package com.example.loadingscreen.Utils;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.loadingscreen.model.postList_model;
import com.example.loadingscreen.win.activity.LetterforDean;
import com.example.loadingscreen.win.model.LetterDean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class checkIfThereAreNewData {
    public static ArrayList<LetterDean> letter_List;
    public static ValueEventListener eventListener;
    public static DatabaseReference checkref;
    public static ValueEventListener postListener;
    public static DatabaseReference count_post;
    public static interface LetterListCallBack {
        void onCallBack(ArrayList<LetterDean> letterDeans);
    }

    public static void checkData(final LetterListCallBack myCallback) {
        letter_List = new ArrayList<>();
        checkref = FirebaseDatabase.getInstance().getReference("LetterDean");
        eventListener = checkref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                letter_List.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LetterDean letterforDean = dataSnapshot.getValue(LetterDean.class);
                    letter_List.add(letterforDean);
                }
                myCallback.onCallBack(letter_List);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
