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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import static com.example.loadingscreen.Utils.utils.DataCache;

public class adding_faculty_sched extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linear_addFs;
    private ImageView load_addFS, closebtn;
    private Button save_btn;
    private TextInputLayout addnote;
    private Uri imgUriFS;
    private TextView successlbl;
    private Uri imgUriSIMG;
    public static final int REQUESTFS = 1;
    public static final int REQUESTSIMG = 2;
    private StorageReference storageReferenceFS;
    private StorageReference storageReferenceSIMG;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    Bitmap bitmap = null;
    Bitmap bitmap1 = null;
    private ProgressBar progressBar;
    String id = "", email = "", label = "", profname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_faculty_sched);
        linear_addFs = findViewById(R.id.linear_addFS);
        load_addFS = findViewById(R.id.load_FSimg);
        addnote = findViewById(R.id.addnoteFS);
        save_btn = findViewById(R.id.save_fsbtn);
        successlbl = findViewById(R.id.successlbl_facultysched);
        progressBar = findViewById(R.id.facultyaddsched_pb);
        closebtn = findViewById(R.id.add_facultyclosebtn);
        permissionUtils.isPermissionGranted(this);
        progressDialog = new ProgressDialog(this);
        closebtn.setOnClickListener(this);
        storageReferenceFS = FirebaseStorage.getInstance().getReference("FacultySched");
        storageReferenceSIMG = FirebaseStorage.getInstance().getReference("Profile");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        save_btn.setOnClickListener(this);
        linear_addFs.setOnClickListener(this);
        label = getIntent().getExtras().getString("label");
        if (label.equals("add")) {

        } else {
            label = "update";
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
    public void onClick(View view) {
        if (view == linear_addFs) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUESTFS);
        } else if (view == save_btn) {
            try {
                save_fs();
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else if (view == closebtn){
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imgUriFS = data.getData();
            load_addFS.setVisibility(View.VISIBLE);
            Picasso.with(this).load(imgUriFS).into(load_addFS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }


    public void save_fs() throws IOException {
        if (imgUriFS != null) {
            progressBar.setVisibility(View.VISIBLE);
            File file = new File(uriPathUtils.getPath(this, imgUriFS));
            bitmap = new Compressor(adding_faculty_sched.this)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] bytes = baos.toByteArray();

            final StorageReference fsRef = storageReferenceFS.child(System.currentTimeMillis() + "." + getFileExtension(imgUriFS));
            fsRef.putBytes(bytes)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fsRef.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String note = addnote.getEditText().getText().toString().trim();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                                String date = simpleDateFormat.format(Calendar.getInstance().getTime());
                                Uri downloaduriFS;
                                downloaduriFS = task.getResult();
                                DatabaseReference databaseReference;
                                databaseReference = FirebaseDatabase.getInstance().getReference("Faculty")
                                        .child(firebaseUser.getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sched", downloaduriFS.toString());
                                hashMap.put("schedDate", label + "/" + date);
                                hashMap.put("schedNote",note);
                                databaseReference.updateChildren(hashMap);
                                DataCache.clear();
                                progressBar.setVisibility(View.GONE);
                                successlbl.setText("Your schedule successfully updated!");
                                successlbl.setVisibility(View.VISIBLE);
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