package com.example.loadingscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.directtopost;
import com.example.loadingscreen.activity_gui.userpostdetail;
import com.example.loadingscreen.fullscreen_img.chat_userimg_fullscreen;
import com.example.loadingscreen.model.chatList_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {
    private ArrayList<chatList_model> chatList;
    private Context context;
    private final static int chat_left = 0;
    private final static int chat_right = 1;
    private FirebaseUser firebaseUser;
    String receiverimgUrl;
    String postkey;
    String post_status;

    public chatAdapter(Context context, ArrayList<chatList_model> chatList, String receiverimgUrl, String postkey, String post_status) {
        this.context = context;
        this.chatList = chatList;
        this.receiverimgUrl = receiverimgUrl;
        this.postkey = postkey;
        this.post_status = post_status;
    }

    @NonNull
    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == chat_right) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_row, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_row, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String currentUser = firebaseUser.getUid();
        final String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
        if (chatList.get(position).getSenderId().equals(currentUser)) {
            //if text ang chat
            if (chatList.get(position).getType().equals("text")) {
                holder.rightmsg.setVisibility(View.VISIBLE);
                holder.rightmsg.setText(chatList.get(position).getChat());
                holder.right_img.setVisibility(View.GONE);
            }
            //if image ang chat
            else if (chatList.get(position).getType().equals("image")) {
                holder.right_img.setVisibility(View.VISIBLE);
                Picasso.with(context).load(chatList.get(position).getChat()).placeholder(R.drawable.placeholder).
                        into(holder.right_img);
                holder.rightmsg.setVisibility(View.GONE);
                holder.right_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String imgurl = chatList.get(position).getChat();
                        Intent intent = new Intent(context, chat_userimg_fullscreen.class);
                        intent.putExtra("chatimg", imgurl);
                        context.startActivity(intent);
                    }
                });
            } else if (chatList.get(position).getType().equals("link")) {
                holder.rightmsg.setVisibility(View.VISIBLE);
                holder.rightmsg.setText(chatList.get(position).getChat());
                holder.right_img.setVisibility(View.GONE);
                holder.rightmsg.setPaintFlags(holder.rightmsg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.rightmsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = chatList.get(position).getLinkkey();
                        checkExist(link, holder.rightmsg,chatList.get(position).getSenderId());

                    }
                });
            }
            holder.timeright.setText(chatList.get(position).getTimeStamp());
        } else {
            //if text ang chat
            if (chatList.get(position).getType().equals("text")) {
                holder.leftmsg.setVisibility(View.VISIBLE);
                holder.leftmsg.setText(chatList.get(position).getChat());
                holder.left_img.setVisibility(View.GONE);
            }
            //if image ang chat
            else if (chatList.get(position).getType().equals("image")) {
                holder.left_img.setVisibility(View.VISIBLE);
                Picasso.with(context).load(chatList.get(position).getChat()).placeholder(R.drawable.placeholder).
                        into(holder.left_img);
                holder.leftmsg.setVisibility(View.GONE);
                holder.left_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String imgurl = chatList.get(position).getChat();
                        Intent intent = new Intent(context, chat_userimg_fullscreen.class);
                        intent.putExtra("chatimg", imgurl);
                        context.startActivity(intent);
                    }
                });
            } else if (chatList.get(position).getType().equals("link")) {
                holder.leftmsg.setVisibility(View.VISIBLE);
                holder.leftmsg.setText(chatList.get(position).getChat());
                holder.left_img.setVisibility(View.GONE);
                holder.leftmsg.setPaintFlags(holder.leftmsg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.leftmsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = chatList.get(position).getLinkkey();
                        checkExist(link, holder.leftmsg,chatList.get(position).getReceiverId());
                    }
                });
            }

            //if walang profile image
            try {
                if (receiverimgUrl.equals("none")) {
                    Picasso.with(context).load(noProfileimg).into(holder.messagemate);
                }
                //if merong profile image
                else {
                    Picasso.with(context).load(receiverimgUrl).into(holder.messagemate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.timeleft.setText(chatList.get(position).getTimeStamp());
        }

        //kunin sa last position ng array list
        if (position == chatList.size() - 1) {
            if (chatList.get(position).getSeen().equals("true")) {
                holder.seenstatus.setText("Seen");
            } else {
                holder.seenstatus.setText("Delivered");
            }
        } else {
            holder.seenstatus.setVisibility(View.GONE);
        }
    }

    public void checkExist(final String linkkey, final TextView textView, final String userId) {
        String[] str = linkkey.split("/");
        final String id = str[0];
        final String status = str[1];
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post").child(status).child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String key = snapshot.child("postKey").getValue().toString();
                    Intent intent = new Intent(context, userpostdetail.class);
                    intent.putExtra("postkey", id);
                    intent.putExtra("id",userId);
                    intent.putExtra("status", status);
                    intent.putExtra("tag","msg");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Error: This item was already cleared", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rightmsg, leftmsg, seenstatus, timeright, timeleft;
        private ImageView messagemate, right_img, left_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rightmsg = itemView.findViewById(R.id.right_msg);
            leftmsg = itemView.findViewById(R.id.left_msg);
            seenstatus = itemView.findViewById(R.id.seenStatus);
            messagemate = itemView.findViewById(R.id.messagemate_img);
            timeleft = itemView.findViewById(R.id.chatTimeLeft);
            timeright = itemView.findViewById(R.id.chatTimeRight);
            right_img = itemView.findViewById(R.id.right_msgImg);
            left_img = itemView.findViewById(R.id.left_msgImg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUser = firebaseUser.getUid();
        if (currentUser.equals(chatList.get(position).getSenderId())) {
            return chat_right;
        } else {
            return chat_left;
        }
    }
}
