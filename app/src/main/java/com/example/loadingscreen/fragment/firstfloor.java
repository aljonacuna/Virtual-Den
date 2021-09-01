package com.example.loadingscreen.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.room_class;
import com.example.loadingscreen.adapter.roomAdapter;
import com.example.loadingscreen.model.roomList_model;

import java.util.ArrayList;

public class firstfloor extends Fragment {
    private RecyclerView recyclerView;
    private roomAdapter room_adapter;
    private ArrayList<roomList_model> roomList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firstfloor, container, false);
        recyclerView = view.findViewById(R.id.recycleviewfirst);
        room_class rooms = new room_class();
        rooms.displayRoom(recyclerView,"First Floor",getContext());
        return view;

    }
}