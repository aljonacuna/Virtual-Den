package com.example.loadingscreen.win.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.win.activity.AddNewPost;
import com.example.loadingscreen.win.activity.FullScreenImage;
import com.example.loadingscreen.win.activity.FullVideoScreen;
import com.example.loadingscreen.win.activity.OrgAbout;
import com.example.loadingscreen.win.activity.OrgUserMenu;
import com.example.loadingscreen.win.activity.PostEdit;
import com.example.loadingscreen.win.activity.UpdateOrg;
import com.example.loadingscreen.win.model.Announcements;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.example.loadingscreen.win.fragment.HomeMenuUser.tag_granted;
import static com.example.loadingscreen.win.fragment.HomeMenuUser.tag_permission;

public class HomeOrgFragment extends Fragment {
    public HomeOrgFragment() {

    }

    private View homeView;
    private FloatingActionButton addPost;
    private RecyclerView postRecyclerView;
    ImageView orgImageTop;
    TextView orgDescTop, orgNameTop, userTypeTop, orgAboutBtn, orgUpdateBtn, fullScreen, fullScreenExit;
    DatabaseReference announceRef, orgAnnounceRef, orgRef, announceRef1, orgAnnounceRef1, userRef;
    FirebaseAuth myAuth;
    CardView topOrgDetails;
    String currentOrg;
    ValueEventListener valueEventListenerUserRef;
    ValueEventListener valueEventListener1;
    LinearLayoutManager announceLayoutManager;
    TextView postCount;
    String tag = "", id = "";
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home_org, container, false);
        postRecyclerView = (RecyclerView) homeView.findViewById(R.id.recyclerPosts);
        postCount = homeView.findViewById(R.id.postCount);
        fullScreen = homeView.findViewById(R.id.fullScreen);
        fullScreenExit = homeView.findViewById(R.id.fullScreenExit);
        topOrgDetails = homeView.findViewById(R.id.topOrgDetails);
        orgImageTop = homeView.findViewById(R.id.orgImage);
        orgDescTop = homeView.findViewById(R.id.orgDesc);
        orgNameTop = homeView.findViewById(R.id.orgName);
        userTypeTop = homeView.findViewById(R.id.userType);
        orgAboutBtn = homeView.findViewById(R.id.orgAboutBtn);
        orgUpdateBtn = homeView.findViewById(R.id.orgUpdateBtn);
        addPost = homeView.findViewById(R.id.orgPostAdd);
        postCount.getPaint().setUnderlineText(true);
        announceLayoutManager = new LinearLayoutManager(getContext());
        announceLayoutManager.setReverseLayout(true);
        announceLayoutManager.setStackFromEnd(true);
        postRecyclerView.setLayoutManager(announceLayoutManager);
        myAuth = FirebaseAuth.getInstance();
        onlineStatus("Organizations");
        postCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrgUserMenu.class);
                startActivity(intent);

            }
        });


        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topOrgDetails.setVisibility(View.GONE);
                fullScreen.setVisibility(View.GONE);
                fullScreenExit.setVisibility(View.VISIBLE);
            }
        });

        fullScreenExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topOrgDetails.setVisibility(View.VISIBLE);
                fullScreenExit.setVisibility(View.GONE);
                fullScreen.setVisibility(View.VISIBLE);
            }
        });

        orgUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateOrg.class);
                startActivity(intent);


            }
        });

        orgAboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag.equals("")) {
                    Intent intent = new Intent(getActivity(), OrgAbout.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), OrgAbout.class);
                    intent.putExtra("taghide", tag);
                    startActivity(intent);
                }
            }
        });


        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddNewPost.class);
                startActivity(intent);

            }
        });
        return homeView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tag_granted, "granted");
            editor.commit();
            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tag_permission, "deny");
            editor.commit();
            Toast.makeText(getContext(), "External storage permission required", Toast.LENGTH_SHORT).show();
        }
    }

    public void onlineStatus(String usertype) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , hh:mm aaa");
        final String dateTime = dateFormat.format(Calendar.getInstance().getTime());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(usertype).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference.child("status").setValue("online");
                    reference.child("status").onDisconnect().setValue(dateTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        currentOrg = myAuth.getCurrentUser().getUid();
        announceRef = FirebaseDatabase.getInstance().getReference().child("Announcements");
        announceRef1 = FirebaseDatabase.getInstance().getReference().child("Announcements");

        orgAnnounceRef1 = FirebaseDatabase.getInstance().getReference().child("OrgAnnounces").child(currentOrg);
        userRef = FirebaseDatabase.getInstance().getReference().child("Organizations").child(currentOrg);
        orgAnnounceRef = FirebaseDatabase.getInstance().getReference().child("OrgAnnounces").child(currentOrg);
        if (sharedPreferences != null && sharedPreferences.contains(tag_permission)) {

        } else {
            permissionUtils.isPermissionGranted(getActivity());
        }
        valueEventListenerUserRef = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sharedpref.spUsertype(getContext());
                    String orgDesc = snapshot.child("about").getValue().toString();
                    String orgName = snapshot.child("fullname").getValue().toString();
                    String orgImage = snapshot.child("profileimg").getValue().toString();
                    userTypeTop.setText(sharedpref.spUsertype(getContext()));
                    orgDescTop.setText(orgDesc);
                    orgNameTop.setText(orgName);
                    Picasso.with(getContext()).load(orgImage).resize(150, 150).
                            transform(new CropCircleTransformation()).into(orgImageTop);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Announcements>().
                setQuery(orgAnnounceRef, Announcements.class).build();
        FirebaseRecyclerAdapter<Announcements, announceViewHolder> adapter =
                new FirebaseRecyclerAdapter<Announcements, announceViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final announceViewHolder holder, int position, @NonNull Announcements model) {
                        final String announceIds = getRef(position).getKey();
                        valueEventListenerUserRef = userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    final String orgName = snapshot.child("fullname").getValue().toString();
                                    final String orgImage = snapshot.child("profileimg").getValue().toString();

                                    announceRef.child(announceIds).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                final String content = snapshot.child("content").getValue().toString().trim();
                                                final String announceFile = snapshot.child("announceFile").getValue().toString().trim();
                                                final String announceType = snapshot.child("type").getValue().toString().trim();
                                                String announceTime = snapshot.child("timestamp").getValue().toString().trim();
                                                Long time = Long.parseLong(announceTime);
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTimeInMillis(time);
                                                String date = DateFormat.format("EEE MMM d,yyyy", calendar).toString();
                                                String times = DateFormat.format("h:mm:ss a", calendar).toString();

                                                Picasso.with(getContext()).load(orgImage).resize(50, 50)
                                                        .transform(new CropCircleTransformation()).into(holder.orgPicture);
                                                holder.postOrgName.setText(orgName);
                                                holder.dateAndTime.setText(date + "  " + times);
                                                holder.postContent.setText(content);
                                                Bundle argument = getArguments();
                                                if (argument == null) {
                                                    holder.imageButton.setVisibility(View.VISIBLE);
                                                } else {
                                                    holder.imageButton.setVisibility(View.GONE);
                                                }



                                                holder.imageButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                                                        popupMenu.inflate(R.menu.announceitems);
                                                        popupMenu.show();

                                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                            @Override
                                                            public boolean onMenuItemClick(MenuItem item) {
                                                                switch (item.getItemId()) {
                                                                    case R.id.removePost:
                                                                        removeAnnounce();
                                                                        break;
                                                                    case R.id.editPost:
                                                                        editAnnounce();
                                                                        break;
                                                                    default:
                                                                        return false;
                                                                }
                                                                return true;
                                                            }

                                                            private void editAnnounce() {
                                                                Intent intent = new Intent(getActivity(), PostEdit.class);
                                                                intent.putExtra("postId", announceIds);
                                                                intent.putExtra("content", content);
                                                                startActivity(intent);

                                                            }

                                                            private void removeAnnounce() {
                                                                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getActivity());
                                                                alertDialogBuilder1.setMessage("Sure to remove post?");

                                                                alertDialogBuilder1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Toast.makeText(getActivity(), "Remove cancelled!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                alertDialogBuilder1.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        announceRef1.child(announceIds).removeValue();
                                                                        orgAnnounceRef1.child(announceIds).child("Posted").removeValue();
                                                                        Toast.makeText(getActivity(), "post removed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                AlertDialog alertDialog1 = alertDialogBuilder1.create();
                                                                alertDialog1.show();


                                                            }

                                                        });

                                                    }

                                                });

                                                //end imagbtn

                                                holder.postImage.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent intent = new Intent(getActivity(), FullScreenImage.class);
                                                        intent.putExtra("image", announceFile);
                                                        startActivity(intent);

                                                    }
                                                });
                                                holder.postVideo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent intent = new Intent(getActivity(), FullVideoScreen.class);
                                                        intent.putExtra("video", announceFile);
                                                        startActivity(intent);
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
                                                    Glide.with(getContext())
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

                                                } //end hide and show img/video
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        DatabaseReference reactRef = FirebaseDatabase.getInstance().getReference("Like").child("announce")
                                .child(announceIds);
                        reactRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String count = String.valueOf(snapshot.getChildrenCount());
                                if (snapshot.getChildrenCount() > 1) {
                                    holder.likelbl.setText("Liked");
                                    holder.likecount.setText("Likes " + count);
                                } else {
                                    holder.likelbl.setText("Liked");
                                    holder.likecount.setText("Like " + count);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public announceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announce_display, parent, false);
                        return new announceViewHolder(view);
                    }
                };

        orgAnnounceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long postCount1 = snapshot.getChildrenCount();
                    String count = Long.toString(postCount1);
                    if (count.equals("1")) {
                        postCount.setText("This organization has " + count + " post.");
                    } else {
                        postCount.setText("This organization has " + count + " posts.");
                    }
                } else {
                    postCount.setText("This organization have not post yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.notifyDataSetChanged();
        postRecyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUserRef);
    }

    @Override
    public void onPause() {
        super.onPause();
        userRef.removeEventListener(valueEventListenerUserRef);
    }

    public static class announceViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        ImageView orgPicture, postImage,likebtn;
        VideoView postVideo;
        TextView postOrgName, dateAndTime, postContent, readMore, likecount, likelbl;

        public announceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageAnnounceBtn);
            orgPicture = itemView.findViewById(R.id.orgPicture);
            postOrgName = itemView.findViewById(R.id.postOrgName);
            dateAndTime = itemView.findViewById(R.id.dateAndTime);
            postContent = itemView.findViewById(R.id.postContent);
            postImage = itemView.findViewById(R.id.postImage);
            postVideo = itemView.findViewById(R.id.postVideo);
            readMore = itemView.findViewById(R.id.readMore);
            likelbl = itemView.findViewById(R.id.likelbl_announce);
            likecount = itemView.findViewById(R.id.likecount);
        }
    }
}