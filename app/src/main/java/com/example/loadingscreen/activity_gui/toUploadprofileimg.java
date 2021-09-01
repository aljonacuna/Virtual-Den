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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class toUploadprofileimg extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageprofile,cancel;
    private Button savebtn;
    private final static int REQUEST = 1;
    private Uri imguri;
    private TextView changeprofile;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    String currentUser;
    String key;
    private Uri downloadUri;
    String usertype = "",tag="",visitid,visitusertype,visitdispname;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_to_uploadprofileimg);
        imageprofile = findViewById(R.id.image_profile);
        savebtn = findViewById(R.id.save_txt);
        changeprofile = findViewById(R.id.changeprofilepic);
        cancel = findViewById(R.id.cancel_profilepic);
        changeprofile.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        cancel.setOnClickListener(this);
        permissionUtils.isPermissionGranted(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Profile");
        if (getIntent().getExtras()!=null){
            tag = getIntent().getExtras().getString("tag1");
        }
        if ( getIntent().getExtras()!=null && getIntent().hasExtra("displayname")){
            visitdispname = getIntent().getExtras().getString("displayname");
            visitid = getIntent().getExtras().getString("userid");
            visitusertype = getIntent().getExtras().getString("userType");
        }
        getProfileimg();
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
        if (view == changeprofile) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);

        } else if (view == savebtn) {
            try {
                saveProfileimg();
            } catch (IOException e) {
                Toast.makeText(this, "Error:  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else if (view == cancel){
            updateProfileBack.onBack(this,tag,visitdispname,visitusertype,visitid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imguri = data.getData();
            Picasso.with(this).load(imguri).into(imageprofile);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void saveProfileimg() throws IOException {

        currentUser = firebaseUser.getUid();
        if (imguri != null) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog_ui);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            File file = new File(uriPathUtils.getPath(this,imguri));
            bitmap = new Compressor(this)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
            final byte[]bytes = baos.toByteArray();
            final StorageReference sReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imguri));
            sReference.putBytes(bytes).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return sReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        downloadUri = task.getResult();
                        if (firebaseUser != null) {
                            UserProfileChangeRequest profile_Img = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(downloadUri.toString()))
                                    .build();
                            firebaseUser.updateProfile(profile_Img).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference = FirebaseDatabase.getInstance().getReference(user_type_get).child(firebaseUser.getUid());
                                        reference.child("profileimg").setValue(downloadUri.toString());
                                        Toast.makeText(toUploadprofileimg.this,
                                                "You have changed your profile picture", Toast.LENGTH_LONG).show();
                                        if (tag.equals("myprofile")) {
                                            Intent intent = new Intent(toUploadprofileimg.this, Mainmenu.class);
                                            intent.putExtra("usertype", usertype);
                                            intent.putExtra("profile", "profile");
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Intent intent = new Intent(toUploadprofileimg.this, userprofile.class);
                                            intent.putExtra("displayname",visitdispname);
                                            intent.putExtra("userType",visitusertype);
                                            intent.putExtra("userid",visitid);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(toUploadprofileimg.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(toUploadprofileimg.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(toUploadprofileimg.this, "Fail: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private void getProfileimg(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user_type_get).child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilepic = snapshot.child("profileimg").getValue().toString();
                Picasso.with(toUploadprofileimg.this).load(profilepic).into(imageprofile);
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