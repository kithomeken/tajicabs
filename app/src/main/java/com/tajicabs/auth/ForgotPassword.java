package com.tajicabs.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tajicabs.R;
import com.tajicabs.configuration.Firebase;
import com.tajicabs.global.Variables;
import com.tajicabs.threads.AuthThread;

import java.util.Objects;

public class ForgotPassword extends Firebase {
    private static String TAG = ForgotPassword.class.getName();

    private AuthThread authThread;
    private Handler handler = new Handler();

    private EditText accountEmail;
    private TextView errorText;
    private RelativeLayout authFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_forgot_password);

        authThread = new AuthThread(ForgotPassword.this, "Recovering Taji Cabs Account");
        Variables.ACTIVITY_STATE = 0;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        accountEmail = findViewById(R.id.accountEmail);
        errorText = findViewById(R.id.accountError);
        authFailed = findViewById(R.id.authFailed);

        Button forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    hideKeyboard(v);
                    threadTrial();

                    String email = email();
                    resetPassword(email);
                }
            }
        });
    }

    private String email() {
        return accountEmail.getText().toString();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = accountEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            accountEmail.setError("Required.");
            valid = false;
        } else {
            accountEmail.setError(null);
        }

        return valid;
    }

    private void resetPassword (String email) {
        if (!validateForm()) {
            return;
        }

        Variables.ACTIVITY_STATE = 1;

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    authFailed.setVisibility(View.VISIBLE);
                    errorText.setText(getString(R.string.recovery_success));
                    errorText.setTextColor(Color.parseColor("#22C114"));
                    Log.d(TAG, "Email sent.");

                    Variables.ACTIVITY_STATE = 0;
                    authThread.hideProgressDialog();
                }

                authFailed.setVisibility(View.VISIBLE);
                errorText.setText(Objects.requireNonNull(task.getException()).getLocalizedMessage());

                Variables.ACTIVITY_STATE = 0;
                authThread.hideProgressDialog();
            }
        });
    }

    public void threadTrial() {
        // Update the progress bar
        Thread mainThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                handler.post(new Runnable() {
                    public void run() {
                        if (!interrupted()) {
                            authThread.run();
                            Log.d(TAG, "Running Auth Thread");
                        }
                    }
                });
            }
        };

        mainThread.start();
    }
}
