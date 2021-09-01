package com.example.loadingscreen.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.checkIfThereAreNewData;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.Utils.lostfoundUtils;
import com.example.loadingscreen.adapter.PostAdapter;
import com.example.loadingscreen.model.postList_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.example.loadingscreen.Utils.lostfoundUtils.FoundCache;
import static com.example.loadingscreen.Utils.lostfoundUtils.LostCache;

public class lost extends Fragment{
    private RecyclerView recyclerViewLost;
    private PostAdapter adapter;
    private TextView lbl;
    private ImageView nonetimg;
    private ArrayList<String> spinner_post;
    private ArrayList<String> populate_spinner;
    private Spinner spinner_listitem;
    private Button filterBtn;
    private String item_name = "";
    private DatabaseReference reference;
    private String status = "Lost";
    private int visiblepos;
    private int totitem = 0;
    private int countFilter = 0;
    private Boolean end = false;
    private boolean isLoading = false;
    final int loadcount = 10;
    private ArrayList<postList_model> post_List;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;
    check_network check;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lost, container, false);
        recyclerViewLost= view.findViewById(R.id.lostrecyclerview);
        spinner_listitem = (Spinner) view.findViewById(R.id.spinner_listitem);
        shimmerFrameLayout = view.findViewById(R.id.shimmerlayout_lost);
        autoCompleteTextView = view.findViewById(R.id.itemautoLost);
        progressBar = view.findViewById(R.id.lostPB);
        nonetimg = view.findViewById(R.id.no_net_imglost);
        lbl = view.findViewById(R.id.lost_lbl_nopost_nonet);
        check = new check_network();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner_array();
                lostadapterRv();
                check.isConnected(getContext(), nonetimg, lbl, recyclerViewLost, shimmerFrameLayout, progressBar);
            }
        }, 1000);
        return view;

    }

    public void scrollRv(final LinearLayoutManager linearLayoutManager) {
        recyclerViewLost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totitem = linearLayoutManager.getItemCount();
                visiblepos = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totitem <= (visiblepos + loadcount)) {
                    if (!end) {
                        displayLost(adapter.getLastid(),null);
                        isLoading = true;
                        progressBar.setVisibility(View.VISIBLE);
                    }

                }

            }
        });
    }

    public void lostadapterRv() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewLost.setLayoutManager(linearLayoutManager);
        adapter = new PostAdapter(getContext(), "");
        recyclerViewLost.setAdapter(adapter);
        if (LostCache.size() > 0) {
            adapter.addAll(LostCache);
            check.hideshimmer(shimmerFrameLayout, progressBar,null);
            recyclerViewLost.setVisibility(View.VISIBLE);
        } else {
            displayLost(null,null);
        }
        scrollRv(linearLayoutManager);
    }

    public void displayLost(String lastnode, final String tag) {
        post_List = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        Query query;
        if (lastnode == null)
            query = FirebaseDatabase.getInstance().getReference("Post")
                    .child("Lost")
                    .orderByKey()
                    .limitToFirst(loadcount);
        else
            query = FirebaseDatabase.getInstance().getReference("Post")
                    .child("Lost")
                    .orderByKey()
                    .startAt(lastnode)
                    .limitToFirst(loadcount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    post_List.clear();
                    for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                        postList_model posts = dSnapshot.getValue(postList_model.class);
                        if (lostfoundUtils.lostList(dSnapshot.getKey())) {
                            end = true;
                        } else {
                            end = false;
                            LostCache.add(posts);
                            post_List.add(posts);
                        }
                    }
                    if (!end) {
                        adapter.addAll(post_List);
                    }
                    recyclerViewLost.setVisibility(View.VISIBLE);
                    isLoading = false;
                    check.hideshimmer(shimmerFrameLayout, progressBar, null);
                    nonetimg.setVisibility(View.GONE);
                    lbl.setVisibility(View.GONE);
                } else {
                    check.hideshimmer(shimmerFrameLayout, progressBar, null);
                    recyclerViewLost.setVisibility(View.GONE);
                    nonetimg.setVisibility(View.GONE);
                    lbl.setVisibility(View.VISIBLE);
                    lbl.setText("Nothing to show here");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void spinner_array() {
        spinner_post = new ArrayList<String>();
        populate_spinner = new ArrayList<String>();
        DatabaseReference spinnerReference = FirebaseDatabase.getInstance().getReference("Post").child(status);
        spinnerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        String itemList = dataSnapshot.child("item").getValue(String.class);
                        spinner_post.add(itemList);
                    }
                }
                //remove duplicate items from autocomplete
                for (String arrTofilter : spinner_post) {
                    if (populate_spinner.size() > 0) {
                        for (int x = 0; x < populate_spinner.size(); x++) {
                            if (arrTofilter.toLowerCase().equals(populate_spinner.get(x).toLowerCase())) {
                                //stop loop
                                x = x + populate_spinner.size()+1;
                            } else {
                                //if not equals and populate_spinner is less than or equal to x means all data been
                                // loop and still not found
                                if (x + 1 >= populate_spinner.size()) {
                                    populate_spinner.add(arrTofilter);
                                }
                            }
                        }
                    } else {
                        populate_spinner.add(arrTofilter);
                    }
                }

                if (getActivity() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdownlayout, populate_spinner);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String item = (String) adapterView.getItemAtPosition(i);
                            filteringpostLostandFound(item);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void filteringpostLostandFound(String item) {
        Query query = FirebaseDatabase.getInstance().getReference("Post").child(status)
                .orderByChild("item").equalTo(item);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<postList_model> filterlost = new ArrayList<>();
                if (snapshot.exists()) {
                    filterlost.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        postList_model posts = dataSnapshot.getValue(postList_model.class);
                        if (!posts.getStatus().equals("Clear")) {
                            filterlost.add(posts);
                        }
                    }
                    adapter = new PostAdapter(getContext(), "");
                    adapter.filter(filterlost);
                    recyclerViewLost.setAdapter(adapter);
                    recyclerViewLost.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        check.isConnected(getContext(), nonetimg, lbl, recyclerViewLost, shimmerFrameLayout, progressBar);
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

}