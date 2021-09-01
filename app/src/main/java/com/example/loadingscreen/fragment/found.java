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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.Utils.lostfoundUtils;
import com.example.loadingscreen.adapter.PostAdapter;
import com.example.loadingscreen.model.postList_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.loadingscreen.Utils.lostfoundUtils.FoundCache;


public class found extends Fragment{
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PostAdapter adapter;
    private ArrayList<postList_model> filterPost;
    private ArrayList<String> spinner_post;
    private ArrayList<String> populate_spinner;
    private Spinner spinner_listitem;
    private String item_name = "";
    private DatabaseReference reference;
    private String status = "Found";
    private boolean isLoading = false;
    private int visiblepos;
    private int totitemcount = 0;
    final int loadcount = 5;
    private Boolean end = false;
    private ArrayList<postList_model> postList;
    private ProgressBar progressBar;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView lbl;
    private ImageView nonetimg;
    check_network check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        recyclerView = view.findViewById(R.id.foundrecyclerview);
        autoCompleteTextView = view.findViewById(R.id.itemauto);
        reference = FirebaseDatabase.getInstance().getReference("Post");
        spinner_listitem = (Spinner) view.findViewById(R.id.spinner_listitem);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_lostnfound);
        progressBar = view.findViewById(R.id.foundPB);
        lbl = view.findViewById(R.id.found_lbl_nopost_nonet);
        nonetimg = view.findViewById(R.id.nonet_img);
        check = new check_network();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner_array();
                found_adapterRV();
                check.isConnected(getContext(), nonetimg, lbl, recyclerView, shimmerFrameLayout, progressBar);
            }
        }, 1000);
        return view;
    }


    public void found_adapterRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PostAdapter(getContext(), "");
        recyclerView.setAdapter(adapter);
        if (FoundCache.size() > 0) {
            adapter.addAll(FoundCache);
            check.hideshimmer(shimmerFrameLayout, progressBar, null);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            displayFound(null, null);
        }

        scrollRv(linearLayoutManager);
    }

    public void scrollRv(final LinearLayoutManager linearLayoutManager) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totitemcount = linearLayoutManager.getItemCount();
                visiblepos = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totitemcount <= (visiblepos + loadcount)) {
                    if (!end) {
                        displayFound(adapter.getLastid(), null);
                        isLoading = true;
                        progressBar.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }

    public void displayFound(String lastnode, final String tag) {
        progressBar.setVisibility(View.VISIBLE);
        postList = new ArrayList<>();
        Query query;
        if (lastnode == null)
            query = FirebaseDatabase.getInstance().getReference("Post")
                    .child("Found")
                    .orderByKey()
                    .limitToFirst(loadcount);
        else
            query = FirebaseDatabase.getInstance().getReference("Post")
                    .child("Found")
                    .orderByKey()
                    .startAt(lastnode)
                    .limitToFirst(loadcount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    postList.clear();
                    for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                        postList_model posts = dSnapshot.getValue(postList_model.class);
                        if (lostfoundUtils.foundList(dSnapshot.getKey())) {
                            end = true;
                        } else {
                            end = false;
                            FoundCache.add(posts);
                            postList.add(posts);
                        }
                    }

                    if (!end) {
                        adapter.addAll(postList);
                    }
                    check.hideshimmer(shimmerFrameLayout, progressBar, null);
                    recyclerView.setVisibility(View.VISIBLE);
                    nonetimg.setVisibility(View.GONE);
                    lbl.setVisibility(View.GONE);
                    isLoading = false;

                } else {
                    lbl.setVisibility(View.VISIBLE);
                    check.hideshimmer(shimmerFrameLayout, progressBar, null);
                    nonetimg.setVisibility(View.GONE);
                    lbl.setText("Nothing to show here");
                    recyclerView.setVisibility(View.GONE);
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
        final DatabaseReference spinnerReference = FirebaseDatabase.getInstance().getReference("Post").child("Found");
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
                                x = x + populate_spinner.size() + 1;
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
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdownlayout, populate_spinner);
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
        Query query;
        query = FirebaseDatabase.getInstance().getReference("Post").child("Found")
                .orderByChild("item").equalTo(item);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filterPost = new ArrayList<>();
                if (snapshot.exists()) {
                    filterPost.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        postList_model posts = dataSnapshot.getValue(postList_model.class);
                        filterPost.add(posts);
                    }
                    adapter = new PostAdapter(getContext(), "");
                    adapter.filter(filterPost);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {

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
        shimmerFrameLayout.startShimmer();
        check.isConnected(getContext(), nonetimg, lbl, recyclerView, shimmerFrameLayout, progressBar);
    }

    @Override
    public void onPause() {
        super.onPause();
        check.hideshimmer(shimmerFrameLayout, progressBar, null);
    }


}