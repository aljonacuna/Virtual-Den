package com.example.loadingscreen.activity_gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.sharedpref;
import com.example.loadingscreen.Utils.updateProfileBack;
import com.example.loadingscreen.win.activity.Mainmenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private EditText editBio, yearsection;
    private ImageView cancelbtn, savebtn;
    private RadioGroup rg_gender, rg_group;
    private RadioButton rb_gender, rb_group;
    private DatabaseReference getDataref;
    private FirebaseUser firebaseUser;
    private TextView lbl;
    boolean focus = false;
    private RelativeLayout relativeLayout;
    String userType, genderText = "", groupText = "", tag = "", visitid="", visitusertype="", visitdispname="";
    DatabaseReference saveRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editBio = findViewById(R.id.bioEdit);
        savebtn = findViewById(R.id.save_edit);
        lbl = findViewById(R.id.genderLbl);
        cancelbtn = findViewById(R.id.editprofile_closebtn);
        yearsection = findViewById(R.id.yearsection_ET);
        rg_gender = findViewById(R.id.RgGenderEdit);
        rg_group = findViewById(R.id.rg_groupedit);
        relativeLayout = findViewById(R.id.relforstud);
        userType = sharedpref.spUsertype(this);
        editBio.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        yearsection.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getDataref = FirebaseDatabase.getInstance().getReference(userType);
        saveRef = FirebaseDatabase.getInstance().getReference(userType).child(firebaseUser.getUid());
        if (getIntent().getExtras() != null) {
            tag = getIntent().getExtras().getString("tag1");
        }
        if (getIntent().getExtras() != null && getIntent().hasExtra("displayname")) {
            visitdispname = getIntent().getExtras().getString("displayname");
            visitid = getIntent().getExtras().getString("userid");
            visitusertype = getIntent().getExtras().getString("userType");
        }
        getData();
        if (userType.equals("Faculty")) {
            relativeLayout.setVisibility(View.GONE);
        } else if (userType.equals("Student")) {
            relativeLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == savebtn) {
            saveData();
        } else if (view == editBio) {
            editBio.setFocusable(true);
            editBio.setFocusableInTouchMode(true);
            editBio.requestFocus();
        } else if (view == yearsection) {
            yearsection.setFocusable(true);
            yearsection.setFocusableInTouchMode(true);
            yearsection.requestFocus();
        } else if (view == cancelbtn) {
            updateProfileBack.onBack(this,tag,visitdispname,visitusertype,visitid);
        }
    }

    public void getData() {
        getDataref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String gender = snapshot.child("gender").getValue().toString();
                        editBio.setText(snapshot.child("about").getValue().toString());
                        if (gender.equals("")) {
                        } else {
                            if (gender.equals("Male")) {
                                rg_gender.check(R.id.RbMaleEdit);
                                genderText = gender;
                            } else {
                                rg_gender.check(R.id.RbFemaleEdit);
                                genderText = gender;
                            }
                        }
                        if (userType.equals("Student")) {
                            String group = snapshot.child("group").getValue().toString();
                            yearsection.setText(snapshot.child("yearsection").getValue().toString());
                            if (group.equals("")) {

                            } else {
                                if (group.equals("G1")) {
                                    rg_group.check(R.id.rb_g1edit);
                                    groupText = group;
                                } else {
                                    rg_group.check(R.id.rb_g2edit);
                                    groupText = group;
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getYs(final ysCallBack callBack) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student")
                .child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callBack.onCallBack(snapshot.child("yearsection").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface ysCallBack {
        void onCallBack(String ys);
    }

    public void saveData() {
        if (userType.equals("Student")) {
            getYs(new ysCallBack() {
                @Override
                public void onCallBack(String ys) {
                    String yearsectionText = yearsection.getText().toString();
                    if (!yearsectionText.equals(ys) && groupText.equals("")) {
                        Toast.makeText(EditProfile.this, "Please choose group!", Toast.LENGTH_SHORT).show();
                    } else {
                        String bio = editBio.getText().toString().trim();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("about", bio);
                        hashMap.put("gender", genderText);
                        hashMap.put("yearsection", yearsectionText.toUpperCase());
                        hashMap.put("group", groupText);
                        save_ref(hashMap);

                    }
                }
            });
        } else {
            String bio = editBio.getText().toString().trim();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("about", bio);
            hashMap.put("gender", genderText);
            save_ref(hashMap);
        }
    }

    private void save_ref(HashMap<String, Object> hashMap) {
        saveRef.updateChildren(hashMap);
        Toast.makeText(EditProfile.this, "Profile display updated", Toast.LENGTH_LONG).show();
        if (tag.equals("myprofile")) {
            Intent intent = new Intent(EditProfile.this, Mainmenu.class);
            intent.putExtra("profile", "profile");
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, userprofile.class);
            intent.putExtra("displayname", visitdispname);
            intent.putExtra("userType", visitusertype);
            intent.putExtra("userid", visitid);
            startActivity(intent);
            finish();
        }
    }

    public void editGender(View view) {
        int radioID = rg_gender.getCheckedRadioButtonId();
        rb_gender = findViewById(radioID);
        genderText = rb_gender.getText().toString();
    }

    public void group_edit(View view) {
        int radioID = rg_group.getCheckedRadioButtonId();
        rb_group = findViewById(radioID);
        groupText = rb_group.getText().toString();
    }



    @Override
    public void onBackPressed() {
        updateProfileBack.onBack(this,tag,visitdispname,visitusertype,visitid);
    }
}