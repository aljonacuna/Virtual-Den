package com.example.loadingscreen.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.chat;
import com.example.loadingscreen.activity_gui.lostandfound;
import com.example.loadingscreen.fullscreen_img.lostandfound_IMG;
import com.example.loadingscreen.model.postList_model;
import com.example.loadingscreen.activity_gui.userprofile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<postList_model> post;
    private Context context;
    private String tag;

    public PostAdapter(Context context, String tag) {
        this.post = new ArrayList<>();
        this.context = context;
        this.tag = tag;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_post_row, parent, false);
        return new ViewHolder(view);
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final postList_model posts = post.get(position);
        if (posts.getStatus().equals("Found")) {
            holder.lostfoundmsgbtn.setText("Claim now");
        } else if (posts.getStatus().equals("Lost")) {
            holder.lostfoundmsgbtn.setText("Send help");
        }
        if (tag.equals("visit")) {
            holder.lostfoundmsgbtn.setVisibility(View.GONE);
        } else {
            holder.lostfoundmsgbtn.setVisibility(View.VISIBLE);
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.item_name.setText(posts.getItem());
        holder.description.setText(posts.getDesc());
        holder.user.setText(posts.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy , HH:mm:ss.SSS");
        final String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
        String datePost = posts.getDateTime();
        Date date = new Date();
        try {
            date = dateFormat.parse(datePost);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //toDisplay time ago
        String date_post = (String) DateUtils.getRelativeTimeSpanString(date.getTime(),
                Calendar.getInstance().getTimeInMillis(),
                DateUtils.MINUTE_IN_MILLIS);
        if (date_post.equals("0 minutes ago")) {
            holder.getTimeAgo.setText("Just now");
        } else {
            holder.getTimeAgo.setText(date_post);
        }
        //Post img
        if (posts.getPhoto().equals("none")) {
            holder.photo.setVisibility(View.GONE);
        } else {
            holder.photo.setVisibility(View.VISIBLE);
            Picasso.with(context).load(posts.getPhoto()).placeholder(R.drawable.placeholder).into(holder.photo);
        }
        if (firebaseUser != null) {
            if (posts.getUserID().equals(firebaseUser.getUid())) {
                holder.lostfoundmsgbtn.setVisibility(View.GONE);
            } else {
                holder.lostfoundmsgbtn.setVisibility(View.VISIBLE);
            }
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userstbl").child(posts.getUserID());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = snapshot.child("userType").getValue().toString();
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference(userType).child(posts.getUserID());
                dref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String url = snapshot.child("profileimg").getValue().toString();
                        if (url.equals("none")) {
                            Picasso.with(context).load(noProfileimg).into(holder.user_profileimg);
                        } else {
                            Picasso.with(context).load(url).into(holder.user_profileimg);
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
    }

    @Override
    public int getItemCount() {
        return post.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ReadMoreTextView description;
        private TextView comment_count, reaction_count, like_count, getTimeAgo, user, item_name;
        private ImageView photo, user_profileimg, reaction_btn, option_btn;
        private ImageButton commentbtn;
        private Button lostfoundmsgbtn;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.usernameText);
            description = itemView.findViewById(R.id.description);
            getTimeAgo = itemView.findViewById(R.id.lastChatTime);
            lostfoundmsgbtn = itemView.findViewById(R.id.lostfoundMsgbtn);
            user_profileimg = itemView.findViewById(R.id.user_profileIMG);
            photo = itemView.findViewById(R.id.photo);
            item_name = itemView.findViewById(R.id.claimitem_name);


            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String postimg = post.get(pos).getPhoto();
                    Intent intent = new Intent(context, lostandfound_IMG.class);
                    intent.putExtra("imgpost", postimg);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, photo,
                            ViewCompat.getTransitionName(photo));
                    context.startActivity(intent, options.toBundle());
                }
            });
            lostfoundmsgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.lostfounddialog);
                    Button sendmsg = dialog.findViewById(R.id.sendmsg_btn);
                    final EditText msgtxtbox = dialog.findViewById(R.id.msg_textbox);
                    ImageView closebtn = dialog.findViewById(R.id.close_btndialog);
                    TextView itemname = dialog.findViewById(R.id.item_namedialog);
                    TextView title = dialog.findViewById(R.id.titleDialog);
                    TextView message = dialog.findViewById(R.id.messageDialog);
                    itemname.setText(post.get(pos).getItem());
                    if (post.get(pos).getStatus().equals("Found")) {
                        title.setText("Claiming?");
                        message.setText("You can add your personal message to the author of this post");
                    } else if (post.get(pos).getStatus().equals("Lost")) {
                        title.setText("Returning?");
                        message.setText("You can add your personal message to the author of this post");
                    }
                    sendmsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String msg = msgtxtbox.getText().toString().trim();
                            if (TextUtils.isEmpty(msg)) {
                                Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                claim(msg);
                            }
                        }
                    });
                    closebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                    dialog.show();
                }
            });

            /**reaction_btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
            popUp_windown(view);
            }
            });*/

            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!tag.equals("visit")) {
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        int position = getAdapterPosition();
                        if (!firebaseUser.getUid().equals(post.get(position).getUserID())) {
                            Intent intent = new Intent(context, userprofile.class);
                            intent.putExtra("displayname", post.get(position).getName());
                            intent.putExtra("userid", post.get(position).getUserID());
                            context.startActivity(intent);
                        }
                    }
                }
            });

        }

        @SuppressLint("RestrictedApi")
        public void showPopupMenu(View view) {
            final int position = getAdapterPosition();
            MenuBuilder menuBuilder = new MenuBuilder(context);
            new SupportMenuInflater(context).inflate(R.menu.post_option_menu, menuBuilder);
            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.clear:
                            return true;
                    }
                    return false;
                }


                @Override
                public void onMenuModeChange(MenuBuilder menu) {

                }
            });
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, view);
            menuPopupHelper.setForceShowIcon(true);
            menuPopupHelper.show();
        }


        public void intent(String date, Intent intent) {
            int position = getAdapterPosition();
            String postkey = post.get(position).getPostKey();
            String status = post.get(position).getStatus();
            String item = post.get(position).getItem();
            String authorid = post.get(position).getUserID();
            String authorname = post.get(position).getName();
            String dateposted = post.get(position).getDateTime();
            String postimg = post.get(position).getPhoto();
            String desc = post.get(position).getDesc();


            if (date != null) {
                intent.putExtra("date", date);
            }
            intent.putExtra("status", status);
            intent.putExtra("postkey", postkey);
            intent.putExtra("item", item);
            intent.putExtra("id", authorid);
            intent.putExtra("name", authorname);
            intent.putExtra("dateposted", dateposted);
            intent.putExtra("postimg", postimg);
            intent.putExtra("desc", desc);
            context.startActivity(intent);
            ((lostandfound) context).finish();
        }


        public void claim(String msg) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context, chat.class);
            String item = post.get(position).getItem();
            intent.putExtra("receivername", post.get(position).getName());
            intent.putExtra("receiverid", post.get(position).getUserID());
            intent.putExtra("postkey", post.get(position).getPostKey());
            intent.putExtra("status", post.get(position).getStatus());
            intent.putExtra("msgclaim", "Link for the item " + item + " " + msg);
            context.startActivity(intent);
        }
    }

    public void addAll(ArrayList<postList_model> newFound) {
        int size = post.size();
        post.addAll(newFound);
        notifyItemRangeChanged(size, newFound.size());
    }

    public void filter(ArrayList<postList_model> filter) {
        post.addAll(filter);
    }

    public String getLastid() {
        return post.get(post.size() - 1).getPostKey();
    }

}
