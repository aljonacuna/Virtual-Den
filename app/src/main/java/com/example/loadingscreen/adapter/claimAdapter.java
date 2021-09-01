package com.example.loadingscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.example.loadingscreen.activity_gui.userprofile;
import com.example.loadingscreen.fullscreen_img.lostandfound_IMG;
import com.example.loadingscreen.model.claimList_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class claimAdapter extends RecyclerView.Adapter<claimAdapter.ViewHolder> {
    private Context context;
    private ArrayList<claimList_model> claimList;
    private String tag;
    public claimAdapter(Context context,String tag) {
        this.context = context;
        this.tag = tag;
        this.claimList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.claim_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        claimList_model claims = claimList.get(position);
        holder.claim_itemnameTv.setText(claims.getItem());
        holder.date_claimTv.setText(claims.getDateclaim());
        holder.claim_nameTv.setText(claims.getClaimername());
        holder.claim_nameTv.setPaintFlags(holder.claim_nameTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        String status = claims.getStatus();
        if (status.equals("Found")) {
            holder.statuslbl.setText("Claimed");
            holder.lbluser.setText("Claimer name:");
        } else if (status.equals("Lost")) {
            holder.statuslbl.setText("Returned");
            holder.lbluser.setText("Returned By:");
        }
        holder.author_postname.setText(claims.getAuthorname());
        String imgpostUrl = claims.getPostimg();
        if (imgpostUrl.equals("none")) {
            holder.claim_img.setImageResource(R.drawable.noimage);
        } else {
           Picasso.with(context).load(imgpostUrl).placeholder(R.drawable.placeholder).into(holder.claim_img);
        }
    }

    @Override
    public int getItemCount() {
        return claimList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView claim_nameTv, claim_itemnameTv, date_claimTv, author_postname, statuslbl, lbluser;
        private ImageView claim_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            claim_img = itemView.findViewById(R.id.claim_img);
            claim_nameTv = itemView.findViewById(R.id.claimer_nameTv);
            claim_itemnameTv = itemView.findViewById(R.id.claimitem_name);
            date_claimTv = itemView.findViewById(R.id.date_claimTv);
            author_postname = itemView.findViewById(R.id.author_postname);
            statuslbl = itemView.findViewById(R.id.statuslbl);
            lbluser = itemView.findViewById(R.id.lbluser);

            claim_nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tag.equals("")) {
                        final int position = getAdapterPosition();
                        final String id = claimList.get(position).getClaimerId();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userstbl").child(id);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userType = snapshot.child("userType").getValue().toString();
                                Intent intent = new Intent(context, userprofile.class);
                                intent.putExtra("displayname", claimList.get(position).getClaimername());
                                intent.putExtra("userid", claimList.get(position).getClaimerId());
                                intent.putExtra("userType", userType);
                                context.startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else{
                        //do nothing
                    }
                }
            });
            claim_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String imgurl = claimList.get(pos).getPostimg();
                    if (imgurl.equals("none")) {

                    } else {
                        Intent intent = new Intent(context, lostandfound_IMG.class);
                        intent.putExtra("imgpost", imgurl);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, claim_img,
                                ViewCompat.getTransitionName(claim_img));
                        context.startActivity(intent, optionsCompat.toBundle());
                    }
                }
            });
        }
    }

    public void addAll(ArrayList<claimList_model> newClaimlist,String tag) {
        if (tag!=null && tag.equals("refresh")){
            claimList.clear();
        }else {

        }
        int size = claimList.size();
        claimList.addAll(newClaimlist);
        notifyItemRangeChanged(size, newClaimlist.size());
    }

    public String getlastid() {
        return claimList.get(claimList.size() - 1).getKey();
    }
}
