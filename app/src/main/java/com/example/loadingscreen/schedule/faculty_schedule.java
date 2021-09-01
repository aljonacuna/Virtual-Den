package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.Utils.utils;
import com.example.loadingscreen.adapter.facultySchedAdapter;
import com.example.loadingscreen.adapter.facultySearchAdapter;
import com.example.loadingscreen.model.usersList_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.loadingscreen.Utils.utils.DataCache;
import static java.util.Objects.requireNonNull;

public class faculty_schedule extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView fs_rv;
    private facultySchedAdapter fs_adapter;
    private facultySearchAdapter search_adapterFs;
    private DatabaseReference reference;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ProgressBar progressBar;
    private SearchView searchViewFs;
    private ImageView backbtn, nonetimg;
    private TextView nonetlbl;
    private boolean isLoading = false;
    private int visiblepos;
    private int totitemcount = 0;
    final int loadcount = 2;
    private Boolean end = false;
    check_network check;
    private List<usersList_model> SchedFS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_schedule);
        nonetimg = findViewById(R.id.nonet_imgfs);
        nonetlbl = findViewById(R.id.no_net_lblfs);
        fs_rv = findViewById(R.id.fs_rv);
        searchViewFs = findViewById(R.id.searchViewFS);
        backbtn = findViewById(R.id.fs_backbtn);
        shimmerFrameLayout = findViewById(R.id.shimmer_fs);
        progressBar = findViewById(R.id.pb_fs);
        backbtn.setOnClickListener(this);
        check = new check_network();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapterxrv();
                check.isConnected(faculty_schedule.this, nonetimg, nonetlbl, fs_rv, shimmerFrameLayout, progressBar);
            }
        },1000);

        searchViewFs.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        searchViewFs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchE(s.toLowerCase());
                return false;
            }
        });
    }


    public void adapterxrv() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(faculty_schedule.this);
        fs_adapter = new facultySchedAdapter(faculty_schedule.this);
        fs_rv.setAdapter(fs_adapter);
        fs_rv.setLayoutManager(linearLayoutManager);
        if (DataCache.size() > 0) {
            fs_rv.setVisibility(View.VISIBLE);
            fs_adapter.addAll(DataCache);
            hideshimer();
        } else {
            displaySchedFS(null);
        }
        scrollRV(linearLayoutManager);
    }

    private void scrollRV(final LinearLayoutManager linearLayoutManager) {
        fs_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totitemcount = linearLayoutManager.getItemCount();
                visiblepos = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totitemcount <= (visiblepos + loadcount)) {
                    if (!end) {
                        displaySchedFS(fs_adapter.getLastId());
                        progressBar.setVisibility(View.VISIBLE);
                        isLoading = true;
                    }
                }
            }
        });
    }


    private void displaySchedFS(String lastnode) {
        progressBar.setVisibility(View.VISIBLE);
        Query query;
        if (lastnode == null)
            query = FirebaseDatabase.getInstance().getReference("Faculty")
                    .orderByKey()
                    .limitToFirst(loadcount);
        else
            query = FirebaseDatabase.getInstance().getReference("Faculty")
                    .orderByKey()
                    .startAt(lastnode)
                    .limitToFirst(loadcount);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<usersList_model> user_list = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getChildrenCount() > 0) {
                            usersList_model users = ds.getValue(usersList_model.class);
                            if (utils.facultyExist(ds.getKey())) {
                                end = true;
                            } else {
                                end = false;
                                DataCache.add(users);
                                user_list.add(users);
                            }
                        } else {

                        }
                    }
                    if (!end) {
                        fs_adapter.addAll(user_list);
                    }
                    isLoading = false;
                    hideshimer();
                    fs_rv.setVisibility(View.VISIBLE);
                    nonetlbl.setVisibility(View.GONE);
                    nonetimg.setVisibility(View.GONE);
                } else {
                    nonetlbl.setVisibility(View.VISIBLE);
                    fs_rv.setVisibility(View.GONE);
                    nonetlbl.setText("No schedule yet");
                    hideshimer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isLoading = false;
            }
        });
    }

    public void searchE(final String text) {
        SchedFS = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("Faculty")
                .orderByChild("sname")
                .startAt(text)
                .endAt(text + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    SchedFS.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        usersList_model staffList = dataSnapshot.getValue(usersList_model.class);
                        SchedFS.add(staffList);
                    }
                    search_adapterFs = new facultySearchAdapter(faculty_schedule.this);
                    search_adapterFs.filtered(SchedFS);
                    fs_rv.setAdapter(search_adapterFs);
                    fs_rv.setLayoutManager(new LinearLayoutManager(faculty_schedule.this));

                } else {
                    Toast.makeText(faculty_schedule.this, "No result found", Toast.LENGTH_LONG).show();
                    SchedFS.clear();
                    search_adapterFs = new facultySearchAdapter(faculty_schedule.this);
                    search_adapterFs.filtered(SchedFS);
                    fs_rv.setAdapter(search_adapterFs);
                    fs_rv.setLayoutManager(new LinearLayoutManager(faculty_schedule.this));
                }
                if (text.length() == 0) {
                    adapterxrv();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hideshimer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        check.isConnected(this, nonetimg, nonetlbl, fs_rv, shimmerFrameLayout, progressBar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn) {
            finish();
        }
    }
}