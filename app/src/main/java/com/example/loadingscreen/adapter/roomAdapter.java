package com.example.loadingscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.example.loadingscreen.model.roomList_model;
import com.example.loadingscreen.schedule.roomsched;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class roomAdapter extends RecyclerView.Adapter<roomAdapter.ViewHolder> {
    private Context context;
    private ArrayList<roomList_model> roomList;

    public roomAdapter(Context context, ArrayList<roomList_model> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public roomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.roomlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull roomAdapter.ViewHolder holder, int position) {
        roomList_model rooms = roomList.get(position);
        holder.room_name.setText(rooms.getRoomname());
        holder.view_sched.setPaintFlags(holder.view_sched.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        if (rooms.getRoomname().length()>5){
            holder.room_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        }

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView room_name,view_sched;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            room_name = itemView.findViewById(R.id.roomname);
            view_sched = itemView.findViewById(R.id.view_schedule);
            view_sched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String roomkey = roomList.get(position).getRoomkey();
                    Intent intent = new Intent(context, roomsched.class);
                    intent.putExtra("roomkey",roomkey);
                    context.startActivity(intent);
                }
            });
        }
    }
}
