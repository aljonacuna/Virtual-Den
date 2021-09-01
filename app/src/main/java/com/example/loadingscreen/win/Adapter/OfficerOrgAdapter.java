package com.example.loadingscreen.win.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.chat;
import com.example.loadingscreen.model.OrgUsers_model;
import com.example.loadingscreen.model.usersList_model;
import com.example.loadingscreen.win.activity.OrgList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.prefs.PreferencesFactory;

public class OfficerOrgAdapter extends RecyclerView.Adapter<OfficerOrgAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OrgUsers_model>ListofUser;
    private String tag;
    public OfficerOrgAdapter(Context context, ArrayList<OrgUsers_model> listofUser,String tag) {
        this.context = context;
        ListofUser = listofUser;
        this.tag = tag;
    }

    @NonNull
    @Override
    public OfficerOrgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orgofficerlistdesign,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfficerOrgAdapter.ViewHolder holder, int position) {
        final OrgUsers_model usersPos = ListofUser.get(position);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference orgUsers1=FirebaseDatabase.getInstance().getReference().child("OrgUsers").child(firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userstbl").child(usersPos.getUserID());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String userType = snapshot.child("userType").getValue().toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(userType).child(usersPos.getUserID());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final String name = snapshot.child("fullname").getValue().toString();
                            String about = snapshot.child("about").getValue().toString();
                            String profileimg = snapshot.child("profileimg").getValue().toString();
                            holder.officerName.setText(name);
                            holder.officerAbout.setText(about);
                            Picasso.with(context).load(profileimg).placeholder(R.drawable.placeholder).into(holder.officerImage);
                            holder.officerType.setText(userType);
                            if (tag.equals("")) {
                                holder.officerRemove.setVisibility(View.VISIBLE);
                                holder.officerRemove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                                        popupMenu.inflate(R.menu.removeofficer);
                                        popupMenu.show();

                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()) {
                                                    case R.id.remove:
                                                        removeOfficer();
                                                        break;
                                                    default:
                                                        return false;
                                                }
                                                return true;
                                            }

                                            private void removeOfficer() {
                                                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(context);
                                                alertDialogBuilder1.setMessage("Remove " + name + "?");

                                                alertDialogBuilder1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(context, "Remove cancelled!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                alertDialogBuilder1.setNegativeButton("Proceed", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        orgUsers1.child(usersPos.getUserID()).child("userID").removeValue();
                                                        Toast.makeText(context, "User removed!", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                                AlertDialog alertDialog1 = alertDialogBuilder1.create();
                                                alertDialog1.show();

                                            }

                                        });

                                    }
                                });
                            }else{
                                holder.officerRemove.setVisibility(View.GONE);
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
        holder.sendPmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = holder.officerName.getText().toString();
                Intent intent = new Intent(context, chat.class);
                intent.putExtra("receiverid", usersPos.getUserID());
                intent.putExtra("receivername", name);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ListofUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView officerName,officerType,officerAbout;
        ImageView officerImage;
        Button sendPmBtn;
        ImageButton officerRemove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            officerName=itemView.findViewById(R.id.userName);
            officerType=itemView.findViewById(R.id.userType);
            officerAbout=itemView.findViewById(R.id.searchDescUser);
            officerImage=itemView.findViewById(R.id.searchImageUser);
            sendPmBtn=itemView.findViewById(R.id.sendMsgOfficerBtn);
            officerRemove=itemView.findViewById(R.id.imageOfficerRemove);
        }
    }
}
