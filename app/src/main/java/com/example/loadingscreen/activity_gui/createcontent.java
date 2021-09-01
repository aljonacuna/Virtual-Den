package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.Utils.uriPathUtils;
import com.example.loadingscreen.model.postList_model;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import id.zelory.compressor.Compressor;

import static com.example.loadingscreen.Utils.lostfoundUtils.FoundCache;
import static com.example.loadingscreen.Utils.lostfoundUtils.LostCache;
import static com.example.loadingscreen.activity_gui.dimunadelete.fileName_permission;

public class createcontent extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout descTxt, itemName;
    private RadioGroup status;
    private RadioButton radioButton;
    private Button postbtn;
    private ImageView imgupload,backbtn;
    private RelativeLayout relativeBrowse;
    private Spinner dropdown;
    private DatabaseReference firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private DatabaseReference keyReference;
    private static final int REQUEST = 1;
    private ProgressDialog progressDialog;
    private Uri imgUri;
    private String userID, postKey;
    private String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
    String statusTxt = "Found";
    private ArrayList<String> lostfound;
    Bitmap bitmap = null;
    File file;
    String realpath;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcontent);
        descTxt = findViewById(R.id.descTxt);
        itemName = findViewById(R.id.itemTxt);
        status = findViewById(R.id.rg_lostfound);
        preferences = getSharedPreferences(fileName_permission, Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference("Post");
        reference = FirebaseDatabase.getInstance().getReference("userstbl");
        progressDialog = new ProgressDialog(createcontent.this);
        relativeBrowse = findViewById(R.id.relativeBrowse);
        imgupload = findViewById(R.id.img_uploadLF);
        backbtn = findViewById(R.id.backBtncreatePostLAD);
        postbtn = findViewById(R.id.postbtn);
        postbtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        relativeBrowse.setOnClickListener(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        permissionUtils.isPermissionGranted(this);
    }

    @Override
    public void onClick(View view) {
        if (view == relativeBrowse) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);
        } else if (view == postbtn) {
            try {
                uploadPost();
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else if (view == backbtn){
            Intent intent = new Intent(this,lostandfound.class);
            startActivity(intent);
            finish();
        }
    }
    public void lostfound_rg(View view) {
        int radioID = status.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        statusTxt = radioButton.getText().toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imgUri = data.getData();
            Picasso.with(this).load(imgUri).into(imgupload);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private String filePath(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
          realpath = cursor.getString(columnIndex);
        } else {
           
        }
        cursor.close();
        return realpath;
    }
    public void withImg(final String dateTime, final String userName, final String item_name,
                        final String desc) throws IOException {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_ui);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            file = new File(filePath(imgUri));
        }else {
            file = new File(uriPathUtils.getPath(this, imgUri));
        }
        bitmap = new Compressor(this)
                .setMaxHeight(800)
                .setMaxWidth(800)
                .setQuality(50)
                .compressToBitmap(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        final byte[] bytes = baos.toByteArray();
        final StorageReference fileref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));
        fileref.putBytes(bytes)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Uri downloadUri = task.getResult();
                    final String timestamp = String.valueOf(9999999999999L + (-1 * new Date().getTime()));
                    Toast.makeText(createcontent.this, "Successfully Post", Toast.LENGTH_LONG).show();
                    if (firebaseUser.getPhotoUrl() != null) {
                        postList_model post = new postList_model(userName, downloadUri.toString(), desc.trim(), timestamp, firebaseUser.getUid(),
                                dateTime, statusTxt, item_name);
                        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Post").child(statusTxt).child(timestamp);
                        firebaseDatabase.setValue(post);
                        Intent intent = new Intent(createcontent.this, lostandfound.class);
                        startActivity(intent);
                        finish();
                    } else {
                        postList_model post = new postList_model(userName, downloadUri.toString(), desc, timestamp, firebaseUser.getUid(),
                                dateTime, statusTxt, item_name);
                        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Post").child(statusTxt).child(timestamp);
                        firebaseDatabase.setValue(post);
                        Intent intent = new Intent(createcontent.this, lostandfound.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(createcontent.this, "Failed to upload" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void withoutImg(final String dateTime, final String item_name, final String desc) {
        final String noImg = "none";
        final String timestamp = String.valueOf(9999999999999L + (-1 * new Date().getTime()));
        String name = firebaseUser.getDisplayName();
        Toast.makeText(createcontent.this, "Successfully Post", Toast.LENGTH_LONG).show();
        if (firebaseUser.getPhotoUrl() != null) {
            postList_model post = new postList_model(name, noImg, desc, timestamp, userID, dateTime, statusTxt, item_name);
            firebaseDatabase = FirebaseDatabase.getInstance().getReference("Post").child(statusTxt).child(timestamp);
            firebaseDatabase.setValue(post);
            Intent intent = new Intent(createcontent.this, lostandfound.class);
            startActivity(intent);
            finish();
        } else {
            postList_model post = new postList_model(name, noImg, desc, timestamp, userID, dateTime, statusTxt, item_name);
            firebaseDatabase = FirebaseDatabase.getInstance().getReference("Post").child(statusTxt).child(timestamp);
            firebaseDatabase.setValue(post);
            Intent intent = new Intent(createcontent.this, lostandfound.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean isItemname() {
        String itemname = itemName.getEditText().getText().toString();
        if (TextUtils.isEmpty(itemname)) {
            itemName.setError("Item name is required");
            itemName.requestFocus();
            return false;
        } else {
            itemName.setError(null);
            return true;
        }
    }

    public boolean isDesc() {
        String desc = descTxt.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(desc)) {
            descTxt.setError("Please enter your Description");
            descTxt.requestFocus();
            return false;
        } else {
            descTxt.setError(null);
            return true;
        }
    }


    public void uploadPost() throws IOException {
        if (!isDesc() || !isItemname()) {
            return;
        } else {
            keyReference = FirebaseDatabase.getInstance().getReference("Post").push();
            postKey = keyReference.getKey();
            userID = firebaseUser.getUid();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy , HH:mm:ss.SSS");
            final String dateTime = dateFormat.format(Calendar.getInstance().getTime());
            final String item_name = itemName.getEditText().getText().toString();
            final String desc = descTxt.getEditText().getText().toString().trim();
            if (imgUri != null) {
                String name = firebaseUser.getDisplayName();
                withImg(dateTime, name, item_name, desc);
                if (statusTxt.equals("Found")) {
                    FoundCache.clear();
                } else if (statusTxt.equals("Lost")) {
                    LostCache.clear();
                }
            } else {
                withoutImg(dateTime, item_name, desc);
                if (statusTxt.equals("Found")) {
                    FoundCache.clear();
                } else if (statusTxt.equals("Lost")) {
                    LostCache.clear();
                }
            }

        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,lostandfound.class);
        startActivity(intent);
        finish();
    }
}