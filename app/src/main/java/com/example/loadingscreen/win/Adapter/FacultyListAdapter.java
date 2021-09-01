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

public class FacultyListAdapter extends RecyclerView.Adapter<FacultyListAdapter.FacultyViewHolder> {

    ArrayList<usersList_model> faculties;
    Context context;

    public FacultyListAdapter(Context c, ArrayList<usersList_model> f) {
        this.faculties = f;
        this.context=c;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.facultylistdesign,parent,false);
        return new FacultyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        usersList_model facultiesPos = faculties.get(position);
        holder.profName.setText(facultiesPos.getFullname());
        holder.profAbout.setText(facultiesPos.getAbout());
        Picasso.with(context).load(facultiesPos.getProfileimg()).resize(40,40).
                transform(new CropCircleTransformation()).into(holder.profImageView);
    }

    @Override
    public int getItemCount() {
        return faculties.size();
    }

    public class FacultyViewHolder extends  RecyclerView.ViewHolder{
        ImageView profImageView;
        TextView profAbout,profName;
        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);

            profName=itemView.findViewById(R.id.profName);
            profAbout=itemView.findViewById(R.id.profAbout);
            profImageView=itemView.findViewById(R.id.profImage);
        }
    }

    public  void filterList(ArrayList<usersList_model> filteredList){
        faculties=filteredList;
        notifyDataSetChanged();
    }
}
