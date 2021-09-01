package com.example.loadingscreen.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.clearUtils;
import com.example.loadingscreen.activity_gui.userpostdetail;
import com.example.loadingscreen.model.postList_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userprofileAdapter extends RecyclerView.Adapter<userprofileAdapter.ViewHolder> {
    private Context context;
    private ArrayList<postList_model> postList;
    private FirebaseUser firebaseUser;
    private String tag;
    public userprofileAdapter(Context context, ArrayList<postList_model> postList,String tag) {
        this.context = context;
        this.postList = postList;
        this.tag = tag;

    }

    @NonNull
    @Override
    public userprofileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_postlostandfound, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userprofileAdapter.ViewHolder holder, int position) {
        postList_model posts = postList.get(position);
        int length = 30;
        String desc = posts.getDesc();
        //labas see more
        if (posts.getDesc().length() >= length) {
            desc = desc.substring(0, length) + "<font color=\"#939393\"> .....See more </font>";
            holder.desc_user.setText(Html.fromHtml(desc));
        } else {
            holder.desc_user.setText(posts.getDesc() + "");
        }
        //pag walang image post adjust margin ng textview
        holder.itemname.setText(posts.getItem());
        if (posts.getPhoto().equals("none")) {
            holder.imgPost.setVisibility(View.GONE);
            setMargins(holder.desc_user, 50, 100, 50, 100);
            setMargins(holder.itemname, 45, 45, 45, 45);
        } else {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.with(context).load(posts.getPhoto()).placeholder(R.drawable.placeholder).into(holder.imgPost);
            int length1 = 25;
            if (posts.getDesc().length() >= length1) {
                desc = desc.substring(0, length1) + "<font color=\"#939393\"> .....See more </font>";
                holder.desc_user.setText(Html.fromHtml(desc));
            } else {
                holder.desc_user.setText(posts.getDesc() + "");
            }
        }
        //pag di yung currentuser yung visit na profile wala button
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!firebaseUser.getUid().equals(posts.getUserID())) {
            holder.clearbtn.setVisibility(View.GONE);
        }
    }

    public void setMargins(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button editbtn, clearbtn;
        private TextView desc_user, itemname;
        private ImageView imgPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desc_user = itemView.findViewById(R.id.desc);
            imgPost = itemView.findViewById(R.id.lost_foundImg);
            clearbtn = itemView.findViewById(R.id.clearbtn);
            itemname = itemView.findViewById(R.id.item_name_user);
            imgPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String imgurl = postList.get(pos).getPhoto();
                    if (imgurl.equals("none")) {

                    } else {
                        toDetail();
                    }
                }
            });
            clearbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    clearUtils.clear(postList.get(pos).getStatus(),postList.get(pos).getPostKey(),context,tag);

                }
            });
            desc_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String img = postList.get(pos).getPhoto();
                    if (img.equals("none")) {
                        toDetailnoImg();
                    } else {
                        toDetail();
                    }
                }
            });
        }

        /**
         * @SuppressLint("RestrictedApi") public void showPopupMenu(View view){
         * final int position = getAdapterPosition();
         * MenuBuilder menuBuilder = new MenuBuilder(context);
         * new SupportMenuInflater(context).inflate(R.menu.post_option_menu,menuBuilder);
         * menuBuilder.setCallback(new MenuBuilder.Callback() {
         * @Override public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
         * switch (item.getItemId()){
         * case R.id.edit:
         * notifyItemChanged(position);
         * return true;
         * case R.id.clear:
         * clear();
         * notifyItemChanged(position);
         * return true;
         * }
         * return false;
         * }
         * @Override public void onMenuModeChange(MenuBuilder menu) {
         * <p>
         * }
         * });
         * MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context,menuBuilder,view);
         * menuPopupHelper.setForceShowIcon(true);
         * menuPopupHelper.show();
         * <p>
         * }
         */
        public void toDetail() {
            Intent intent = new Intent(context, userpostdetail.class);
            int position = getAdapterPosition();
            Pair[] pairs = new Pair[3];
            pairs[0] = new Pair<View, String>(imgPost, "imgpost_transition");
            pairs[1] = new Pair<View, String>(itemname, "item_transition");
            pairs[2] = new Pair<View, String>(desc_user, "desc_transition");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                intent.putExtra("caption", postList.get(position).getDesc());
                intent.putExtra("item", postList.get(position).getItem());
                intent.putExtra("imgurl", postList.get(position).getPhoto());
                intent.putExtra("postkey", postList.get(position).getPostKey());
                intent.putExtra("status", postList.get(position).getStatus());
                intent.putExtra("name", postList.get(position).getName());
                intent.putExtra("idprofile",postList.get(position).getUserID());
                context.startActivity(intent, options.toBundle());
            }
        }

        public void toDetailnoImg() {
            Intent intent = new Intent(context, userpostdetail.class);
            int position = getAdapterPosition();
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(itemname, "item_transition");
            pairs[1] = new Pair<View, String>(desc_user, "desc_transition");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                intent.putExtra("caption", postList.get(position).getDesc());
                intent.putExtra("item", postList.get(position).getItem());
                intent.putExtra("imgurl", postList.get(position).getPhoto());
                intent.putExtra("postkey", postList.get(position).getPostKey());
                intent.putExtra("status", postList.get(position).getStatus());
                intent.putExtra("idprofile",postList.get(position).getUserID());
                context.startActivity(intent, options.toBundle());
            }
        }

    }

    public void addAll(ArrayList<postList_model> newPost) {
        int size = postList.size();
        postList.addAll(newPost);
        notifyItemRangeChanged(size, newPost.size());
    }

    public String getLastId() {
        return postList.get(postList.size() - 1).getPostKey();
    }

}
