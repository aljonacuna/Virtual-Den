package com.example.loadingscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.model.roomList_model;
import com.example.loadingscreen.schedule.add_room_sched;

import java.util.ArrayList;

public class roomschedToupdateAdapter extends RecyclerView.Adapter<roomschedToupdateAdapter.ViewHolder> {
    private Context context;
    private ArrayList<roomList_model>roomList;

    public roomschedToupdateAdapter(Context context) {
        this.context = context;
        this.roomList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.toupateroomsched,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final roomList_model rooms = roomList.get(position);
        holder.textView.setText(rooms.getRoomname());
        holder.parentClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrfordd dialogs = new arrfordd();
                String title = "Edit schedule";
                String message = "Are you sure that this room ";
                String message1 = " need an update on schedule?";
                dialogs.dialog_conf(context, title, message, rooms.getRoomname(), message1
                        , new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String roomkey = rooms.getRoomkey();
                                Intent intent = new Intent(context, add_room_sched.class);
                                intent.putExtra("roomKey",roomkey);
                                context.startActivity(intent);
                                arrfordd.dialog.dismiss();
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RelativeLayout parentClick;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.roomname_TXT);
            parentClick = itemView.findViewById(R.id.parentRelroomschedupdt);
        }
    }
    public void filteredList(ArrayList<roomList_model>rooms){
        roomList = rooms;
    }
}
