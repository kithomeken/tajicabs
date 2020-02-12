package com.tajicabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PassengerSignUp extends FireBase implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "Sign In Activity";

    private EditText firstField;
    private EditText secondField;
    private EditText emailField;
    private EditText passwordField;
    private TextView statusText;
    private Button accountSignUp;
    private TextView accountSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_sign_up);
        mAuth = FirebaseAuth.getInstance();

        firstField = findViewById(R.id.first_name);
        secondField = findViewById(R.id.last_name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        accountSignUp = findViewById(R.id.accountSignUp);
        accountSignIn = findViewById(R.id.accountSignIn);
        statusText = findViewById(R.id.wrong_cred);

        findViewById(R.id.accountSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.accountSignUp) {
            createAccount(emailField.getText().toString(), passwordField.getText().toString(),
                    firstField.getText().toString(), secondField.getText().toString());
        } else if (i == R.id.accountSignIn) {
            Intent intent = new Intent(PassengerSignUp.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }


        String firstName = firstField.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            firstField.setError("Required.");
            valid = false;
        } else {
            firstField.setError(null);
        }

        String secondName = secondField.getText().toString();
        if (TextUtils.isEmpty(secondName)) {
            secondField.setError("Required.");
            valid = false;
        } else {
            secondField.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password, String firstName, String secondName) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PassengerSignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Sign In User Automatically

            Intent intent = new Intent(PassengerSignUp.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
