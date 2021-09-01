package com.example.loadingscreen.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.example.loadingscreen.model.class_sched_model;
import com.example.loadingscreen.schedule.dimuna_deleteeditclasssc;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class classSchedAdapter extends RecyclerView.Adapter<classSchedAdapter.ViewHolder> {
    private Context context;
    private List<class_sched_model> class_schedList;

    public classSchedAdapter(Context context) {
        this.context = context;
        this.class_schedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_sched_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final class_sched_model classScheds = class_schedList.get(position);
        if (classScheds.getClassSched().equals("")) {
            holder.classSched_img.setImageResource(R.drawable.noimage);
        } else {
            Picasso.with(context).load(classScheds.getClassSched()).placeholder(R.drawable.placeholder).into(holder.classSched_img);
            holder.classSched_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, schedulefull_img.class);
                    intent.putExtra("imgschedclass", classScheds.getClassSched());
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.classSched_img, ViewCompat.getTransitionName(holder.classSched_img));
                    context.startActivity(intent, optionsCompat.toBundle());
                }
            });
        }
        holder.postedby.setText(classScheds.getPostedBy());
        holder.courseYSG.setText(classScheds.getCourse() + " " + classScheds.getYearsection() + " " + classScheds.getGroup());
        String[] str = classScheds.getDate().split("/");
        String labeltxt = str[0];
        String noteText = classScheds.getNote();
        String date = str[1];
        if (labeltxt.equals("dateposted")) {
            holder.not_yetlbl.setText(date);
            holder.label.setText("Date Posted: ");
            holder.label.setVisibility(View.VISIBLE);
            holder.postedby.setVisibility(View.VISIBLE);
            holder.labelpostedby.setVisibility(View.VISIBLE);
            holder.note.setText(noteText);

        } else if (labeltxt.equals("updated")) {
            holder.not_yetlbl.setText(date);
            holder.not_yetlbl.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.label.setText("Last Updated: ");
            holder.label.setVisibility(View.VISIBLE);
            holder.postedby.setVisibility(View.VISIBLE);
            holder.labelpostedby.setVisibility(View.VISIBLE);
            holder.note.setText(noteText);
        }

    }

    @Override
    public int getItemCount() {
        return class_schedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseYSG, not_yetlbl, label, postedby, labelpostedby, note;
        private ImageView classSched_img, editbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseYSG = itemView.findViewById(R.id.courseYSG);
            classSched_img = itemView.findViewById(R.id.class_schedImg);
            editbtn = itemView.findViewById(R.id.editbtn_class_sched);
            not_yetlbl = itemView.findViewById(R.id.not_yetlbl);
            label = itemView.findViewById(R.id.label_date_class);
            postedby = itemView.findViewById(R.id.postedby);
            labelpostedby = itemView.findViewById(R.id.label_postedby);
            note = itemView.findViewById(R.id.noteClassSched);
        }

        public void toEdit() {
            int pos = getAdapterPosition();
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(classSched_img, "imgpost_transition");
            pairs[1] = new Pair<View, String>(courseYSG, "item_transition");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                Intent intent = new Intent(context, dimuna_deleteeditclasssc.class);
                intent.putExtra("course", class_schedList.get(pos).getCourse());
                intent.putExtra("year", class_schedList.get(pos).getYearsection());
                intent.putExtra("group", class_schedList.get(pos).getGroup());
                intent.putExtra("imgurl", class_schedList.get(pos).getClassSched());
                intent.putExtra("key", class_schedList.get(pos).getKey());
                context.startActivity(intent, activityOptions.toBundle());
            }
        }
    }


    public void addAll(List<class_sched_model> classSched) {
        int size = class_schedList.size();
        class_schedList.addAll(classSched);
        notifyItemRangeChanged(size, classSched.size());
    }

    public String getLastId() {
        return class_schedList.get(class_schedList.size() - 1).getKey();
    }

    public void removelastitem() {
        class_schedList.remove(class_schedList.size() - 1);
    }

}
