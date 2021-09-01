package com.example.loadingscreen.fragment;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.RecoverySystem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.Utils.lostfoundUtils;
import com.example.loadingscreen.adapter.claimAdapter;
import com.example.loadingscreen.model.claimList_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.loadingscreen.Utils.lostfoundUtils.FoundCache;
import static com.example.loadingscreen.Utils.lostfoundUtils.claimCache;

public class claim extends Fragment {
    private RecyclerView claim_rv;
    private ProgressBar progressBar;
    private ImageView nonet;
    private TextView lbl;
    private int totitem = 0;
    private int lastvisible;
    private Boolean end = false;
    private boolean isLoading = false;
    private claimAdapter claimer_adapter;
    final int loadcount = 3;
    private ArrayList<claimList_model> claimList;
    private ShimmerFrameLayout shimmerFrameLayout;
    check_network check;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_claim, container, false);
        claim_rv = view.findViewById(R.id.claimrecyclerview1);
        progressBar = view.findViewById(R.id.claimPB1);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_claim);
        nonet = view.findViewById(R.id.claim_icon_nonet);
        lbl = view.findViewById(R.id.claim_nopostornet);
        check = new check_network();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
                check.isConnected(getContext(), nonet, lbl, claim_rv, shimmerFrameLayout, progressBar);
            }
        },1000);
        return view;
    }


    public void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        claimer_adapter = new claimAdapter(getContext(), "");
        claim_rv.setLayoutManager(linearLayoutManager);
        claim_rv.setAdapter(claimer_adapter);
        if (claimCache.size() > 0) {
            claimer_adapter.addAll(claimCache,"");
            check.hideshimmer(shimmerFrameLayout, progressBar, null);
            claim_rv.setVisibility(View.VISIBLE);
        } else {
            getClaim(null,null);
        }
        scrollRv(linearLayoutManager);
    }

    public void scrollRv(final LinearLayoutManager linearLayoutManager) {
        claim_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totitem = linearLayoutManager.getItemCount();
                lastvisible = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totitem <= (lastvisible + loadcount)) {
                    if (!end) {
                        getClaim(claimer_adapter.getlastid(),null);
                        isLoading = true;
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void getClaim(String lastnode, final String tag) {
        progressBar.setVisibility(View.VISIBLE);
        Query query;
        if (lastnode == null)
            query = FirebaseDatabase.getInstance().getReference("Post").child("Clear")
                    .orderByKey().limitToFirst(loadcount);
        else
            query = FirebaseDatabase.getInstance().getReference("Post").child("Clear")
                    .orderByKey().startAt(lastnode).limitToFirst(loadcount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                claimList = new ArrayList<>();
                if (snapshot.hasChildren() || snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        claimList_model claims = dataSnapshot.getValue(claimList_model.class);
                        if (lostfoundUtils.claimList(dataSnapshot.getKey())) {
                            end = true;
                        } else {
                            end = false;
                            claimCache.add(claims);
                            claimList.add(claims);
                        }
                    }
                    if (!end) {
                        claimer_adapter.addAll(claimList,tag);
                    }
                    check.hideshimmer(shimmerFrameLayout, progressBar, null);
                    claim_rv.setVisibility(View.VISIBLE);
                    isLoading = false;
                    nonet.setVisibility(View.GONE);
                    lbl.setVisibility(View.GONE);
                } else {
                    check.hideshimmer(shimmerFrameLayout, progressBar, null);
                    claim_rv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    lbl.setVisibility(View.VISIBLE);
                    lbl.setText("Nothing to show here");
                    nonet.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
        check.isConnected(getContext(), nonet, lbl, claim_rv, shimmerFrameLayout, progressBar);
        shimmerFrameLayout.startShimmer();
    }


}