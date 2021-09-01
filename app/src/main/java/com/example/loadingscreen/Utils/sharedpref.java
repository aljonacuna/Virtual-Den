package com.example.loadingscreen.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.loadingscreen.R;
import com.example.loadingscreen.activity_gui.signin;
import com.example.loadingscreen.win.activity.AdminPanel;
import com.example.loadingscreen.win.activity.UpdateEmail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.example.loadingscreen.activity_gui.signin.emailAdd;
import static com.example.loadingscreen.activity_gui.signin.fileName;
import static com.example.loadingscreen.activity_gui.signin.fileName_usertype;
import static com.example.loadingscreen.activity_gui.signin.password;
import static com.example.loadingscreen.activity_gui.signin.userTypeSP;


public class sharedpref {
    public static String user_type_get = "";
    public static String userType = "";
    public static String schoolyear = "";
    public static String sems = "";
    public static String orgRef = "Organizations";
    public static SharedPreferences preferences;
    public static String usertype;
    public static FirebaseAuth firebaseAuth;
    public static SharedPreferences removeDatapref;
    public static SharedPreferences logoutpreferences;


    public static String spUsertype(Context context) {
        preferences = context.getSharedPreferences(fileName_usertype, Context.MODE_PRIVATE);
        usertype = preferences.getString(userTypeSP, "");
        return usertype;
    }

    public static void logout(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        logoutpreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        preferences = context.getSharedPreferences(fileName_usertype, Context.MODE_PRIVATE);
        SharedPreferences.Editor uteditor = preferences.edit();
        uteditor.remove(userTypeSP);
        uteditor.commit();
        SharedPreferences.Editor editor = logoutpreferences.edit();
        editor.remove(emailAdd);
        editor.remove(signin.signup);
        editor.commit();
        firebaseAuth.signOut();
        Intent intent = new Intent(context, signin.class);
        context.startActivity(intent);
        ((Activity) context).finishAffinity();
    }

    public static void adminReauth(final Context context, final ProgressDialog progressDialog) throws GeneralSecurityException, IOException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog_ui);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            String email = saveAdminEncryptedData(context).getString(emailAdd, "");
            String pass = saveAdminEncryptedData(context).getString(password, "");
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(context,AdminPanel.class);
                            context.startActivity(intent);
                            ((Activity)context).finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Fail: " + e.toString() + "Please re-login", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Intent intent = new Intent(context,AdminPanel.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }

    public static SharedPreferences saveAdminEncryptedData(Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                fileName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        return sharedPreferences;
    }
}
