package com.example.loadingscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.example.loadingscreen.model.usersList_model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class facultySearchAdapter extends RecyclerView.Adapter<facultySearchAdapter.ViewHolder> {
    private Context context;
    private List<usersList_model> searchFs;

    public facultySearchAdapter(Context context) {
        this.context = context;
        this.searchFs = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_sched_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final usersList_model schedsFs = searchFs.get(position);
        Picasso.with(context).load(schedsFs.getProfileimg()).into(holder.profimg);
        if (schedsFs.getSched().equals("")){
            holder.fs_sched.setImageResource(R.drawable.noimage);
        }else {
            Picasso.with(context).load(schedsFs.getSched()).placeholder(R.drawable.placeholder).into(holder.fs_sched);

            holder.fs_sched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, schedulefull_img.class);
                    intent.putExtra("imgfacultysched", schedsFs.getSched());
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.fs_sched, ViewCompat.getTransitionName(holder.fs_sched));
                    context.startActivity(intent, optionsCompat.toBundle());
                }
            });
        }
        if (!schedsFs.getSchedDate().equals("")){
            String[] str = schedsFs.getSchedDate().split("/");
            String lbl = str[0];
            String date1 = str[1];
            if (lbl.equals("update")) {
                holder.datelbl.setText("Date updated: "+date1);
            } else {
                holder.datelbl.setText("Date added: "+date1);
            }
        }else{
            holder.datelbl.setVisibility(View.GONE);
        }
        holder.note.setText(schedsFs.getSchedNote());
        holder.staffname.setText(schedsFs.getFullname());
    }

    @Override
    public int getItemCount() {
        return searchFs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView fs_sched,profimg;
        private TextView staffname,datelbl,note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fs_sched = itemView.findViewById(R.id.fs_schedimg);
            staffname = itemView.findViewById(R.id.staffname);
            datelbl = itemView.findViewById(R.id.datefacultyTV);
            note = itemView.findViewById(R.id.noteFS);
            profimg = itemView.findViewById(R.id.progimg);
        }
    }

    public void filtered(List<usersList_model> newSearchfs) {
        searchFs = newSearchfs;
        notifyDataSetChanged();
    }
}
