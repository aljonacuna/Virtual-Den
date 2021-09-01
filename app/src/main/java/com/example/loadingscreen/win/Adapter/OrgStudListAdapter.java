package com.example.loadingscreen.win.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.activity.StudentOrgView;
import com.example.loadingscreen.win.fragment.HomeOrgFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrgStudListAdapter extends RecyclerView.Adapter<OrgStudListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<usersList_model>orgsList;

    public OrgStudListAdapter(Context context, ArrayList<usersList_model> orgsList) {
        this.context = context;
        this.orgsList = orgsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orgliststudview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        usersList_model newOrgs = orgsList.get(position);
        final String imgprofile = newOrgs.getProfileimg();
        final String orgNameTxt = newOrgs.getFullname();
        holder.orgname.setText(orgNameTxt);
        Picasso.with(context).load(imgprofile).placeholder(R.drawable.placeholder).into(holder.orgImg);
        final String id = newOrgs.getUserID();
        getPostCount(id,holder.postCount);
        getOfficialCount(id,holder.officialCount);
        holder.parentRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String post_Count = holder.postCount.getText().toString();
                Intent intent = new Intent(context, StudentOrgView.class);
                intent.putExtra("id",id);
                intent.putExtra("name",orgNameTxt);
                intent.putExtra("img",imgprofile);
                intent.putExtra("count",post_Count);
                context.startActivity(intent);
            }
        });

    }
    public void getPostCount(final  String id, final TextView textView){
        final ArrayList<String>getOrgPost = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Announcements");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String orgAuthorId = dataSnapshot.child("orgkey").getValue(String.class);
                    if (orgAuthorId.equals(id)){
                        String postKey = dataSnapshot.child("postkey").getValue(String.class);
                        getOrgPost.add(postKey);
                    }
                }
                int count = 0;
                for (String toGetCount:getOrgPost){
                    count = count + 1;
                }
                String strCount = String.valueOf(count);
                textView.setText(strCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getOfficialCount(String id, final TextView officialCount){
        final DatabaseReference orgUsers1=FirebaseDatabase.getInstance().getReference().child("OrgUsers").child(id);
        orgUsers1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                String strCount = String.valueOf(count);
                officialCount.setText(strCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return orgsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parentRel;
        private TextView orgname,officialCount,postCount;
        private ImageView orgImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentRel = itemView.findViewById(R.id.org_rel);
            orgname = itemView.findViewById(R.id.org_Name);
            officialCount = itemView.findViewById(R.id.officials_count);
            postCount = itemView.findViewById(R.id.CountPost);
            orgImg = itemView.findViewById(R.id.org_Image);
        }
    }
}
