package com.example.loadingscreen.Revise;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.Utils.uriPathUtils;
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
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

import static com.example.loadingscreen.Utils.classSchedUtils.blisCache;
import static com.example.loadingscreen.Utils.classSchedUtils.bsitCache;
import static com.example.loadingscreen.schedule.add_room_sched.REQUEST;

public class UpdateClassSchedSecond extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout relBrowseimg;
    private TextInputLayout noteClassTil;
    private ImageView classImg,cancelbtn;
    private TextView successlbl;
    private Button savebtn;
    private FirebaseUser firebaseUser;
    private Uri imgUri;
    private StorageReference storageReference;
    Bitmap bitmap;
    String course = "", id = "",yearsec,group;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_class_sched_second);
        relBrowseimg = findViewById(R.id.browseImg_classsched);
        noteClassTil = findViewById(R.id.noteTIL);
        savebtn = findViewById(R.id.save_btn_class_schedupdate);
        classImg = findViewById(R.id.Image_Sched_Update);
        successlbl = findViewById(R.id.successlbl_updateclass);
        cancelbtn = findViewById(R.id.update_class_closebtn1);
        cancelbtn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference("ClassSched");
        course = getIntent().getExtras().getString("course");
        yearsec = getIntent().getExtras().getString("yearsec");
        group = getIntent().getExtras().getString("group");
        id = getIntent().getExtras().getString("id");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        relBrowseimg.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        permissionUtils.isPermissionGranted(this);
    }

    @Override
    public void onClick(View view) {
        if (view == relBrowseimg) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);
        } else if (view == savebtn) {
            try {
                saveSched();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(view == cancelbtn){
            finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        classImg.setVisibility(View.VISIBLE);
        try {
            imgUri = data.getData();
            Picasso.with(this).load(imgUri).into(classImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getExtensionFile(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void saveSched() throws IOException {
        if (imgUri != null) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog_ui);
            progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            File file = new File(uriPathUtils.getPath(this, imgUri));
            bitmap = new Compressor(this)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] bytes = baos.toByteArray();
            final StorageReference classRef = storageReference.child(System.currentTimeMillis() + "." + getExtensionFile(imgUri));
            classRef.putBytes(bytes)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return classRef.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                                String date = simpleDateFormat.format(Calendar.getInstance().getTime());
                                String label = "updated";
                                Uri downloadUri = task.getResult();
                                String noteTxt = noteClassTil.getEditText().getText().toString().trim();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Schedule")
                                        .child("Class")
                                        .child(course);
                                HashMap<String,Object>hashMap = new HashMap<>();
                                hashMap.put("classSched",downloadUri.toString());
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                hashMap.put("postedBy", firebaseUser.getDisplayName());
                                hashMap.put("date", label + "/" + date);
                                hashMap.put("note",noteTxt);
                                databaseReference.child(id).updateChildren(hashMap);
                                if (course.equals("BSIT")){
                                    bsitCache.clear();
                                }else{
                                    blisCache.clear();
                                }
                                successlbl.setText(course+" "+yearsec+"-"+group+" schedule successfully updated!");
                                successlbl.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}