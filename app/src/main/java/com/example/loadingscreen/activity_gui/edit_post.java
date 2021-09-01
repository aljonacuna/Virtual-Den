package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.Utils.uriPathUtils;
import com.example.loadingscreen.model.postList_model;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

import static com.example.loadingscreen.Utils.arrfordd.dialog;
import static com.example.loadingscreen.Utils.lostfoundUtils.FoundCache;
import static com.example.loadingscreen.Utils.lostfoundUtils.LostCache;

public class edit_post extends AppCompatActivity implements View.OnClickListener {
    private ImageView imagePost;
    private EditText captionTxt, itemname;
    private TextInputLayout status;
    private AutoCompleteTextView ddStatus;
    private Button savebtn;
    private TextView change_statusbtn, status_edit;
    private RelativeLayout rel;
    private DatabaseReference reference;
    String post_key, imageurl, post_caption, status_Text, item_Text, authorid, authorname, dateposted;
    public static final int REQUEST = 1;
    private StorageReference storageReference;
    private Uri imgUri;
    private ProgressDialog progressDialog;
    private Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        reference = FirebaseDatabase.getInstance().getReference("Post");
        progressDialog = new ProgressDialog(this);
        imagePost = findViewById(R.id.image_post);
        captionTxt = findViewById(R.id.caption_input);
        savebtn = findViewById(R.id.save_btn);
        ddStatus = findViewById(R.id.status_editdd);
        itemname = findViewById(R.id.item_textedit);
        rel = findViewById(R.id.edit_imagerel);
        change_statusbtn = findViewById(R.id.change_status);
        status_edit = findViewById(R.id.status_lbl_edit_post);
        status = findViewById(R.id.status_edit);
        permissionUtils.isPermissionGranted(this);
        storageReference = FirebaseStorage.getInstance().getReference("Post");
        post_key = getIntent().getExtras().getString("postkey");
        Intent intent = getIntent();
        arrfordd arrstatus = new arrfordd();
        ddStatus.setAdapter(arrstatus.status(this));
        if (intent.hasExtra("postimg")) {
            imageurl = getIntent().getExtras().getString("postimg");
            if (imageurl.equals("none")) {
                imagePost.setImageResource(R.drawable.noimage);
            } else {
                Picasso.with(this).load(imageurl).into(imagePost);
            }
        } else {
            imagePost.setImageResource(R.drawable.noimage);
        }
        change_statusbtn.setPaintFlags(change_statusbtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        post_caption = getIntent().getExtras().getString("desc");
        status_Text = getIntent().getExtras().getString("status");
        item_Text = getIntent().getExtras().getString("item");
        authorname = getIntent().getExtras().getString("name");
        authorid = getIntent().getExtras().getString("id");
        dateposted = getIntent().getExtras().getString("dateposted");
        status_edit.setText(status_Text);
        itemname.setText(item_Text);
        rel.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        captionTxt.setText(post_caption);
        change_statusbtn.setOnClickListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "External storage permission required", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        if (view == rel) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);
        } else if (view == savebtn) {
            final arrfordd dialogutil = new arrfordd();
            final String titlestr = "Are you sure you want to edit this?";
            final String msgstr = "If you click confirm button the information of this post will be change.";
            dialogutil.dialog_conf(this, titlestr, msgstr,"","",
                    new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String status_lbl = status_edit.getText().toString();
                    dialog.dismiss();
                    try {
                        editpost(status_lbl);
                    } catch (IOException e) {
                        dialog.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(edit_post.this, "Error:  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

        } else if (view == change_statusbtn) {
            status.setVisibility(View.VISIBLE);
            status_edit.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imgUri = data.getData();
            Picasso.with(this).load(imgUri).into(imagePost);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getFileExtensionType(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void editpost(final String status_lbl) throws IOException {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_ui);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (imgUri != null) {
            File file = new File(uriPathUtils.getPath(this,imgUri));
            bitmap = new Compressor(this)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
            final byte[]bytes = baos.toByteArray();
            final StorageReference editpostimg = storageReference.child(System.currentTimeMillis() + "." + getFileExtensionType(imgUri));
            editpostimg.putBytes(bytes)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return editpostimg.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri dowloadUri;
                            if (task.isSuccessful()) {
                                dowloadUri = task.getResult();
                                if (status_lbl.equals("")) {
                                    changeStatus();
                                    progressDialog.dismiss();
                                } else {
                                    String captionText = captionTxt.getText().toString().trim();
                                    String item = itemname.getText().toString();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("item", item);
                                    hashMap.put("photo", dowloadUri.toString());
                                    hashMap.put("desc", captionText);
                                    reference.child(status_lbl).child(post_key).updateChildren(hashMap);
                                    FoundCache.clear();
                                    LostCache.clear();
                                    Toast.makeText(edit_post.this, "Successfully Edited", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(edit_post.this, lostandfound.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                }
                            }
                        }
                    });
        } else {
            if (status_lbl.equals("")) {
                changeStatus();
                progressDialog.dismiss();
            } else {
                String captionText = captionTxt.getText().toString().trim();
                String item = itemname.getText().toString();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("item", item);
                hashMap.put("desc", captionText);
                reference.child(status_lbl).child(post_key).updateChildren(hashMap);
                FoundCache.clear();
                LostCache.clear();
                Toast.makeText(edit_post.this, "Successfully Edited", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(edit_post.this, lostandfound.class);
                startActivity(intent);
                finish();
                progressDialog.dismiss();
            }
        }
    }

    public void changeStatus() {
        String captionText = captionTxt.getText().toString().trim();
        String item = itemname.getText().toString();
        String status_edit = status.getEditText().getText().toString();
        postList_model postlists = new postList_model(authorname, imageurl, captionText, post_key, authorid, dateposted, status_edit, item);
        reference.child(status_edit).child(post_key).setValue(postlists);
        reference.child(status_Text).child(post_key).removeValue();
        FoundCache.clear();
        LostCache.clear();
        Toast.makeText(edit_post.this, "Successfully Edited", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(edit_post.this, lostandfound.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}