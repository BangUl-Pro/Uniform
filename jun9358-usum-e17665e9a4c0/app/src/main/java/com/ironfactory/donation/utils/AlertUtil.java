package com.ironfactory.donation.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ironfactory.donation.R;

public class AlertUtil {
    private Context context;

    public AlertUtil(Context context) {
        this.context = context;
    }

    public void showMsgAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
