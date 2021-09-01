package com.example.loadingscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.activity_gui.userprofile;
import com.example.loadingscreen.win.activity.StudentOrgView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.ViewHolder> {
    private Context context;
    private List<usersList_model> list_user;
    private FirebaseUser firebaseUser;
    private String usertype;

    public searchAdapter(Context context, List<usersList_model> list_user, String usertype) {
        this.context = context;
        this.list_user = list_user;
        this.usertype = usertype;
    }

    @NonNull
    @Override
    public searchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchAdapter.ViewHolder holder, int position) {
        usersList_model lists_user = list_user.get(position);
        holder.searchName.setText(lists_user.getFullname());
        final String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";

        if (lists_user.getProfileimg().equals("none")) {
            Picasso.with(context).load(noProfileimg).into(holder.searchImg);
        } else {
            Picasso.with(context).load(lists_user.getProfileimg()).into(holder.searchImg);
        }
        holder.moredetails.setText(lists_user.getAbout());

    }

    @Override
    public int getItemCount() {
        return list_user.size();
    }

    public void filteredList(ArrayList<usersList_model> userList, String text) {
        if (text.length() == 0) {
            list_user.clear();
        } else {
            list_user = userList;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView searchImg;
        private TextView searchName, moredetails;
        private RelativeLayout userLayoutbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchImg = itemView.findViewById(R.id.search_img);
            searchName = itemView.findViewById(R.id.search_name);
            userLayoutbtn = itemView.findViewById(R.id.userLayoutbtn);
            moredetails = itemView.findViewById(R.id.moredetailsLbl);
            userLayoutbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (usertype.equals("Organizations")) {
                        Intent intent = new Intent(context, StudentOrgView.class);
                        intent.putExtra("id",list_user.get(position).getUserID());
                        intent.putExtra("name",list_user.get(position).getFullname());
                        intent.putExtra("img",list_user.get(position).getProfileimg());
                        context.startActivity(intent);
                    } else {

                        Intent intent = new Intent(context, userprofile.class);
                        intent.putExtra("displayname", list_user.get(position).getFullname());
                        intent.putExtra("profileimage", list_user.get(position).getProfileimg());
                        intent.putExtra("userid", list_user.get(position).getUserID());
                        intent.putExtra("userType", usertype);
                        context.startActivity(intent);
                    }

                }
            });
        }
    }
}
