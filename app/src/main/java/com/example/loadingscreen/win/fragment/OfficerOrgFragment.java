package com.example.loadingscreen.win.fragment;

import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.OrgUsers_model;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.Adapter.OfficerOrgAdapter;
import com.example.loadingscreen.win.activity.AddOfficer;
import com.example.loadingscreen.win.model.UserList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class OfficerOrgFragment extends Fragment {
    View officerView;
    RecyclerView officerRecyclerView;
    LinearLayoutManager linearLayoutManager;
    FloatingActionButton addOfficerBtn;
    private OfficerOrgAdapter adapter;
    private ArrayList<OrgUsers_model>users;
    private DatabaseReference orgUserRef,userRef,orgUsers1;;
    FirebaseAuth myAuth;
    String currentOrg;
    TextView officerCount;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        officerView = inflater.inflate(R.layout.fragment_officerorg, container, false);
        officerRecyclerView=officerView.findViewById(R.id.officerRecyclerView);
        officerCount=officerView.findViewById(R.id.officerCount);
        linearLayoutManager=new LinearLayoutManager(getContext());
        officerRecyclerView.setLayoutManager(linearLayoutManager);
        myAuth=FirebaseAuth.getInstance();
        currentOrg=myAuth.getCurrentUser().getUid();
        orgUserRef= FirebaseDatabase.getInstance().getReference().child("OrgUsers").child(currentOrg);
        userRef= FirebaseDatabase.getInstance().getReference().child("userstbl");
        orgUsers1=FirebaseDatabase.getInstance().getReference().child("OrgUsers").child(currentOrg);
        addOfficerBtn=officerView.findViewById(R.id.orgAddOfficer);
        addOfficerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddOfficer.class);
                startActivity(intent);
            }
        });
        return officerView;
    }
    @Override
    public void onStart() {
        super.onStart();
        users = new ArrayList<>();
        orgUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrgUsers_model orguser = dataSnapshot.getValue(OrgUsers_model.class);
                    users.add(orguser);
                }
                adapter = new OfficerOrgAdapter(getContext(),users,"");
                officerRecyclerView.setAdapter(adapter);
                officerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
