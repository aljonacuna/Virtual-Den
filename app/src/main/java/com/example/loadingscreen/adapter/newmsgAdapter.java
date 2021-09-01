package com.example.loadingscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.chat;
import com.example.loadingscreen.model.usersList_model;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class newmsgAdapter extends RecyclerView.Adapter<newmsgAdapter.ViewHolder> {
    private Context context;
    private ArrayList<usersList_model> usersList;
    private String user_type;

    public newmsgAdapter(Context context,String user_type) {
        this.context = context;
        this.user_type = user_type;
        this.usersList = new ArrayList<>();
    }

    @NonNull
    @Override
    public newmsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final newmsgAdapter.ViewHolder holder, int position) {
        final usersList_model users = usersList.get(position);
        holder.userdispname.setText(users.getFullname());
        Picasso.with(context).load(users.getProfileimg()).into(holder.userimg);
        holder.usertype.setText(user_type);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chat.class);
                intent.putExtra("receiverid", users.getUserID());
                intent.putExtra("receivername", users.getFullname());
                intent.putExtra("receiverimg", users.getProfileimg());
                intent.putExtra("receiveruserType",user_type);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        private ImageView userimg;
        private TextView userdispname, usertype;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.userLayoutbtn);
            userimg = itemView.findViewById(R.id.search_img);
            userdispname = itemView.findViewById(R.id.search_name);
            usertype = itemView.findViewById(R.id.moredetailsLbl);
        }
    }

    public void filtered(ArrayList<usersList_model> newusers) {
        usersList = newusers;
    }
}
