package com.example.loadingscreen.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.commentList_model;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder> {

    private ArrayList<commentList_model> comment_class;
    private Context context;

    public commentAdapter(Context context, ArrayList<commentList_model> comment_class) {
        this.comment_class = comment_class;
        this.context = context;
    }

    @NonNull
    @Override
    public commentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_row_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commentList_model comments = comment_class.get(position);
        holder.user_fullname.setText(comments.getName());
        holder.user_comment.setText(comments.getCommenttxt());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , HH:mm:ss.SSS");
        String date_comment = comments.getDateTime();
        Date date = new Date();
        try {
            date = dateFormat.parse(date_comment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateTime_comment = (String) DateUtils.getRelativeTimeSpanString(date.getTime(),
                Calendar.getInstance().getTimeInMillis(),
                DateUtils.MINUTE_IN_MILLIS);
        if (dateTime_comment.equals("0 minutes ago")) {
            holder.time_ago.setText("Just now");
        } else {
            holder.time_ago.setText(" * "+dateTime_comment);
        }

        Picasso.with(context).load(comments.getUser_profilephoto()).into(holder.user_img_profile);
    }

    @Override
    public int getItemCount() {
        return comment_class.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_img_profile;
        private TextView user_fullname, user_comment, time_ago;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_img_profile = itemView.findViewById(R.id.user_profileimg);
            user_fullname = itemView.findViewById(R.id.user_fullname);
            user_comment = itemView.findViewById(R.id.user_comment);
            time_ago = itemView.findViewById(R.id.timeAgolbl);
        }
    }
}
