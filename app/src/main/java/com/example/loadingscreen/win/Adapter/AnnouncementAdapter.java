package com.example.loadingscreen.win.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.win.activity.FullScreenImage;
import com.example.loadingscreen.win.activity.FullVideoScreen;
import com.example.loadingscreen.win.model.Announcements;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.loadingscreen.Utils.sharedpref.orgRef;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Announcements> announcementsList;
    ShareDialog shareDialog;
    public AnnouncementAdapter(Context context, ArrayList<Announcements> announcementsList) {
        this.context = context;
        this.announcementsList = announcementsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.announce_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Announcements announcements = announcementsList.get(position);
        String orgKey = announcements.getOrgkey();
        final DatabaseReference announceRef1 = FirebaseDatabase.getInstance().getReference().child("Announcements");
        final DatabaseReference orgAnnounceRef1 = FirebaseDatabase.getInstance().getReference().child("OrgAnnounces").child(orgKey);
        final String content = announcements.getContent();
        final String announceFile = announcements.getAnnounceFile();
        String announceType = announcements.getType();
        final String postkey = announcements.getPostkey();
        Long announceTime = announcements.getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(announceTime);
        String date = DateFormat.format("EEE MMM d,yyyy", calendar).toString();
        final String times = DateFormat.format("h:mm:ss a", calendar).toString();
        holder.dateAndTime.setText(date + "  " + times);
        holder.postContent.setText(content);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(orgRef)
                .child(orgKey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img = snapshot.child("profileimg").getValue(String.class);
                String name = snapshot.child("fullname").getValue(String.class);
                Picasso.with(context).load(img).into(holder.orgPicture);
                holder.postOrgName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.imageButton.setVisibility(View.GONE);
        //end imagbtn

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenImage.class);
                intent.putExtra("image", announceFile);
                context.startActivity(intent);

            }
        });
        holder.postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullVideoScreen.class);
                intent.putExtra("video", announceFile);
                context.startActivity(intent);
            }
        });
        //readmore
        if (holder.postContent.length() > 150) {
            holder.postContent.setMaxLines(5);
            holder.readMore.setVisibility(View.VISIBLE);
            holder.readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.readMore.getText().toString().equalsIgnoreCase("Read more")) {
                        holder.postContent.setMaxLines(Integer.MAX_VALUE);
                        holder.readMore.setText("Show less");
                    } else {
                        holder.postContent.setMaxLines(5);
                        holder.readMore.setText("Read more");
                    }
                }
            });
        } else {
            holder.readMore.setVisibility(View.GONE);
        }//end readmore


        //hide and show img/video
        if (announceType.equals("image")) {
            holder.postImage.setVisibility(View.VISIBLE);
            holder.postVideo.setVisibility(View.GONE);
            Glide.with(context)
                    .load(announceFile)
                    .into(holder.postImage);
        } else if (announceType.equals("video")) {
            holder.postVideo.setVisibility(View.VISIBLE);
            Uri videouri = Uri.parse(announceFile);
            holder.postImage.setVisibility(View.GONE);
            holder.postVideo.setVideoURI(videouri);
            holder.postVideo.seekTo(1);

        } else {
            holder.postImage.setVisibility(View.GONE);
            holder.postVideo.setVisibility(View.GONE);

        } //end h
        reaction_count(holder.likebtn,holder.likelbl,holder.likecount,postkey);
    }

    @Override
    public int getItemCount() {
        return announcementsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        ImageView orgPicture, postImage,likebtn;
        VideoView postVideo;
        CardView sharelike;
        Button sharebtn;
        TextView postOrgName, dateAndTime, postContent, readMore,likecount,likelbl;

        CallbackManager callbackManager;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageAnnounceBtn);
            orgPicture = itemView.findViewById(R.id.orgPicture);
            sharelike = itemView.findViewById(R.id.shareAndLikeCard);
            postOrgName = itemView.findViewById(R.id.postOrgName);
            dateAndTime = itemView.findViewById(R.id.dateAndTime);
            postContent = itemView.findViewById(R.id.postContent);
            postImage = itemView.findViewById(R.id.postImage);
            postVideo = itemView.findViewById(R.id.postVideo);
            readMore = itemView.findViewById(R.id.readMore);
            likecount = itemView.findViewById(R.id.likecount);
            likelbl = itemView.findViewById(R.id.likelbl_announce);
            sharelike.setVisibility(View.VISIBLE);
            sharebtn = itemView.findViewById(R.id.btnShare);
            likebtn = itemView.findViewById(R.id.btnLike);
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog((Activity) context);
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    SharePhoto sharePhoto = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();
                    if (ShareDialog.canShow(SharePhotoContent.class)) {
                        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                                .addPhoto(sharePhoto)
                                .build();
                        shareDialog.show(sharePhotoContent);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String file = announcementsList.get(pos).getAnnounceFile();
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(context, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (announcementsList.get(pos).getType().equals("image")) {
                        Picasso.with(context).load(file).into(target);
                    }else{
                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                .setQuote("Video from CICT VD")
                                .setContentUrl(Uri.parse(file))
                                .build();
                        if (ShareDialog.canShow(ShareLinkContent.class)){
                            shareDialog.show(shareLinkContent);
                        }

                    }

                }
            });
            likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Like")
                            .child("announce");
                    String lbllike = likelbl.getText().toString();
                    if (!lbllike.equals("Liked")){
                        databaseReference.child(announcementsList.get(pos).getPostkey())
                                .child(firebaseUser.getUid())
                                .setValue("Yes");
                    }else{
                        databaseReference.child(announcementsList.get(pos).getPostkey())
                                .child(firebaseUser.getUid())
                                .removeValue();
                    }
                }
            });
        }


    }
    public void reaction_count(final ImageView likebtn,final TextView likelbl, final TextView likecount, String announceId){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like").child("announce")
                .child(announceId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    String count = String.valueOf(snapshot.getChildrenCount());
                    likebtn.setImageResource(R.drawable.ic_baseline_thumb_up);
                    if (snapshot.getChildrenCount()>1) {
                        likelbl.setText("Liked");
                        likelbl.setVisibility(View.GONE);
                        likecount.setText("Likes " + count);
                    }else {
                        likelbl.setText("Liked");
                        likelbl.setVisibility(View.GONE);
                        likecount.setText("Like " + count);
                    }

                }else{
                    String count = String.valueOf(snapshot.getChildrenCount());
                    likebtn.setImageResource(R.drawable.like_announce);
                    if (snapshot.getChildrenCount()>1) {
                        likelbl.setText("Likes");
                        likelbl.setVisibility(View.GONE);
                        likecount.setText("Likes " + count);
                    }else {
                        likelbl.setText("Likes");
                        likelbl.setVisibility(View.GONE);
                        likecount.setText("Like " + count);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
