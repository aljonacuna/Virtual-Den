package com.example.loadingscreen.schedule;

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

public class dimuna_deleteeditclasssc extends AppCompatActivity implements View.OnClickListener {
    private ImageView sched_img;
    private TextView cysg_Text;
    private Button savebtn, showprevbtn;
    String course, yearsection, group, imgurl, key;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private RelativeLayout container;
    public static final int REQUEST = 1;
    private Uri imgUri;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editclass_sched);
        cysg_Text = findViewById(R.id.cysg_text);
        sched_img = findViewById(R.id.sched_img);
        savebtn = findViewById(R.id.save_btn_updateclass);
        showprevbtn = findViewById(R.id.showprevbtn);
        progressDialog = new ProgressDialog(this);
        permissionUtils.isPermissionGranted(this);
        course = getIntent().getExtras().getString("course");
        yearsection = getIntent().getExtras().getString("year");
        group = getIntent().getExtras().getString("group");
        container = findViewById(R.id.container_edit_sched);
        imgurl = getIntent().getExtras().getString("imgurl");
        cysg_Text.setText(course + " " + yearsection + " " + group);
        key = getIntent().getExtras().getString("key");

        storageReference = FirebaseStorage.getInstance().getReference("ClassSched");
        container.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        showprevbtn.setOnClickListener(this);
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
        if (view == container) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);
        } else if (view == savebtn) {
            try {
                editSched();
            } catch (IOException e) {
                Toast.makeText(this, "Error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (view == showprevbtn) {
            Picasso.with(this).load(imgurl).into(sched_img);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imgUri = data.getData();
            sched_img.setVisibility(View.VISIBLE);
            Picasso.with(this).load(imgUri).into(sched_img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileExtensionType(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void editSched() throws IOException {
        if (imgUri != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            final String date = simpleDateFormat.format(Calendar.getInstance().getTime());
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog_ui);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            File file = new File(uriPathUtils.getPath(this, imgUri));
            bitmap = new Compressor(this)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] bytes = baos.toByteArray();
            final StorageReference editsched = storageReference.child(System.currentTimeMillis() + "." + getFileExtensionType(imgUri));
            editsched.putBytes(bytes)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return editsched.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String label = "updated";
                                Uri downloadUri;
                                downloadUri = task.getResult();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("classSched", downloadUri.toString());
                                hashMap.put("date", label + "/" + date);
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                hashMap.put("postedBy", firebaseUser.getDisplayName());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schedule").child("Class")
                                        .child(course);
                                reference.child(key).updateChildren(hashMap);
                                Toast.makeText(dimuna_deleteeditclasssc.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                blisCache.clear();
                                bsitCache.clear();
                                Intent intent = new Intent(dimuna_deleteeditclasssc.this, class_schedule.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }else{
            Toast.makeText(this, "Please upload image schedule", Toast.LENGTH_SHORT).show();
        }
    }
}