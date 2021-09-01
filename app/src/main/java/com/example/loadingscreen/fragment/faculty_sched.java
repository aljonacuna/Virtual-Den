package com.example.loadingscreen.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;

import java.util.ArrayList;


public class faculty_sched extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<usersList_model>userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_faculty_sched, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFaculty);
        return view;
    }
}