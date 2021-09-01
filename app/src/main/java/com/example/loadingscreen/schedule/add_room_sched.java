package com.example.loadingscreen.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.Utils.uriPathUtils;
import com.example.loadingscreen.model.add_roomsched_model;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class add_room_sched extends AppCompatActivity implements View.OnClickListener {
    private String noimage = "https://myoji-yurai.net/profileImages/noimage.png";
    private ImageView browsebtn, image_r_sched,closebtn;
    private RelativeLayout addroomsched;
    private TextInputLayout noteTil;
    private Button save_btn_rSched;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private DatabaseReference keyref;
    private Uri imguri;
    private TextView successlbl;
    public static final int REQUEST = 1;
    private ProgressDialog progressDialog;
    String roomkey,roomname;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room_sched);
        image_r_sched = findViewById(R.id.imgAddroomsched);
        addroomsched = findViewById(R.id.addschedroom);
        save_btn_rSched = findViewById(R.id.save_btn_add_r_sched);
        noteTil = findViewById(R.id.note_TILroomsched);
        successlbl = findViewById(R.id.successlbl_addroomsched);
        save_btn_rSched.setOnClickListener(this);
        addroomsched.setOnClickListener(this);
        closebtn = findViewById(R.id.add_room_sched_closebtn);
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference("Room_Sched");
        permissionUtils.isPermissionGranted(this);
        roomname = getIntent().getExtras().getString("roomname");
        roomkey = getIntent().getExtras().getString("roomKey");
        closebtn.setOnClickListener(this);


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
        if (view == addroomsched) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);
        } else if (view == save_btn_rSched) {
            try {
                saveRoomSched();
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if (view == closebtn){
           finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imguri = data.getData();
            Picasso.with(this).load(imguri).into(image_r_sched);
            image_r_sched.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getExtensionFile(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }


    public void saveRoomSched() throws IOException {
        if (imguri != null) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog_ui);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            keyref = FirebaseDatabase.getInstance().getReference("Schedule").child("Room").push();
            final String key = keyref.getKey();
            File file = new File(uriPathUtils.getPath(this, imguri));
            bitmap = new Compressor(this)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setQuality(50)
                    .compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] bytes = baos.toByteArray();
            final StorageReference saveref = storageReference.child(System.currentTimeMillis() + "." + getExtensionFile(imguri));
            saveref.putBytes(bytes)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return saveref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final String noteTxt = noteTil.getEditText().getText().toString().trim();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM,dd,yyyy");
                                final String date = simpleDateFormat.format(Calendar.getInstance().getTime());
                                progressDialog.dismiss();
                                final Uri downloadUri;
                                downloadUri = task.getResult();
                                reference = FirebaseDatabase.getInstance().getReference("Schedule").child("Room").child(roomkey);
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            String label = "dateposted";
                                            add_roomsched_model addRoomSched = new add_roomsched_model(downloadUri.toString(),
                                                    key,label + "/" + date,noteTxt);
                                            reference.setValue(addRoomSched);
                                            successlbl.setText(roomname+" schedule successfully updated!");
                                            successlbl.setVisibility(View.VISIBLE);
                                        } else {
                                            String label = "updated";
                                            String keys = snapshot.getKey();
                                            HashMap<String, Object> room_sched = new HashMap<>();
                                            room_sched.put("image", downloadUri.toString());
                                            room_sched.put("date", label + "/" + date);
                                            room_sched.put("note",noteTxt);
                                            reference.updateChildren(room_sched);
                                            successlbl.setText(roomname+" schedule successfully updated!");
                                            successlbl.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

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