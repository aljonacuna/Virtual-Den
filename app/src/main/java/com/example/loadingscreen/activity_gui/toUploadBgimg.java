package com.example.loadingscreen.activity_gui;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.Utils.updateProfileBack;
import com.example.loadingscreen.Utils.uriPathUtils;
import com.example.loadingscreen.win.activity.Mainmenu;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

import static com.example.loadingscreen.Utils.sharedpref.user_type_get;

public class toUploadBgimg extends AppCompatActivity implements View.OnClickListener {
    private ImageView bg_img,cancelbtn;
    private Button bg_save;
    private final static int REQUEST = 1;
    private Uri imguri;
    private TextView changebgpic;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String usertype = "",tag="",visitid,visitusertype,visitdispname;;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_upload_bgimg);
        storageReference = FirebaseStorage.getInstance().getReference("BackgroundImg");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        changebgpic = findViewById(R.id.changebgpic);
        cancelbtn = findViewById(R.id.cancel_btnbgpic);
        progressDialog = new ProgressDialog(this);
        permissionUtils.isPermissionGranted(this);
        bg_img = findViewById(R.id.bg_img);
        bg_save = findViewById(R.id.bg_save);
        bg_save.setOnClickListener(this);
        changebgpic.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        if (getIntent().getExtras()!=null){
            tag = getIntent().getExtras().getString("tag1");
        }
        if ( getIntent().getExtras()!=null && getIntent().hasExtra("displayname")){
            visitdispname = getIntent().getExtras().getString("displayname");
            visitid = getIntent().getExtras().getString("userid");
            visitusertype = getIntent().getExtras().getString("userType");
        }
        getBgPic();
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
        if (view == changebgpic) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);
        } else if (view == bg_save) {
            try {
                saveBg_img();
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else if (view == cancelbtn){
            updateProfileBack.onBack(this,tag,visitdispname,visitusertype,visitid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imguri = data.getData();
            Picasso.with(this).load(imguri).into(bg_img);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return MimeTypeMap.getFileExtensionFromUrl(cr.getType(uri));
    }

    public void saveBg_img() throws IOException {

        final String curreUid = firebaseUser.getUid();
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_ui);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (imguri != null) {
            File file = new File(uriPathUtils.getPath(this, imguri));
            bitmap = new Compressor(this)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] bytes = baos.toByteArray();
            final StorageReference sReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imguri));
            sReference.putBytes(bytes)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return sReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloadUri = task.getResult();
                            progressDialog.dismiss();
                            reference = FirebaseDatabase.getInstance().getReference(user_type_get).child(curreUid);
                            reference.child("bgimg").setValue(downloadUri.toString());
                            Toast.makeText(toUploadBgimg.this,
                                    "You have changed your background picture", Toast.LENGTH_LONG).show();
                            if (tag.equals("myprofile")) {
                                Intent intent = new Intent(toUploadBgimg.this, Mainmenu.class);
                                intent.putExtra("profile", "profile");
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(toUploadBgimg.this, userprofile.class);
                                intent.putExtra("displayname",visitdispname);
                                intent.putExtra("userType",visitusertype);
                                intent.putExtra("userid",visitid);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }

    }

    public void getBgPic() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user_type_get).child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bgpic = snapshot.child("bgimg").getValue().toString();
                if (bgpic.equals("none")) {
                    bg_img.setImageResource(R.drawable.placeholder);
                }
                else {

                    Picasso.with(toUploadBgimg.this).load(bgpic).into(bg_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        updateProfileBack.onBack(this,tag,visitdispname,visitusertype,visitid);
    }
}