package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.adapter.claim_getuserAdapter;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.activity.Mainmenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class itemclaim_2 extends AppCompatActivity {
    private RecyclerView claimRv;
    private claim_getuserAdapter claim_adapter;
    private TextView dateclaim,datelbl;
    private SearchView claim_nameSv;
    String postkey,status,date,item,authorid,name,dateposted,postimg,tag;
    private RadioGroup rg_claim;
    private RadioButton radioButton;
    String claim_userstatus = "Student";
    private ArrayList<usersList_model>claimerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemclaim_2);
        dateclaim = findViewById(R.id.dateclaim);
        datelbl = findViewById(R.id.dateLbl);
        claimRv = findViewById(R.id.claim_rv);
        claim_nameSv = findViewById(R.id.claim_nameSv);
        rg_claim = findViewById(R.id.rg_claim);
        postkey = getIntent().getExtras().getString("postkey");
        status = getIntent().getExtras().getString("status");
        date = getIntent().getExtras().getString("date");
        item = getIntent().getExtras().getString("item");
        authorid = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        dateposted = getIntent().getExtras().getString("dateposted");
        postimg = getIntent().getExtras().getString("postimg");
        tag = getIntent().getExtras().getString("tag");
        if (status.equals("Found")){
            datelbl.setText("Date Claimed");
        }
        else if (status.equals("Lost")){
            datelbl.setText("Date Returned");
        }
        dateclaim.setText(date);
        claim_nameSv.requestFocus();
        claim_nameSv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        claim_nameSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getclaimerName(s.toLowerCase());
                return false;
            }
        });
    }
    public void getclaimerName(final String text){
        if (claim_userstatus!=null) {
            Query query = FirebaseDatabase.getInstance().getReference(claim_userstatus)
                    .orderByChild("sname")
                    .startAt(text)
                    .endAt(text + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren() || snapshot.exists()) {
                        claimerList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            usersList_model claimer = dataSnapshot.getValue(usersList_model.class);
                            claimerList.add(claimer);
                        }
                        claim_adapter = new claim_getuserAdapter(itemclaim_2.this, date, postkey, status, item, authorid,
                                name,dateposted,postimg);
                        claim_adapter.filter(claimerList);
                        claimRv.setAdapter(claim_adapter);
                        claimRv.setLayoutManager(new LinearLayoutManager(itemclaim_2.this));
                    }
                    else{
                        if (claimerList!=null) {
                            claimerList.clear();
                            claim_adapter = new claim_getuserAdapter(itemclaim_2.this, date, postkey, status, item, authorid,name,dateposted,postimg);
                            claim_adapter.filter(claimerList);
                            claimRv.setAdapter(claim_adapter);
                            claimRv.setLayoutManager(new LinearLayoutManager(itemclaim_2.this));
                            Toast.makeText(itemclaim_2.this, "No result found", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(itemclaim_2.this, "No result found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (text.length()==0){
                        claimerList.clear();
                        claim_adapter = new claim_getuserAdapter(itemclaim_2.this, date, postkey, status, item, authorid,name,dateposted,postimg);
                        claim_adapter.filter(claimerList);
                        claimRv.setAdapter(claim_adapter);
                        claimRv.setLayoutManager(new LinearLayoutManager(itemclaim_2.this));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            Toast.makeText(this, "Student or Staff?", Toast.LENGTH_SHORT).show();
        }
    }

    public void rb_userstatus(View view) {
        int radioID = rg_claim.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        claim_userstatus = radioButton.getText().toString();
        claim_nameSv.setQuery("",false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (tag.equals("visitprofile")) {
            Intent intent = new Intent(this, userprofile.class);
            intent.putExtra("displayname",firebaseUser.getDisplayName());
            intent.putExtra("userid",firebaseUser.getUid());
            startActivity(intent);
        }else if (tag.equals("myprofile")){
            Intent intent = new Intent(this, Mainmenu.class);
            intent.putExtra("profile", "profile");
            startActivity(intent);
        }
    }
}