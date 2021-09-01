package com.example.loadingscreen.Utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.itemclaim_2;
import com.example.loadingscreen.activity_gui.userpostdetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class clearUtils {
    public static void clear(final String post_status, final String postkey, final Context context, final String tag) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post")
                .child(post_status).child(postkey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.itemclaim_1);
                Button nextbtn = dialog.findViewById(R.id.nxtbtn_claim);
                final TextView title = dialog.findViewById(R.id.titleclaim);
                final TextView dateclaim = dialog.findViewById(R.id.datelbl);
                RelativeLayout lineardate = dialog.findViewById(R.id.lineardate);
                if (post_status.equals("Found")) {
                    title.setText("Someone claimed the item?");
                } else if (post_status.equals("Lost")) {
                    title.setText("Someone returning the item?");
                }
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                lineardate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                int month = i1 + 1;
                                dateclaim.setText(month + "/" + i2 + "/" + i);
                            }
                        },
                                Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                                Calendar.getInstance().get(Calendar.MONTH));
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                });
                nextbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = dateclaim.getText().toString();
                        if (date.equals("Choose Date")) {
                            Toast.makeText(context, "Please enter date", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Intent intent = new Intent(context, itemclaim_2.class);
                            intent.putExtra("date",date);
                            intent.putExtra("status",snapshot.child("status").getValue().toString());
                            intent.putExtra("postkey", snapshot.child("postKey").getValue().toString());
                            intent.putExtra("item", snapshot.child("item").getValue().toString());
                            intent.putExtra("id", snapshot.child("userID").getValue().toString());
                            intent.putExtra("name", snapshot.child("name").getValue().toString());
                            intent.putExtra("dateposted", snapshot.child("dateTime").getValue().toString());
                            intent.putExtra("postimg", snapshot.child("photo").getValue().toString());
                            intent.putExtra("desc", snapshot.child("desc").getValue().toString());
                            intent.putExtra("tag",tag);
                            context.startActivity(intent);
                            ((Activity)context).finish();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
