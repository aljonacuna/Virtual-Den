package com.example.loadingscreen.win.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.model.UserList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class OrgListAdapter extends RecyclerView.Adapter<OrgListAdapter.OrgViewHolder> {
    ArrayList<usersList_model>orgs;
    Context context;

    public OrgListAdapter(Context c,ArrayList<usersList_model>o) {
        this.context=c;
        this.orgs=o;
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.orglistdesign,parent,false);
        return new OrgViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
        usersList_model orgPos = orgs.get(position);
        holder.orgName.setText(orgPos.getFullname());
        holder.orgAbout.setText(orgPos.getAbout());
        Picasso.with(context).load(orgPos.getProfileimg()).resize(40,40)
                .transform(new CropCircleTransformation()).into(holder.orgImage);
    }

    @Override
    public int getItemCount() {
        return orgs.size();
    }


    public class OrgViewHolder extends RecyclerView.ViewHolder{
        TextView orgName,orgAbout;
        ImageView orgImage;
        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            orgName=itemView.findViewById(R.id.orgNameList);
            orgAbout=itemView.findViewById(R.id.orgAboutList);
            orgImage=itemView.findViewById(R.id.orgImageList);
        }
    }


    public  void filterList(ArrayList<usersList_model> filteredList){
        orgs=filteredList;
        notifyDataSetChanged();
    }
}

