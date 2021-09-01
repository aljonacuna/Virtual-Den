package com.example.loadingscreen.win.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.lang.UScript;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.model.UserList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    ArrayList<usersList_model> userList;
    Context context;
    DatabaseReference orgUsers;
    FirebaseAuth myAuth;
    String currentOrg;

    public UserListAdapter(Context c, ArrayList<usersList_model> u) {
        context = c;
        userList = u;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.userlistdesign, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        usersList_model userPos = userList.get(position);
        final String userId = userPos.getUserID();
        final String userName = userPos.getFullname();
        final String userImage = userPos.getProfileimg();
        final String userType = userPos.getUserType();


        holder.userName.setText(userName);
        holder.userType.setText(userType);
        holder.userAbout.setText(userPos.getAbout());
        Picasso.with(context).load(userImage).resize(150, 150)
                .transform(new CropCircleTransformation()).into(holder.userImage);
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAuth = FirebaseAuth.getInstance();
                currentOrg = myAuth.getCurrentUser().getUid();
                orgUsers = FirebaseDatabase.getInstance().getReference().child("OrgUsers");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Add " + userName + "?");
                alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentOrg.equals(userId)) {
                            Toast.makeText(context, "Sorry, You can't add Organization!", Toast.LENGTH_SHORT).show();
                        } else {
                            orgUsers.child(currentOrg).child(userId).child("userID").setValue(userId);
                            Toast.makeText(context, userName + " added to the group!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Cancelled to add " + userName, Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                ;


            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, userType, userAbout;
        private Button addButton;
        private ImageView userImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userType = itemView.findViewById(R.id.userType);
            userAbout = itemView.findViewById(R.id.searchDescUser);
            userImage = itemView.findViewById(R.id.searchImageUser);
            addButton = itemView.findViewById(R.id.addOfficerBtn);

        }
    }

    public void filterList(ArrayList<usersList_model> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }
}
