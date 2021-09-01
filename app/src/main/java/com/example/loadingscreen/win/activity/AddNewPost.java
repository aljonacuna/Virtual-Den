package com.example.loadingscreen.win.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.win.model.Announcements;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.Date;

import static android.content.Intent.EXTRA_MIME_TYPES;

public class AddNewPost extends AppCompatActivity implements View.OnClickListener {

    Uri imguri, videouri;
    ImageView imageView, photoOrVideo, removeimgbtn;
    VideoView videoView;
    Button postBtn;
    DatabaseReference announceRef, orgAnnounces;
    StorageReference announceStorage;
    EditText editAnnounce;
    FirebaseAuth myAuth;
    String currentOrg;
    TextView snackbarView, addimgorvideolbl;

    private static final int CHOOSE_FILE_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        imageView = findViewById(R.id.imageViewPost);
        videoView = findViewById(R.id.videoViewPost);
        postBtn = findViewById(R.id.postBtn);
        editAnnounce = findViewById(R.id.editAnnounce);
        removeimgbtn = findViewById(R.id.removeimgbtn);
        myAuth = FirebaseAuth.getInstance();
        currentOrg = myAuth.getCurrentUser().getUid();
        announceRef = FirebaseDatabase.getInstance().getReference().child("Announcements");
        orgAnnounces = FirebaseDatabase.getInstance().getReference().child("OrgAnnounces");
        announceStorage = FirebaseStorage.getInstance().getReference().child("uploads");
        addimgorvideolbl = findViewById(R.id.addimgorvideolbl);
        photoOrVideo = findViewById(R.id.photoOrVideo);
        removeimgbtn.setOnClickListener(this);
        photoOrVideo.setOnClickListener(this);
        postBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view == photoOrVideo){
            addImageoRVideo();
        }else if (view == removeimgbtn){
            if (imguri!=null){
                photoOrVideo.setVisibility(View.VISIBLE);
                addimgorvideolbl.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                removeimgbtn.setVisibility(View.GONE);
                imguri = null;
            }else if (videouri !=null){
                photoOrVideo.setVisibility(View.VISIBLE);
                addimgorvideolbl.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                removeimgbtn.setVisibility(View.GONE);
                videouri = null;
            }
        }else if (view == postBtn){
            addAnnounce();
        }
    }
    //intent gallery
    private void addImageoRVideo() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] extraMimeTypes = {"image/*", "video/*"};
        intent.putExtra(EXTRA_MIME_TYPES, extraMimeTypes);
        startActivityForResult(intent, CHOOSE_FILE_REQUEST);
    }//end intent gallery

    //start activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FILE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            photoOrVideo.setVisibility(View.GONE);
            addimgorvideolbl.setVisibility(View.GONE);
            removeimgbtn.setVisibility(View.VISIBLE);
            Uri selectedMedia = data.getData();
            ContentResolver cr = getContentResolver();
            String mime = cr.getType(selectedMedia);
            if (mime.toLowerCase().contains("image")) {
                imguri = data.getData();
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                Picasso.with(this).load(imguri).into(imageView);

            } else if (mime.toLowerCase().contains("video")) {
                videouri = data.getData();
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(videouri);
                videoView.start();
                imageView.setVisibility(View.GONE);
            }

        }
    }//end resultactivity

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //start add announce
    private void addAnnounce() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String timestamp = String.valueOf(9999999999999L + (-1 * new Date().getTime()));
        if (imguri != null) {
            StorageReference mFileRef = announceStorage.child(System.currentTimeMillis() + "." + getFileExtension(imguri));
            mFileRef.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imgurl = uri.toString();
                            String type = "image";
                            String content = editAnnounce.getText().toString().trim();
                            Announcements announces = new Announcements(content, imgurl, System.currentTimeMillis()
                                    , type, firebaseUser.getUid(), timestamp);
                            announceRef.child(timestamp).setValue(announces);
                            orgAnnounces.child(currentOrg).child(timestamp).child("Posted").setValue("Posted");
                            Toast.makeText(AddNewPost.this, R.string.posted, Toast.LENGTH_SHORT).show();
                            imguri = null;
                            editAnnounce.setText("");
                            imageView.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            photoOrVideo.setVisibility(View.VISIBLE);
                            addimgorvideolbl.setVisibility(View.VISIBLE);
                            removeimgbtn.setVisibility(View.GONE);

                        }
                    });
                }
            });

        }//end imguri
        else if (videouri != null) {
            StorageReference mFileRef = announceStorage.child(System.currentTimeMillis() + "." + getFileExtension(videouri));
            mFileRef.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String videourl = uri.toString();
                            String type = "video";
                            String content = editAnnounce.getText().toString().trim();
                            Announcements announces = new Announcements(content, videourl, System.currentTimeMillis(),
                                    type, firebaseUser.getUid(), timestamp);
                            announceRef.child(timestamp).setValue(announces);
                            orgAnnounces.child(currentOrg).child(timestamp).child("Posted").setValue("Posted");
                            Toast.makeText(AddNewPost.this, R.string.posted, Toast.LENGTH_SHORT).show();
                            videouri = null;
                            editAnnounce.setText("");
                            imageView.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            photoOrVideo.setVisibility(View.VISIBLE);
                            addimgorvideolbl.setVisibility(View.VISIBLE);
                            removeimgbtn.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "text lang", Toast.LENGTH_SHORT).show();
            String text = "none";
            String type = "text";
            String content = editAnnounce.getText().toString().trim();
            Announcements announces = new Announcements(content, text, System.currentTimeMillis(),
                    type, firebaseUser.getUid(), timestamp);
            announceRef.child(timestamp).setValue(announces);
            orgAnnounces.child(currentOrg).child(timestamp).child("Posted").setValue("Posted");
            Toast.makeText(AddNewPost.this, R.string.posted, Toast.LENGTH_SHORT).show();
            editAnnounce.setText("");
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }

    }//end add announce



}