package com.example.loadingscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.add_roomsched_model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class safinalnadelete extends RecyclerView.Adapter<safinalnadelete.ViewHolder> {
    private Context context;
    private List<add_roomsched_model>roomschedList;

    public safinalnadelete(Context context) {
        this.context = context;
        this.roomschedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_sched_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        add_roomsched_model roomscheds = roomschedList.get(position);
        Picasso.with(context).load(roomscheds.getImage()).placeholder(R.drawable.placeholder).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return roomschedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.room_sched_img);
        }
    }
    public void allAdd(List<add_roomsched_model> newroomSchedlist){
        int size = roomschedList.size();
        roomschedList.addAll(newroomSchedlist);
        notifyItemRangeChanged(size,newroomSchedlist.size());
    }
}
