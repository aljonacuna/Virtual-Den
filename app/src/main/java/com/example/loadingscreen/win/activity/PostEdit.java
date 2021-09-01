package com.example.loadingscreen.win.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class PostEdit extends AppCompatActivity {

    Button updateBtn;
    EditText editContent;
    DatabaseReference contentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        Intent postIds=getIntent();
        final String content=postIds.getStringExtra("content");
        final String postId=postIds.getStringExtra("postId");
        updateBtn=findViewById(R.id.updateBtn);
        editContent=findViewById(R.id.editContent);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.editPostToolbar);
        mToolbar.setNavigationIcon(R.drawable.arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostEdit.this,OrgUserMenu.class);
                startActivity(intent);
            }
        });//end back button

        contentRef= FirebaseDatabase.getInstance().getReference().child("Announcements");
        editContent.setText(content);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            contentRef.child(postId).child("content").setValue(editContent.getText().toString().trim());
                            Toast.makeText(PostEdit.this, "Post updated!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PostEdit.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }
}