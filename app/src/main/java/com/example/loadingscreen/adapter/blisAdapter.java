package com.example.loadingscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.example.loadingscreen.model.class_sched_model;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class blisAdapter extends RecyclerView.Adapter<blisAdapter.ViewHolder> {
    private Context context;
    private List<class_sched_model> blis_schedList;

    public blisAdapter(Context context) {
        this.context = context;
        this.blis_schedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_sched_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final class_sched_model classScheds = blis_schedList.get(position);
        holder.parentlayout.setVisibility(View.VISIBLE);
        holder.courseYSG.setText(classScheds.getCourse() + " " + classScheds.getYearsection() + " " + classScheds.getGroup());
        Picasso.with(context).load(classScheds.getClassSched()).placeholder(R.drawable.placeholder).into(holder.classSched_img);
        holder.classSched_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, schedulefull_img.class);
                intent.putExtra("imgschedclass", classScheds.getClassSched());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blis_schedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseYSG, not_yetlbl;
        private ImageView classSched_img, editbtn;
        private MaterialCardView parentlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseYSG = itemView.findViewById(R.id.courseYSG);
            classSched_img = itemView.findViewById(R.id.class_schedImg);
            editbtn = itemView.findViewById(R.id.editbtn_class_sched);
            parentlayout = itemView.findViewById(R.id.parentlayout);
            not_yetlbl = itemView.findViewById(R.id.not_yetlbl);
            editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }


    }

    public void addAllblis(List<class_sched_model> blisSched) {
        int size = blis_schedList.size();
        blis_schedList.addAll(blisSched);
        notifyItemRangeChanged(size, blisSched.size());
    }

    public String getLastId() {
        return blis_schedList.get(blis_schedList.size() - 1).getKey();
    }

    public void removelastitem() {
        blis_schedList.remove(blis_schedList.size() - 1);
    }
}
