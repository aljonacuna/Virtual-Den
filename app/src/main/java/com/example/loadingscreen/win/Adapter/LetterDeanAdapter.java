package com.example.loadingscreen.win.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.curseWords;
import com.example.loadingscreen.activity_gui.userprofile;
import com.example.loadingscreen.win.activity.LetterforDean;
import com.example.loadingscreen.win.model.LetterDean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class LetterDeanAdapter extends RecyclerView.Adapter<LetterDeanAdapter.ViewHolder> {
    private Context context;
    private ArrayList<LetterDean>letterList;
    String strAsterisk="";
    public LetterDeanAdapter(Context context, ArrayList<LetterDean> letterList) {
        this.context = context;
        this.letterList = letterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.letterdean_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final LetterDean letterDean = letterList.get(position);
        String letter = letterDean.getLetter();
        curseWords cw = new curseWords();
        for (String cursewords : cw.getIllegalWords()){
            Pattern pattern = Pattern.compile("\\b"+cursewords+"\\b",Pattern.CASE_INSENSITIVE);
            letter = pattern.matcher(letter).replaceAll("****");
        }
        holder.letter.setText(letter);
        holder.datetime.setText(letterDean.getDateSubmit());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userstbl")
                .child(letterDean.getUserID());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userType = snapshot.child("userType").getValue().toString();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userType)
                                .child(letterDean.getUserID());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.dispname.setText(snapshot.child("fullname").getValue().toString());
                                Picasso.with(context).load(snapshot.child("profileimg").getValue().toString()).into(holder.userpic);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        reactionCount(holder.likebtn,holder.likecount,holder.likelbl,letterDean.getLetterID());
    }

    @Override
    public int getItemCount() {
        return letterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dispname,datetime,letter,likecount,likelbl;
        private ImageView userpic,likebtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userpic = itemView.findViewById(R.id.userPic);
            datetime = itemView.findViewById(R.id.dateAndTime);
            letter = itemView.findViewById(R.id.postContent1);
            dispname = itemView.findViewById(R.id.userDispName);
            likebtn = itemView.findViewById(R.id.likeBTN);
            likecount = itemView.findViewById(R.id.like_count);
            likelbl = itemView.findViewById(R.id.likeLabel);

            dispname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String dispnameTxt = dispname.getText().toString();
                    Intent intent = new Intent(context, userprofile.class);
                    intent.putExtra("displayname",dispnameTxt);
                    intent.putExtra("userid",letterList.get(pos).getUserID());
                    context.startActivity(intent);
                }
            });

            likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    final DatabaseReference reactionRef = FirebaseDatabase.getInstance().getReference("Like").child("letter");
                    String reactionstr = likelbl.getText().toString();
                    if (reactionstr.equals("Like")){
                        likelbl.setText("Liked");
                        reactionRef.child(letterList.get(pos).getLetterID())
                                .child(firebaseUser.getUid()).setValue("Yes");
                    }else{
                        reactionRef.child(letterList.get(pos).getLetterID())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
            });
        }

    }
    public void reactionCount(final ImageView likebtn, final TextView likecount,
                              final TextView likelbl, String letterID){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("Like").child("letter")
                .child(letterID);
        countRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    String count = String.valueOf(snapshot.getChildrenCount());
                    likebtn.setImageResource(R.drawable.ic_baseline_thumb_up);
                    if (snapshot.getChildrenCount()>1) {
                        likelbl.setText("Liked ");
                        likecount.setText("Likes "+count);
                    }else {
                        likelbl.setText("Liked ");
                        likecount.setText("Like "+count);
                    }
                }else{
                    String count = String.valueOf(snapshot.getChildrenCount());
                    likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    if (snapshot.getChildrenCount()>1) {
                        likelbl.setText("Like");
                        likecount.setText("Likes "+count);
                    }else{
                        likelbl.setText("Like");
                        likecount.setText("Like "+count);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
