package com.example.loadingscreen.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.classSchedAdapter;
import com.example.loadingscreen.model.class_sched_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class blis_class_sched extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private classSchedAdapter adapter;
    private DatabaseReference reference;
    private boolean maxdata = false;
    private String lastnode = "";
    private String last_key = "";
    final int load_count = 7;
    private Button load;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blis_class_sched, container, false);
        recyclerView = view.findViewById(R.id.recyclerviewBlis);
        load = view.findViewById(R.id.load);
        reference = FirebaseDatabase.getInstance().getReference("Schedule").child("Class");
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        getlast();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        load.setOnClickListener(this);
        load.setEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new classSchedAdapter(getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void displayClassSched() {
        if (!maxdata) {
            Query query;
            if (TextUtils.isEmpty(lastnode)) {
                query = reference.orderByKey().limitToFirst(load_count);
            } else {
                query = reference.orderByKey().startAt(lastnode).limitToFirst(load_count);
            }
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        List<class_sched_model> classSchedList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            class_sched_model classScheds = dataSnapshot.getValue(class_sched_model.class);
                            if (classScheds.getCourse().equals("BLIS")) {
                                classSchedList.add(classScheds);
                            }
                        }
                        if (classSchedList.size() > 0) {
                            lastnode = classSchedList.get(classSchedList.size() - 1).getKey();
                            if (!lastnode.equals(last_key)) {
                                classSchedList.remove(classSchedList.size() - 1);
                            } else {
                                lastnode = "end";
                            }
                        }

                        adapter.addAll(classSchedList);
                        load.setEnabled(true);

                    } else {
                        maxdata = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getlast() {
        Query getlastkey = FirebaseDatabase.getInstance().getReference("Schedule").child("Class")
                .orderByKey().limitToLast(1);
        getlastkey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    last_key = dataSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == load) {
            loadmore();
        }
    }

    public void loadmore() {
        maxdata = false;
        lastnode = adapter.getLastId();
        adapter.removelastitem();
        adapter.notifyDataSetChanged();
        getlast();
        displayClassSched();
    }
}