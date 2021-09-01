package com.example.loadingscreen.win.Adapter;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.Utils.arrfordd;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.model.Students;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentsViewHolder> {
    ArrayList<usersList_model>studentList;
    Context context;


    public StudentListAdapter(Context c,ArrayList<usersList_model>s){
        context=c;
        studentList=s;
    }

    @NonNull
    @Override
    public StudentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.studentsdesign,parent,false);
        return new StudentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsViewHolder holder, int position) {
        usersList_model users = studentList.get(position);
        holder.studName.setText(users.getFullname());
        Picasso.with(context).load(users.getProfileimg()).resize(40, 40)
                .transform(new CropCircleTransformation()).into(holder.studentImage);
        holder.studentNumber.setVisibility(View.GONE);
        if (users.getIsDisabled().equals("No")){
            holder.verifyBtn.setBackgroundResource(R.drawable.buttonsignin);
            holder.verifyBtn.setTextColor(ContextCompat.getColor(context, R.color.dangertext));
            holder.verifyBtn.setText("Disable");
        }else{
            holder.verifyBtn.setBackgroundResource(R.drawable.buttonfound);
            holder.verifyBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.verifyBtn.setText("Enable");
        }
    }

    @Override
    public int getItemCount() {
        return  studentList.size();
    }

    class StudentsViewHolder extends RecyclerView.ViewHolder{
        TextView studName,studentNumber,verifyBtn;
        ImageView studentImage;
        public StudentsViewHolder(@NonNull View itemView) {
            super(itemView);
            studName=itemView.findViewById(R.id.studentName);
            studentImage=itemView.findViewById(R.id.studentImage);
            studentNumber=itemView.findViewById(R.id.studentNumber);
            verifyBtn=itemView.findViewById(R.id.verifyBtn);
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student")
                            .child(studentList.get(pos).getUserID());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String btnText = verifyBtn.getText().toString();
                            //if button is disable
                            if (btnText.equals("Disable")) {
                                String title ="Confirm Disable";
                                String msg1 = "Disable this account ";
                                String name = studentList.get(pos).getFullname();
                                String msg2 = "?";
                                arrfordd.dialog_conf(context, title, msg1, name, msg2, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("isDisabled", "Yes");
                                        reference.updateChildren(hashMap);
                                        verifyBtn.setBackgroundResource(R.drawable.buttonfound);
                                        verifyBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
                                        verifyBtn.setText("Enable");
                                        arrfordd.dialog.dismiss();
                                    }
                                });

                            }
                            //if button is enable
                            else{
                                String title ="Confirm Enable";
                                String msg1 = "Enable this account ";
                                String name = studentList.get(pos).getFullname();
                                String msg2 = "?";
                                arrfordd.dialog_conf(context, title, msg1, name, msg2, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("isDisabled", "No");
                                        reference.updateChildren(hashMap);
                                        verifyBtn.setBackgroundResource(R.drawable.buttonsignin);
                                        verifyBtn.setTextColor(ContextCompat.getColor(context, R.color.dangertext));
                                        verifyBtn.setText("Disable");
                                        arrfordd.dialog.dismiss();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

        }
    }


    public void filterList(ArrayList<usersList_model> filteredList){
        studentList=filteredList;
        notifyDataSetChanged();
    }
}
