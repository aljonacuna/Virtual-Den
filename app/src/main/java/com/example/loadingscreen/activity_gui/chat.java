package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.permissionUtils;
import com.example.loadingscreen.adapter.chatAdapter;
import com.example.loadingscreen.model.chatList_model;
import com.example.loadingscreen.model.contact_list;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class chat extends AppCompatActivity implements View.OnClickListener {
    private EditText msgtxt;
    private ImageView sendbtn, chatmate_pic, sendImg_btn, sendImg, cancelbtn, backbtn;
    private CardView onlineCard;
    private TextView chatmate_name, lastOnlineTime;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private chatAdapter chat_adapter;
    private ArrayList<chatList_model> chatList;
    String receiverid, receivername, receiverimg, receiverusertype;
    private String noProfileimg = "https://th.bing.com/th/id/OIP.jIJ5g0Yp4mPTHuVRwA05bQHaHa?pid=Api&rs=1";
    private DatabaseReference seenReference;
    private ValueEventListener seenStatusListener;
    private LinearLayoutManager linearLayoutManager;
    private StorageReference storageReference;
    private Uri imgUri;
    private Uri downloadUri;
    private RelativeLayout imgholder;
    private final static int REQUEST = 1;
    private ProgressBar progressBar;
    String postKey, post_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        progressBar = findViewById(R.id.progress_barchat);
        backbtn = findViewById(R.id.backBtnchat);
        imgholder = findViewById(R.id.imgHolder);
        msgtxt = findViewById(R.id.msgTxt);
        cancelbtn = findViewById(R.id.cancelBtn);
        sendbtn = findViewById(R.id.sendBtn);
        sendImg = findViewById(R.id.sendImg);
        chatmate_name = findViewById(R.id.displaynameChat);
        chatmate_pic = findViewById(R.id.chat_img);
        lastOnlineTime = findViewById(R.id.lastOnlinetime);
        onlineCard = findViewById(R.id.online_card);
        recyclerView = findViewById(R.id.recyclerView_chat);
        sendImg_btn = findViewById(R.id.sendImgbtn);
        sendbtn.setOnClickListener(this);
        sendImg_btn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        receiverid = getIntent().getExtras().getString("receiverid");
        receivername = getIntent().getExtras().getString("receivername");
        receiverimg = getIntent().getExtras().getString("receiverimg");
        receiverusertype = getIntent().getExtras().getString("receiveruserType");
        databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        storageReference = FirebaseStorage.getInstance().getReference("Chat");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatmate_name.setText(receivername);
        if (receiverimg == null) {
            getDataReceiver();
        } else {
            if (receiverimg.equals("none") || receiverimg.equals("")) {
                Picasso.with(this).load(noProfileimg).into(chatmate_pic);
            } else {
                Picasso.with(this).load(receiverimg).into(chatmate_pic);

            }
        }
        displayMessage();
        seenMessage(receiverid);
        getStatus_Receiver();
        msgtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (msgtxt.length() > 0) {
                    sendbtn.setImageResource(R.drawable.ic_baseline_send);
                } else {
                    sendbtn.setImageResource(R.drawable.ic_baseline_send_24);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (getIntent().getExtras() != null && getIntent().hasExtra("msgclaim")) {
            String claim = getIntent().getExtras().getString("msgclaim");
            postKey = getIntent().getExtras().getString("postkey");
            post_status = getIntent().getExtras().getString("status");
            sendMessage(claim);
        } else if (getIntent().getExtras() != null && getIntent().hasExtra("msghelp")) {
            String help = getIntent().getExtras().getString("msghelp");
            postKey = getIntent().getExtras().getString("postkey");
            post_status = getIntent().getExtras().getString("status");
            sendMessage(help);
        }
        permissionUtils.isPermissionGranted(this);
    }

    public void getDataReceiver() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userstbl").child(receiverid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String usertype = snapshot.child("userType").getValue().toString();
                    DatabaseReference dref = FirebaseDatabase.getInstance().getReference(usertype).child(receiverid);
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                receiverimg = snapshot.child("profileimg").getValue().toString();
                                if (receiverimg.equals("none")) {
                                    Picasso.with(chat.this).load(noProfileimg).into(chatmate_pic);
                                } else {
                                    Picasso.with(chat.this).load(receiverimg).into(chatmate_pic);
                                }
                            } else {
                                Toast.makeText(chat.this, "Not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(chat.this, "Not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //get status of the receiver wheter he/she is online or not
    private void getStatus_Receiver() {
        //usertype is null
        if (receiverusertype == null) {
            DatabaseReference dref = FirebaseDatabase.getInstance().getReference("userstbl").child(receiverid);
            dref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    receiverusertype = snapshot.child("userType").getValue().toString();
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , hh:mm aaa");
                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference(receiverusertype).child(receiverid);
                    dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String onlineStatus = snapshot.child("status").getValue().toString();
                                if (onlineStatus.equals("online")) {
                                    onlineCard.setVisibility(View.VISIBLE);
                                    lastOnlineTime.setText("Online Now");
                                } else {
                                    if (onlineStatus.equals("")) {
                                        lastOnlineTime.setText("Inactive");
                                    } else {
                                        Date date = new Date();
                                        try {
                                            date = dateFormat.parse(onlineStatus);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        String lastOnlineNiya = (String) DateUtils.getRelativeTimeSpanString(date.getTime(),
                                                Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
                                        lastOnlineTime.setText("Online " + lastOnlineNiya);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        //with usertype from intent
        else {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , hh:mm aaa");
            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference(receiverusertype).child(receiverid);
            dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String onlineStatus = snapshot.child("status").getValue().toString();
                        if (onlineStatus.equals("online")) {
                            onlineCard.setVisibility(View.VISIBLE);
                            lastOnlineTime.setText("Online Now");
                        } else {
                            if (onlineStatus.equals("")) {
                                lastOnlineTime.setText("Inactive");
                            } else {
                                Date date = new Date();
                                try {
                                    date = dateFormat.parse(onlineStatus);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String lastOnlineNiya = (String) DateUtils.getRelativeTimeSpanString(date.getTime(),
                                        Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
                                lastOnlineTime.setText("Online " + lastOnlineNiya);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view == sendbtn) {
            sendMessage(null);
        } else if (view == sendImg_btn) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST);

        } else if (view == cancelbtn) {
            imgholder.setVisibility(View.GONE);
            sendbtn.setImageResource(R.drawable.ic_baseline_send_24);
            imgUri = null;
        } else if (view == backbtn) {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgUri = data.getData();
        Picasso.with(this).load(imgUri).placeholder(R.drawable.placeholder).into(sendImg);
        imgholder.setVisibility(View.VISIBLE);
        sendbtn.setImageResource(R.drawable.ic_baseline_send);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    public void msgTypeImg(final String dateTime, final String seen, final String key) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference imgref = FirebaseDatabase.getInstance().getReference("Chat");
        final String type = "image";
        if (imgUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference sendImgref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));
            sendImgref.putFile(imgUri)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return sendImgref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult();
                                chatList_model chat_sendimg = new chatList_model(downloadUri.toString(), receiverid,
                                        firebaseUser.getUid(), seen, dateTime, type, key);
                                imgref.child(key).setValue(chat_sendimg);
                                Toast.makeText(chat.this, "Message sent", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

        }

    }

    public void sendMessage(String lostfound) {
        final String currentUser = firebaseUser.getUid();
        String seen = "false";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy , hh:mm aaa");
        final String dateTime = dateFormat.format(Calendar.getInstance().getTime());
        DatabaseReference getkey = FirebaseDatabase.getInstance().getReference("Chat").push();
        String key = getkey.getKey();
        String msg_txt = msgtxt.getText().toString().trim();
        if (msg_txt.length() == 0 && imgUri == null) {
            if (lostfound != null) {
                String type = "link";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("chat", lostfound);
                hashMap.put("receiverId", receiverid);
                hashMap.put("senderId", currentUser);
                hashMap.put("seen", seen);
                hashMap.put("timeStamp", dateTime);
                hashMap.put("type", type);
                hashMap.put("linkkey", postKey + "/" + post_status);
                hashMap.put("chatkey", key);
                databaseReference.child(key).setValue(hashMap);
                savesendertoContact();
                savereceivertoContact();
                Toast.makeText(chat.this, "Message sent", Toast.LENGTH_SHORT).show();
            }
            //do nothing
        } else {
            if (!TextUtils.isEmpty(msg_txt)) {
                String type = "text";
                final chatList_model chat_class = new chatList_model(msg_txt, receiverid, currentUser, seen, dateTime, type, key);
                databaseReference.child(key).setValue(chat_class);
                //for sender
                savesendertoContact();
                //for receiver list
                savereceivertoContact();
                Toast.makeText(chat.this, "Message sent", Toast.LENGTH_SHORT).show();
                msgtxt.setText("");
            } else if (imgUri != null) {
                msgTypeImg(dateTime, seen, key);
                savesendertoContact();
                savereceivertoContact();
                imgholder.setVisibility(View.GONE);
                sendbtn.setImageResource(R.drawable.ic_baseline_send_24);
                imgUri = null;
            }

        }
    }

    private void savesendertoContact() {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        final DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("ContactList").child(firebaseUser.getUid())
                .child(receiverid);

        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    contact_list contactLists = new contact_list(receiverid, receivername, timeStamp);
                    contactRef.setValue(contactLists);
                } else if (snapshot.hasChild("timestamp")) {
                    String contactTimestamp = snapshot.child("timestamp").getValue().toString();
                    if (!timeStamp.equals(contactTimestamp)) {
                        contactRef.child("timestamp").setValue(timeStamp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void savereceivertoContact() {
        final String timeStamp1 = String.valueOf(System.currentTimeMillis());
        final DatabaseReference contactRef1 = FirebaseDatabase.getInstance().getReference("ContactList").child(receiverid)
                .child(firebaseUser.getUid());
        contactRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    String currentusername = firebaseUser.getDisplayName();
                    contact_list contactLists1 = new contact_list(firebaseUser.getUid(), currentusername, timeStamp1);
                    contactRef1.setValue(contactLists1);

                } else if (snapshot.hasChild("timestamp")) {
                    String contactTimestamp1 = snapshot.child("timestamp").getValue().toString();
                    if (!timeStamp1.equals(contactTimestamp1)) {
                        contactRef1.child("timestamp").setValue(timeStamp1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Display messages
    public void displayMessage() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = firebaseUser.getUid();
        chatList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String sender = dataSnapshot.child("senderId").getValue().toString();
                    String receiver = dataSnapshot.child("receiverId").getValue().toString();
                    if (sender.equals(currentUser) && receiver.equals(receiverid) ||
                            receiver.equals(currentUser) && sender.equals(receiverid)) {
                        chatList_model chat_class = dataSnapshot.getValue(chatList_model.class);
                        if (dataSnapshot.hasChild("linkkey")) {
                            chat_class.setLinkkey(dataSnapshot.child("linkkey").getValue().toString());
                            chatList.add(chat_class);
                        } else {
                            chatList.add(chat_class);
                        }
                    }
                }
                chat_adapter = new chatAdapter(chat.this, chatList, receiverimg, postKey, post_status);
                recyclerView.setAdapter(chat_adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void seenMessage(final String receiverid) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = firebaseUser.getUid();
        seenReference = FirebaseDatabase.getInstance().getReference("Chat");
        seenStatusListener = seenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    chatList_model chat_class = dataSnapshot.getValue(chatList_model.class);
                    if (chat_class.getSenderId().equals(receiverid) && chat_class.getReceiverId().equals(currentUser)) {
                        HashMap<String, Object> seenhashMap = new HashMap<>();
                        seenhashMap.put("seen", "true");
                        dataSnapshot.getRef().updateChildren(seenhashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    protected void onPause() {
        super.onPause();
        seenReference.removeEventListener(seenStatusListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}