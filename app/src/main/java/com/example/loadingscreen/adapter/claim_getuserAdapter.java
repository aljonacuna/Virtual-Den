package com.example.loadingscreen.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.activity_gui.itemclaim_2;
import com.example.loadingscreen.activity_gui.lostandfound;
import com.example.loadingscreen.activity_gui.userprofile;
import com.example.loadingscreen.fragment.lost;
import com.example.loadingscreen.model.claimList_model;
import com.example.loadingscreen.model.usersList_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.loadingscreen.Utils.arrfordd.dialog;
import static com.example.loadingscreen.Utils.lostfoundUtils.FoundCache;
import static com.example.loadingscreen.Utils.lostfoundUtils.LostCache;
import static com.example.loadingscreen.Utils.lostfoundUtils.claimCache;

public class claim_getuserAdapter extends RecyclerView.Adapter<claim_getuserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<usersList_model>claimerList;
    private String dateclaim;
    private String status;
    private String postkey;
    private String item;
    private String authorid;
    private String authorname;
    private String dateposted;
    private String postimg;
    public claim_getuserAdapter(Context context,String dateclaim,String postkey,String status,
                                String item,String authorid,String authorname,String dateposted,String postimg) {
        this.context = context;
        this.dateclaim = dateclaim;
        this.postkey = postkey;
        this.status = status;
        this.item = item;
        this.authorid = authorid;
        this.authorname = authorname;
        this.dateposted = dateposted;
        this.postimg = postimg;
        this.claimerList = new ArrayList<>();
    }

    @NonNull
    @Override
    public claim_getuserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull claim_getuserAdapter.ViewHolder holder, int position) {
        usersList_model claimer = claimerList.get(position);
        String claimerimg = claimer.getProfileimg();
        final String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
        if (claimerimg.equals("none")){
            Picasso.with(context).load(noProfileimg).into(holder.claimerimg);
        }
        else{
            Picasso.with(context).load(claimerimg).into(holder.claimerimg);
        }
        holder.claimername.setText(claimer.getFullname());

    }

    @Override
    public int getItemCount() {
        return claimerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView claimername,loc;
        private ImageView claimerimg;
        private RelativeLayout claimerLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            claimername = itemView.findViewById(R.id.search_name);
            loc = itemView.findViewById(R.id.moredetailsLbl);
            claimerimg = itemView.findViewById(R.id.search_img);
            claimerLayout = itemView.findViewById(R.id.userLayoutbtn);
            loc.setVisibility(View.GONE);
            claimerLayout.setOnClickListener(new View.OnClickListener() {
                String addmsg;
                @Override
                public void onClick(View view) {

                    if (status.equals("Found")){
                       addmsg = "claimed";
                    }
                    else if (status.equals("Lost")){
                        addmsg = "returned";
                    }
                    int position = getAdapterPosition();
                    final String name = claimerList.get(position).getFullname();
                    String titlestr = "Proceed on clearing?";
                    final String msg1 = "You've searched for this person";
                    String msg2 = ", Are you sure this is the person that " +addmsg+" the item?";
                    arrfordd dialog_show = new arrfordd();
                    dialog_show.dialog_conf(context, titlestr, msg1,name,msg2, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            clear(name);

                        }
                    });
                }
            });
        }
        public void clear(String name){
            if (dateclaim!=null) {
                int position = getAdapterPosition();
                final String timestamp = String.valueOf(9999999999999L + (-1 * new Date().getTime()));
                String claimerid = claimerList.get(position).getUserID();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child("Clear");
                claimList_model addClaim = new claimList_model(name, dateclaim, claimerid, status, item, authorid, timestamp, authorname, dateposted, postimg);
                reference.child(timestamp).setValue(addClaim);
                delete_data();
                Toast.makeText(context, "Item cleared successfully", Toast.LENGTH_LONG).show();
                claimCache.clear();
                FoundCache.clear();
                LostCache.clear();
                dateclaim = null;
            }else {
                Toast.makeText(context, "Item already cleared", Toast.LENGTH_LONG).show();
            }
        }
        public void delete_data(){
            DatabaseReference delref = FirebaseDatabase.getInstance().getReference("Post").child(status).child(postkey);
            delref.removeValue();
        }
    }
    public void filter(ArrayList<usersList_model> newclaimerList){
        claimerList = newclaimerList;
        notifyDataSetChanged();
    }
}
