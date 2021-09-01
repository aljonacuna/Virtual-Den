package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.commentAdapter;
import com.example.loadingscreen.model.commentList_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class commentsection extends AppCompatActivity implements View.OnClickListener {
    private ImageView okbtn;
    private ImageView postImg, userprofilephoto;
    private TextView authorName, caption;
    private EditText commentText;
    private DatabaseReference reference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference saveref;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String user_id, post_key, user_profilephoto;
    private ArrayList<commentList_model> comment_class;
    private RecyclerView recyclerView;
    private commentAdapter adapter;
    private DatabaseReference referenceguest;
    String default_photo = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentsection);
        recyclerView = findViewById(R.id.recyclerviewComment);
        okbtn = findViewById(R.id.ok_btn);
        commentText = findViewById(R.id.comment_txtBox);
        okbtn.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("userstbl");
        String authorStr = getIntent().getExtras().getString("name");
        String captionStr = getIntent().getExtras().getString("caption");
        post_key = getIntent().getExtras().getString("postkey");
        user_id = getIntent().getExtras().getString("userid");
        String img_url = getIntent().getExtras().getString("imgurl");
        displayComment();
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String commentTxt = commentText.getText().toString();
                if (commentTxt.length() > 0) {
                    okbtn.setImageResource(R.drawable.ic_baseline_send);
                } else {
                    okbtn.setImageResource(R.drawable.ic_baseline_send_24);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == okbtn) {
            sendComment();
        }
    }

    public void sendComment() {
        final String commenttext = commentText.getText().toString();
        if (commenttext.length() != 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , HH:mm:ss.SSS");
            final String dateTime = dateFormat.format(Calendar.getInstance().getTime());
            final String userID = firebaseUser.getUid();
            saveref = firebaseDatabase.getInstance().getReference("Comment").child(post_key).push();
            String user_name = firebaseUser.getDisplayName();
            if (firebaseUser.getPhotoUrl() != null) {
                commentList_model comment_class = new commentList_model(user_name, userID, user_id, post_key, commenttext, firebaseUser.getPhotoUrl().toString(), dateTime);
                saveref.setValue(comment_class).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(commentsection.this, "Comment Added", Toast.LENGTH_LONG).show();
                        commentText.setText("");
                    }
                });

            } else {
                commentList_model comment_class = new commentList_model(user_name, userID, user_id, post_key, commenttext, default_photo, dateTime);
                saveref.setValue(comment_class).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(commentsection.this, "Comment Added", Toast.LENGTH_LONG).show();
                        commentText.setText("");
                    }
                });
            }

        }
    }

    /**
     * public void guestUser(){
     * final String commenttext = commentText.getText().toString();
     * if (commenttext.length()!=0) {
     * SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , HH:mm:ss.SSS");
     * final String dateTime = dateFormat.format(Calendar.getInstance().getTime());
     * final String userID = firebaseUser.getUid();
     * referenceguest.addListenerForSingleValueEvent(new ValueEventListener() {
     *
     * @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
     * if (snapshot.exists()) {
     * saveref = firebaseDatabase.getInstance().getReference("Comment").child(post_key).push();
     * String userComment = snapshot.child("fullname").getValue(String.class);
     * if (firebaseUser.getPhotoUrl() != null) {
     * commentList_model comment_class = new commentList_model(userComment, userID, user_id, post_key, commenttext, firebaseUser.getPhotoUrl().toString(), dateTime);
     * saveref.setValue(comment_class).addOnCompleteListener(new OnCompleteListener<Void>() {
     * @Override public void onComplete(@NonNull Task<Void> task) {
     * Toast.makeText(commentsection.this, "Comment Added", Toast.LENGTH_LONG).show();
     * commentText.setText("");
     * }
     * });
     * <p>
     * } else {
     * commentList_model comment_class = new commentList_model(userComment, userID, user_id, post_key, commenttext, default_photo, dateTime);
     * saveref.setValue(comment_class).addOnCompleteListener(new OnCompleteListener<Void>() {
     * @Override public void onComplete(@NonNull Task<Void> task) {
     * Toast.makeText(commentsection.this, "Comment Added", Toast.LENGTH_LONG).show();
     * commentText.setText("");
     * }
     * });
     * }
     * <p>
     * }
     * }
     * @Override public void onCancelled(@NonNull DatabaseError error) {
     * <p>
     * }
     * });
     * }
     * }
     */
    public void displayComment() {
        comment_class = new ArrayList<>();
        DatabaseReference displayRef = FirebaseDatabase.getInstance().getReference("Comment").child(post_key);
        displayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    comment_class.clear();
                    for (DataSnapshot data_snapshot : snapshot.getChildren()) {
                        commentList_model comment_display = data_snapshot.getValue(commentList_model.class);
                        comment_class.add(comment_display);
                    }
                    adapter = new commentAdapter(commentsection.this, comment_class);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(commentsection.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(commentsection.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}