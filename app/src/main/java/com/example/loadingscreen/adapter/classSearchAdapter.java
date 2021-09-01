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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.fullscreen_img.schedulefull_img;
import com.example.loadingscreen.model.class_sched_model;
import com.example.loadingscreen.schedule.dimuna_deleteeditclasssc;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.loadingscreen.Utils.arrfordd.dialog;

public class classSearchAdapter extends RecyclerView.Adapter<classSearchAdapter.ViewHolder> {
    private Context context;
    private List<class_sched_model> classSearchlist;
    private String tag;

    public classSearchAdapter(Context context, String tag) {
        this.context = context;
        this.classSearchlist = new ArrayList<>();
        this.tag = tag;
    }

    @NonNull
    @Override
    public classSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_sched_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final classSearchAdapter.ViewHolder holder, final int position) {
        final class_sched_model classScheds = classSearchlist.get(position);
        if (classScheds.getClassSched().equals("")){
            holder.classSched_img.setImageResource(R.drawable.noimage);
        }else {
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
        String [] str = classScheds.getDate().split("/");
        String noteText = classScheds.getNote();
        String labeltxt = str[0];
        String date = str[1];
        if (labeltxt.equals("dateposted")){
            holder.not_yetlbl.setText(date);
            holder.label.setText("Date Posted: ");
            holder.label.setVisibility(View.VISIBLE);
            holder.postedby.setVisibility(View.VISIBLE);
            holder.labelpostedby.setVisibility(View.VISIBLE);
            if (!noteText.equals("")){
                holder.note.setText(noteText);
            }

        }
        else if (labeltxt.equals("updated")){
            holder.not_yetlbl.setText(date);
            holder.not_yetlbl.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.label.setText("Last Updated: ");
            holder.label.setVisibility(View.VISIBLE);
            holder.postedby.setVisibility(View.VISIBLE);
            holder.labelpostedby.setVisibility(View.VISIBLE);
            if (!noteText.equals("")){
                holder.note.setText(noteText);
            }
        }

    }

    public void toEdit(final int position, final ImageView classSched_img, final TextView courseYSG) {
        arrfordd getdialog = new arrfordd();
        String title = "Edit schedule";
        String message = "Are you sure that ";
        String message1 = " is one of your advisory class?";
        final String course = classSearchlist.get(position).getCourse();
        final String ys = classSearchlist.get(position).getYearsection();
        final String group = classSearchlist.get(position).getGroup();
        getdialog.dialog_conf(context, title, message, course + " " + ys + " " + group
                , message1, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Pair[] pairs = new Pair[2];
                        pairs[0] = new Pair<View, String>(classSched_img, "imgpost_transition");
                        pairs[1] = new Pair<View, String>(courseYSG, "item_transition");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                            Intent intent = new Intent(context, dimuna_deleteeditclasssc.class);
                            intent.putExtra("course", course);
                            intent.putExtra("year", ys);
                            intent.putExtra("group", group);
                            intent.putExtra("imgurl", classSearchlist.get(position).getClassSched());
                            intent.putExtra("key", classSearchlist.get(position).getKey());
                            dialog.dismiss();
                            context.startActivity(intent, activityOptions.toBundle());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return classSearchlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseYSG, not_yetlbl, label, postedby, labelpostedby,note;
        private ImageView classSched_img;
        private MaterialCardView parentlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseYSG = itemView.findViewById(R.id.courseYSG);
            classSched_img = itemView.findViewById(R.id.class_schedImg);
            parentlayout = itemView.findViewById(R.id.parentlayout);
            not_yetlbl = itemView.findViewById(R.id.not_yetlbl);
            label = itemView.findViewById(R.id.label_date_class);
            postedby = itemView.findViewById(R.id.postedby);
            labelpostedby = itemView.findViewById(R.id.label_postedby);
            note = itemView.findViewById(R.id.noteClassSched);


        }


    }

    public void filteredList(List<class_sched_model> class_List) {
        classSearchlist = class_List;
    }

    public String getlastsearch() {
        return classSearchlist.get(classSearchlist.size() - 1).getYearsection();
    }
}
