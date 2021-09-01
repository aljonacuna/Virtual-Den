package com.example.loadingscreen.win.Adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.activity.PendingStudents;
import com.example.loadingscreen.win.model.Students;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PendingStudAdapter extends RecyclerView.Adapter<PendingStudAdapter.PendStudentsViewHolder> {
    ArrayList<usersList_model>pendstudentList;
    Context context;
    DatabaseReference studentsRef;


    public PendingStudAdapter(Context c, ArrayList<usersList_model>s){
        context=c;
        pendstudentList=s;

    }

    @NonNull
    @Override
    public PendStudentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.studentsdesign,parent,false);
        return new PendStudentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendStudentsViewHolder holder, int position) {
        final String studentId=pendstudentList.get(position).getUserID();
        studentsRef= FirebaseDatabase.getInstance().getReference().child("Student");
        final String studentName=pendstudentList.get(position).getFullname();
        final String studentNumber=pendstudentList.get(position).getIdnum();
        holder.studName.setText(pendstudentList.get(position).getFullname());
        Picasso.with(context).load(pendstudentList.get(position).getProfileimg()).resize(40, 40)
                .transform(new CropCircleTransformation()).into(holder.studentImage);
        holder.studentNumber.setText(pendstudentList.get(position).getIdnum());
        holder.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder verifyDialog=new AlertDialog.Builder(context);
                verifyDialog.setMessage("Validate "+studentName+"("+studentNumber+")?");
                verifyDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show();

                    }
                });
                verifyDialog.setNegativeButton("Validate now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        studentsRef.child(studentId).child("isVerified").setValue("Yes");
                        Toast.makeText(context, studentName+" Validated!", Toast.LENGTH_SHORT).show();
                        Intent refresh=new Intent(context, PendingStudents.class);
                        context.startActivity(refresh);

                    }
                });

                AlertDialog verifyDialog1=verifyDialog.create();
                verifyDialog1.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return  pendstudentList.size();
    }

    class PendStudentsViewHolder extends RecyclerView.ViewHolder{
        TextView studName,studentNumber,verifyBtn;
        ImageView studentImage;
        public PendStudentsViewHolder(@NonNull View itemView) {
            super(itemView);
            studName=itemView.findViewById(R.id.studentName);
            studentImage=itemView.findViewById(R.id.studentImage);
            studentNumber=itemView.findViewById(R.id.studentNumber);
            verifyBtn=itemView.findViewById(R.id.verifyBtn);

        }
    }


    public void filterList(ArrayList<usersList_model> filteredList){
        pendstudentList=filteredList;
        notifyDataSetChanged();
    }
}

