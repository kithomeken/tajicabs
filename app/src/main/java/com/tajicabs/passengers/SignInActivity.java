package com.tajicabs.passengers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tajicabs.R;
import com.tajicabs.configuration.Firebase;


public class SignInActivity extends Firebase implements View.OnClickListener {
    private static final String TAG = "Sign In Activity";

    private FirebaseAuth mAuth;
    private TextView authFailed;
    private EditText emailText;
    private EditText passwordText;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        authFailed = findViewById(R.id.authFailed);
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);

        findViewById(R.id.accountSignIn).setOnClickListener(this);
        findViewById(R.id.accountSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.accountSignUp) {
            hideKeyboard(v);

            // Navigate to Create Account Page
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        } else if (i == R.id.accountSignIn) {
            hideKeyboard(v);

            // Sign In User
            signIn(emailText.getText().toString(), passwordText.getText().toString());
        }
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Required.");
            valid = false;
        } else {
            emailText.setError(null);
        }

        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordText.setError("Required.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "Firebase Signing In: success");

                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "Firebase Signing In: Failed", task.getException());

//                    Toast.makeText(SignInActivity.this, "Taji Account Authentication failed.",
//                            Toast.LENGTH_SHORT).show();
                    authFailed.setVisibility(View.VISIBLE);

                    updateUI(null);
                }

                if (!task.isSuccessful()) {
                    authFailed.setText(R.string.auth_failed);
                }

                hideProgressDialog();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Sign In User Automatically
            Intent intent = new Intent(SignInActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();

            hideProgressDialog();
        }
    }
}
