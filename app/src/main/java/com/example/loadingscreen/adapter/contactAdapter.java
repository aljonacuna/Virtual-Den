package com.example.loadingscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.chat;
import com.example.loadingscreen.model.chatList_model;
import com.example.loadingscreen.model.contact_list;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class contactAdapter extends RecyclerView.Adapter<contactAdapter.ViewHolder> {
    private ArrayList<contact_list> contactLists;
    private Context context;
    private String lastChatStr;
    private String lastChatTimeStr;
    private FirebaseUser firebaseUser;

    public contactAdapter(Context context, ArrayList<contact_list> contactLists) {
        this.context = context;
        this.contactLists = contactLists;
    }

    @NonNull
    @Override

    public contactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final contactAdapter.ViewHolder holder, int position) {
        final contact_list contact_Lists = contactLists.get(position);
        holder.dispname.setText(contact_Lists.getName());
        final String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";

        DatabaseReference getImgurl = FirebaseDatabase.getInstance().getReference("userstbl").child(contact_Lists.getId());
        getImgurl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = snapshot.child("userType").getValue().toString();
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference(userType).child(contact_Lists.getId());
                dref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String url = snapshot.child("profileimg").getValue().toString();
                        String status = snapshot.child("status").getValue().toString();
                        if (url.equals("none")) {
                            Picasso.with(context).load(noProfileimg).into(holder.contact_img);
                        } else {
                            Picasso.with(context).load(url).into(holder.contact_img);
                        }
                        //get online status
                        if (status.equals("online")) {
                            holder.onlineCard.setVisibility(View.VISIBLE);
                        } else {
                            holder.onlineCard.setVisibility(View.GONE);
                        }
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

        holder.lastChatDisplay(contact_Lists.getId(), holder.lastChatTxt, holder.lastChatTime);

    }

    @Override
    public int getItemCount() {
        return contactLists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dispname, lastChatTxt, lastChatTime;
        private ImageView contact_img;
        private CardView onlineCard;
        private RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dispname = itemView.findViewById(R.id.displayname);
            contact_img = itemView.findViewById(R.id.contact_img);
            lastChatTxt = itemView.findViewById(R.id.lastChatTxt);
            onlineCard = itemView.findViewById(R.id.onlineCard);
            lastChatTime = itemView.findViewById(R.id.lastChatTime);
            relativeLayout = itemView.findViewById(R.id.clickableLayout);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    getRecieverdata(position);

                }
            });
        }

        public void lastChatDisplay(final String chatmateId, final TextView lastChatTxt, final TextView lastChatTime) {
            final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy , hh:mm aaa");
            lastChatStr = "none";
            lastChatTimeStr = "none";
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final String currentUser = firebaseUser.getUid();
            final Query query = FirebaseDatabase.getInstance().getReference("Chat");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        chatList_model chat_class = dataSnapshot.getValue(chatList_model.class);
                        if (chat_class.getReceiverId().equals(chatmateId) && chat_class.getSenderId().equals(currentUser) ||
                                chat_class.getReceiverId().equals(currentUser) && chat_class.getSenderId().equals(chatmateId)) {
                            lastChatStr = chat_class.getChat();
                            lastChatTimeStr = chat_class.getTimeStamp();
                            Date date = new Date();
                            try {
                                date = format.parse(lastChatTimeStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String date_time = (String) DateUtils.getRelativeTimeSpanString(date.getTime(),
                                    Calendar.getInstance().getTimeInMillis(),
                                    DateUtils.MINUTE_IN_MILLIS);
                            lastChatTxt.setText(lastChatStr);
                            lastChatTime.setText(" * " + date_time);
                            if (chat_class.getSeen().equals("false") && chat_class.getReceiverId().equals(currentUser)) {
                                lastChatTxt.setTypeface(null, Typeface.BOLD);
                                lastChatTxt.setTextColor(ContextCompat.getColor(context, R.color.black));

                            } else {
                                lastChatTxt.setTypeface(null, Typeface.NORMAL);
                                lastChatTxt.setTextColor(ContextCompat.getColor(context, R.color.lightdark));
                            }
                            if (chat_class.getType().equals("image")) {
                                lastChatTxt.setText("Sent photo");
                            }

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    public void getRecieverdata(int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ContactList").
                child(contactLists.get(position).getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String key = snapshot.getKey();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userstbl").child(key);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String userType = snapshot.child("userType").getValue().toString();
                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference(userType).child(key);
                        dref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String fullname = snapshot.child("fullname").getValue().toString();
                                String profileimg = snapshot.child("profileimg").getValue().toString();
                                Intent intent = new Intent(context, chat.class);
                                intent.putExtra("receiverid", key);
                                intent.putExtra("receivername", fullname);
                                intent.putExtra("receiverimg", profileimg);
                                intent.putExtra("receiveruserType",userType);
                                context.startActivity(intent);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
