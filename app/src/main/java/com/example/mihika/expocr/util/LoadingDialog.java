package com.example.mihika.expocr.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mihika.expocr.R;

public class LoadingDialog {

    public static Dialog showDialog(Context context, String msg){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_loading_view);

        TextView dialogText = (TextView) v.findViewById(R.id.dialog_loading_text);
        dialogText.setText(msg);

        Dialog dialog = new Dialog(context, 0);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public static void closeDialog(Dialog dialog){
        if(dialog != null & dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
