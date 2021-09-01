package com.example.loadingscreen.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.loadingscreen.R;

import static com.example.loadingscreen.Utils.sharedpref.schoolyear;
import static com.example.loadingscreen.Utils.sharedpref.sems;

public class arrfordd {
    public static String[] syarr = new String[]
            {schoolyear};
    public static String[] semarr = new String[]
            {sems};
    public static Dialog dialog;

    public static ArrayAdapter<String> syadapter(Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.dropdownlayout, syarr);
        return adapter;
    }

    public static ArrayAdapter<String> semadapter(Context context) {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context, R.layout.dropdownlayout, semarr);
        return adapter1;
    }

    public static ArrayAdapter<String> status(Context context) {
        String[] statusarray = new String[]{
                "Lost",
                "Found"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdownlayout, statusarray);
        return arrayAdapter;
    }

    public static void dialog_conf(final Context context, String title_dialog,
                                   String msg_dialog,String msg_detail,String msg_dialog1,
                                   View.OnClickListener listener) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirmation_lessdetail);
        Button cancelbtn = dialog.findViewById(R.id.cancelbtn_dialog);
        Button confbtn = dialog.findViewById(R.id.confirmbtn_dialog);
        TextView title = dialog.findViewById(R.id.confirm_dialog_title);
        TextView msg = dialog.findViewById(R.id.confirm_dialog_msg);
        confbtn.setOnClickListener(listener);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        if (msg_detail.equals("") && msg_dialog1.equals("")){
            msg.setText(msg_dialog);
        }
        else{
            String new_msg_dialog = msg_dialog+" "+"<font color=\"#4B8BBE\">"+msg_detail+"</font>"+msg_dialog1;
            msg.setText(Html.fromHtml(new_msg_dialog), TextView.BufferType.SPANNABLE);
        }
        title.setText(title_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
