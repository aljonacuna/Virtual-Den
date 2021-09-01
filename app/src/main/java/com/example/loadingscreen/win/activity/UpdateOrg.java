package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.loadingscreen.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.content.Intent.EXTRA_MIME_TYPES;
import static com.example.loadingscreen.activity_gui.signin.fileName_usertype;
import static com.example.loadingscreen.activity_gui.signin.userTypeSP;

public class UpdateOrg extends AppCompatActivity {

    DatabaseReference userRefUpdate;
    String currentUser;
    FirebaseAuth myAuth;
    EditText updateDescEdit;
    ImageView updateImageView,newImageView;
    Button updateNowBtn,changeImageBtn;
    private static final int PICK_IMAGE_REQUEST=1;
    Uri imguri;
    SharedPreferences preferences;
    StorageReference imageRef;
    String usertype="";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_org);
        myAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        currentUser=myAuth.getCurrentUser().getUid();
        updateDescEdit=findViewById(R.id.updateDescEdit);
        updateImageView=findViewById(R.id.updateImageView);
        newImageView=findViewById(R.id.newImageView);
        updateNowBtn=findViewById(R.id.updateNowBtn);
        changeImageBtn=findViewById(R.id.changeImageBtn);
        preferences = getSharedPreferences(fileName_usertype, Context.MODE_PRIVATE);
        loaddata();
        Toolbar updateToolBar=(Toolbar)findViewById(R.id.updateOrgToolBar);
        updateToolBar.setNavigationIcon(R.drawable.arrow_back);
        updateToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateOrg.this,OrgUserMenu.class);
                startActivity(intent);
            }
        });

        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        updateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateNow();
                }catch (Exception e) {

                }
            }
        });


    }


        private void loaddata(){
        usertype = preferences.getString(userTypeSP,"");
        userRefUpdate= FirebaseDatabase.getInstance().getReference().child(usertype).child(currentUser);
        imageRef= FirebaseStorage.getInstance().getReference().child("uploads");

        userRefUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userImage=snapshot.child("profileimg").getValue().toString();
                    String userDesc=snapshot.child("about").getValue().toString();
                    Picasso.with(UpdateOrg.this).load(userImage).resize(500, 500)
                            .transform(new CropCircleTransformation()).into(updateImageView);
                    updateDescEdit.setText(userDesc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateOrg.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    //intent gallery
    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }//end intent gallery

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()
                != null) {
            updateImageView.setVisibility(View.GONE);
            imguri = data.getData();
            Picasso.with(UpdateOrg.this).load(imguri).resize(500, 500)
                    .transform(new CropCircleTransformation()).into(newImageView);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void updateNow() {
        if(imguri!=null){
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog_ui);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            StorageReference imageRefGet=imageRef.child(System.currentTimeMillis()+"."+getFileExtension(imguri));
            imageRefGet.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String imageRef=uri.toString();
                            userRefUpdate.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        HashMap<String,Object>hashMap = new HashMap<>();
                                        hashMap.put("profileimg",imageRef);
                                        hashMap.put("about",updateDescEdit.getText().toString().trim());
                                        userRefUpdate.updateChildren(hashMap);
                                        progressDialog.dismiss();
                                        Toast.makeText(UpdateOrg.this, "Display updated!", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UpdateOrg.this, "Org not found!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }
            });

        }
        else{
            userRefUpdate.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        userRefUpdate.child("about").setValue(updateDescEdit.getText().toString().trim());
                        Toast.makeText(UpdateOrg.this, "Display updated!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateOrg.this, "Org not found!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}