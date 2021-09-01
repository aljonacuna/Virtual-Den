package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.Utils.classSchedUtils;
import com.example.loadingscreen.adapter.blisAdapter;
import com.example.loadingscreen.adapter.classSchedAdapter;
import com.example.loadingscreen.adapter.classSearchAdapter;
import com.example.loadingscreen.model.class_sched_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.loadingscreen.Utils.classSchedUtils.blisCache;
import static com.example.loadingscreen.Utils.classSchedUtils.bsitCache;

public class class_schedule extends AppCompatActivity implements View.OnClickListener {
    private classSchedAdapter class_adapter;
    private classSearchAdapter searchClassAdapter;
    private List<class_sched_model> classSearch;
    private RecyclerView recyclerViewCS;
    private SearchView searchViewCS;
    private String lastkeyBLIS = "";
    final int loadcount = 5;
    private DatabaseReference reference;
    private ImageView backbtn, nonetimg;
    private boolean max_data = false;
    private Button bsitbtn, blisbtn;
    private String course = "BSIT";
    private List<class_sched_model> class_schedList;
    private String search = "";
    private blisAdapter blis_adapter;
    private boolean isLoading;
    private int visiblepostion;
    private int totItemcount = 0;
    private ProgressBar progressBar;
    private Boolean endbsit = false;
    private Boolean endblis = false;
    private TextView nonetlbl;
    private ShimmerFrameLayout shimmerFrameLayout;
    check_network check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_schedule);
        recyclerViewCS = findViewById(R.id.class_schedRv);
        searchViewCS = findViewById(R.id.searchViewCS);
        backbtn = findViewById(R.id.backbtn_classsched);
        bsitbtn = findViewById(R.id.bsitbtn);
        blisbtn = findViewById(R.id.blisbtn);
        shimmerFrameLayout = findViewById(R.id.shimmer_cs);
        nonetimg = findViewById(R.id.nonet_img_class);
        nonetlbl = findViewById(R.id.no_net_lbl);
        progressBar = findViewById(R.id.pbloadmore);
        backbtn.setOnClickListener(this);
        bsitbtn.setOnClickListener(this);
        blisbtn.setOnClickListener(this);
        check = new check_network();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checknetwork();
            }
        },1000);


        searchViewCS.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchViewCS.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search = s;
                LinearLayoutManager lineasearch = new LinearLayoutManager(class_schedule.this);
                schedDatasearch(s.toUpperCase());
                return false;
            }
        });
    }

    private void checknetwork() {
        if (!check.isConnectedClass(this)) {
            nonetimg.setVisibility(View.VISIBLE);
            nonetlbl.setText("No internet connection");
            nonetlbl.setVisibility(View.VISIBLE);
            recyclerViewCS.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
        } else {
            if (course.equals("BSIT")) {
                displayBSIT();
            } else if (course.equals("BLIS")) {
                displayBLIS();
            }
        }
    }

    public void displayBSIT() {
        nonetlbl.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(class_schedule.this);
        class_adapter = new classSchedAdapter(class_schedule.this);
        recyclerViewCS.setAdapter(class_adapter);
        recyclerViewCS.setLayoutManager(linearLayoutManager);
        if (bsitCache.size() > 0) {
            class_adapter.addAll(bsitCache);
            hideshimer();
            recyclerViewCS.setVisibility(View.VISIBLE);
        } else {
            displaySchedBSIT(null);
        }

        scrollRV(linearLayoutManager);

    }

    public void displayBLIS() {
        nonetlbl.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(class_schedule.this);
        class_adapter = new classSchedAdapter(class_schedule.this);
        recyclerViewCS.setAdapter(class_adapter);
        recyclerViewCS.setLayoutManager(linearLayoutManager);
        if (blisCache.size() > 0) {
            class_adapter.addAll(blisCache);
            hideshimer();
            recyclerViewCS.setVisibility(View.VISIBLE);
        } else {
            displaySchedBLIS(null);
        }
        scrollRV(linearLayoutManager);
    }

    @Override
    public void onClick(View view) {
        if (view == bsitbtn) {
            course = "BSIT";
            bsitbtn.setTextColor(Color.parseColor("#4B8BBE"));
            blisbtn.setTextColor(Color.parseColor("#000000"));
            if (!check.isConnectedClass(this)) {
                Toast.makeText(this, "Unstable internet connection", Toast.LENGTH_SHORT).show();
            }else{
                displayBSIT();
            }
        } else if (view == blisbtn) {
            course = "BLIS";
            blisbtn.setTextColor(Color.parseColor("#4B8BBE"));
            bsitbtn.setTextColor(Color.parseColor("#000000"));
            if (!check.isConnectedClass(this)) {
                Toast.makeText(this, "Unstable internet connection", Toast.LENGTH_SHORT).show();
            }else{
                displayBLIS();
            }

        } else if (view == backbtn) {
            finish();
        }
    }

    public void scrollRV(final LinearLayoutManager linearLayoutManager) {
        recyclerViewCS.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totItemcount = linearLayoutManager.getItemCount();
                visiblepostion = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totItemcount <= (visiblepostion + loadcount)) {
                    if (course.equals("BSIT")) {
                        if (!endbsit) {
                            displaySchedBSIT(class_adapter.getLastId());
                            isLoading = true;
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    } else if (course.equals("BLIS")) {
                        if (!endblis) {
                            displaySchedBLIS(class_adapter.getLastId());
                            isLoading = true;
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                    if (dy > 0) {

                    }
                }
            }
        });
    }


    public void displaySchedBSIT(String lastnodeBSIT) {
        Query query;
        progressBar.setVisibility(View.VISIBLE);
        if (lastnodeBSIT == null)
            query = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child(course)
                    .orderByKey()
                    .limitToFirst(loadcount);

        else
            query = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child(course)
                    .orderByKey()
                    .startAt(lastnodeBSIT)
                    .limitToFirst(loadcount);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    class_schedList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        class_sched_model classSched = dataSnapshot.getValue(class_sched_model.class);

                        if (classSchedUtils.bsitList(dataSnapshot.getKey())) {
                            endbsit = true;
                        } else {
                            endbsit = false;
                            class_schedList.add(classSched);
                            bsitCache.add(classSched);
                        }
                    }
                    if (!endbsit) {
                        class_adapter.addAll(class_schedList);
                        isLoading = false;
                        recyclerViewCS.setVisibility(View.VISIBLE);
                    }
                    hideshimer();
                } else {
                    nonetlbl.setText("No schedule yet");
                    nonetlbl.setVisibility(View.VISIBLE);
                    recyclerViewCS.setVisibility(View.GONE);
                    hideshimer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;
            }
        });
    }


    public void displaySchedBLIS(String lastnodeBLIS) {
        progressBar.setVisibility(View.VISIBLE);
        Query query;
        if (lastnodeBLIS == null)
            query = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child(course)
                    .orderByKey()
                    .limitToFirst(loadcount);
        else
            query = FirebaseDatabase.getInstance().getReference("Schedule")
                    .child("Class")
                    .child(course)
                    .orderByKey()
                    .startAt(lastnodeBLIS)
                    .limitToFirst(loadcount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    class_schedList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        class_sched_model classSched = dataSnapshot.getValue(class_sched_model.class);
                        if (classSchedUtils.blisList(dataSnapshot.getKey())) {
                            endblis = true;
                        } else {
                            endblis = false;
                            blisCache.add(classSched);
                            class_schedList.add(classSched);
                        }

                    }
                    if (!endblis) {
                        class_adapter.addAll(class_schedList);
                        isLoading = false;
                    }
                    hideshimer();
                    recyclerViewCS.setVisibility(View.VISIBLE);
                } else {
                    nonetlbl.setText("No schedule yet");
                    nonetlbl.setVisibility(View.VISIBLE);
                    recyclerViewCS.setVisibility(View.GONE);
                    hideshimer();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;
            }
        });
    }

    //show shimmer

    private void hideshimer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    public void schedDatasearch(final String text) {
        classSearch = new ArrayList<>();
        final String tag = "main";
        Query query = FirebaseDatabase.getInstance().getReference("Schedule").child("Class").child(course)
                .orderByChild("yearsection").startAt(text).endAt(text + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if the inputted text is existing
                if (snapshot.hasChildren()) {
                    classSearch.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        class_sched_model class_search = dataSnapshot.getValue(class_sched_model.class);
                        classSearch.add(class_search);
                    }
                    searchClassAdapter = new classSearchAdapter(class_schedule.this, tag);
                    searchClassAdapter.filteredList(classSearch);
                    recyclerViewCS.setAdapter(searchClassAdapter);
                    recyclerViewCS.setLayoutManager(new LinearLayoutManager(class_schedule.this));
                }
                //else return no result found
                else {
                    Toast.makeText(class_schedule.this, "No result found", Toast.LENGTH_LONG).show();
                    classSearch.clear();
                    searchClassAdapter = new classSearchAdapter(class_schedule.this, tag);
                    searchClassAdapter.filteredList(classSearch);
                    recyclerViewCS.setAdapter(searchClassAdapter);
                    recyclerViewCS.setLayoutManager(new LinearLayoutManager(class_schedule.this));
                }
                //if the they cleared searchview show the loadcount result
                if (text.length() == 0) {
                    if (course.equals("BSIT")) {
                        displayBSIT();
                    } else if (course.equals("BLIS")) {
                        displayBLIS();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        check.isConnected(this, nonetimg, nonetlbl, recyclerViewCS, shimmerFrameLayout, progressBar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideshimer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}