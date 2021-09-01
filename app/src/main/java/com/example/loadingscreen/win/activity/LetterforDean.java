package com.example.loadingscreen.win.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.checkIfThereAreNewData;
import com.example.loadingscreen.Utils.check_network;
import com.example.loadingscreen.win.Adapter.LetterDeanAdapter;
import com.example.loadingscreen.win.model.LetterDean;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LetterforDean extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private LetterDeanAdapter adapter;
    private ArrayList<LetterDean> letterList;
    private FloatingActionButton addLetter;
    private ImageView backbtn,nonet;
    private DatabaseReference reference;
    ValueEventListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    checkIfThereAreNewData checkNewData;
    check_network check;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView lbl;
    private ConstraintLayout constraintLayout;
    String letter ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letterfor_dean);
        recyclerView = findViewById(R.id.letterRV);
        addLetter = findViewById(R.id.addLetterBtn);
        backbtn = findViewById(R.id.backbtn_letter4dean);
        swipeRefreshLayout = findViewById(R.id.refreshLetter);
        shimmerFrameLayout = findViewById(R.id.shimmer_letter);
        nonet = findViewById(R.id.nonet_letter);
        lbl = findViewById(R.id.lbl_letter_net);
        constraintLayout = findViewById(R.id.mainLayout);
        addLetter.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        backbtn.setOnClickListener(this);
        checkNewData = new checkIfThereAreNewData();
        check = new check_network();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayLetter();
            }
        },1000);

    }

    @Override
    public void onClick(View view) {
        if (view == addLetter) {
            Intent intent = new Intent(this, AddLetter.class);
            startActivity(intent);
            finish();
        } else if (view == backbtn) {
            finish();
        }
    }

    public void displayLetter() {
        letterList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("LetterDean");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    letterList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LetterDean letterDean = dataSnapshot.getValue(LetterDean.class);
                        letterList.add(letterDean);
                    }
                    check.hideshimmer(shimmerFrameLayout,null,swipeRefreshLayout);
                    lbl.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new LetterDeanAdapter(LetterforDean.this, letterList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(LetterforDean.this));
                    checkNewData.checkData(new checkIfThereAreNewData.LetterListCallBack() {
                        @Override
                        public void onCallBack(ArrayList<LetterDean> letterDeans) {
                            if (letterDeans.size() > letterList.size()) {
                                int newletter = letterDeans.size() - letterList.size();
                                if (newletter>1) {
                                    letter = "<font color=\"#FFFFFF\">"+String.valueOf(newletter)+" unread letters"+"</font>";
                                }else{
                                    letter = "<font color=\"#FFFFFF\">"+String.valueOf(newletter)+" unread letter"+"</font>";
                                }
                                final Snackbar snackbar = Snackbar.make(constraintLayout, Html.fromHtml(letter),Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Read", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        displayLetter();
                                        try {
                                            adapter.notifyDataSetChanged();
                                            checkNewData.checkref.removeEventListener(checkNewData.eventListener);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(LetterforDean.this, "Error: Unstable internet connection ", Toast.LENGTH_SHORT).show();
                                        }
                                        snackbar.dismiss();
                                    }
                                });
                                snackbar.show();
                            }
                        }
                    });
                }else {
                    check.hideshimmer(shimmerFrameLayout,null,swipeRefreshLayout);
                    lbl.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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
        check.isConnected(this,nonet,lbl,recyclerView,shimmerFrameLayout,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        check.hideshimmer(shimmerFrameLayout,null,swipeRefreshLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            checkNewData.checkref.removeEventListener(checkNewData.eventListener);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onRefresh() {
        displayLetter();
        try {
            adapter.notifyDataSetChanged();
            checkNewData.checkref.removeEventListener(checkNewData.eventListener);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Unstable internet connection ", Toast.LENGTH_SHORT).show();
        }
    }
}