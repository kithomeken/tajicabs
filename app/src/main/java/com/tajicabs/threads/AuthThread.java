package com.tajicabs.threads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

public class AuthThread extends Thread {
    private static final String TAG = AuthThread.class.getName();

    private ProgressDialog progressDialog;
    private Activity activity;
    private String message;

    public AuthThread(Activity activity, String message) {
        this.activity = activity;
        this.message = message;

        Log.e(TAG, "Auth Thread Constructor");
    }

    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "Executing Auth Thread");
                showProgressDialog();
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
