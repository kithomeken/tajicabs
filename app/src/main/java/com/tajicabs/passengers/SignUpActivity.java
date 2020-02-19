package com.tajicabs.passengers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tajicabs.R;
import com.tajicabs.configuration.Firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static com.tajicabs.configuration.TajiCabs.PASSENGER_DETAILS;

public class SignUpActivity extends Firebase implements View.OnClickListener {
    private static final String TAG = "Sign Up Activity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView regFailed;
    private static String STATUS;

    private EditText firstText;
    private EditText lastText;
    private EditText idText;
    private EditText phoneText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmText;

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
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        regFailed = findViewById(R.id.regFailed);

        firstText = findViewById(R.id.first_name);
        lastText = findViewById(R.id.last_name);
        idText = findViewById(R.id.id_number);
        phoneText = findViewById(R.id.phone_number);
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        confirmText = findViewById(R.id.confirm_password);

        findViewById(R.id.accountSignIn).setOnClickListener(this);
        findViewById(R.id.accountSignUp).setOnClickListener(this);

        // Firestore DB
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();

        FirebaseFirestoreSettings cacheSettings = new FirebaseFirestoreSettings.Builder()
        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        .build();

        db.setFirestoreSettings(settings);
        db.setFirestoreSettings(cacheSettings);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.accountSignUp) {
            hideKeyboard(v);

            // Create User Account
            createAccount(emailText.getText().toString(), passwordText.getText().toString());
        } else if (i == R.id.accountSignIn) {
            hideKeyboard(v);

            // Return to Sign In Page
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstName = firstText.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            firstText.setError("Required.");
            valid = false;
        } else {
            firstText.setError(null);
        }

        String lastName = lastText.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            lastText.setError("Required.");
            valid = false;
        } else {
            lastText.setError(null);
        }

        String idNumber = idText.getText().toString();
        if (TextUtils.isEmpty(idNumber)) {
            idText.setError("Required.");
            valid = false;
        } else {
            idText.setError(null);
        }

        String phoneNumber = phoneText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneText.setError("Required.");
            valid = false;
        } else {
            phoneText.setError(null);
        }

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

//        String confirmPwd = confirmText.getText().toString();
//        if (TextUtils.isEmpty(confirmPwd)) {
//            confirmText.setError("Required.");
//            valid = false;
//        } else {
//            confirmText.setError(null);
//        }
//
//        if (!password.equals(confirmPwd)) {
//            passwordText.setError("Passwords Do Not Match");
//            valid = false;
//        } else {
//            passwordText.setError(null);
//        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.i(TAG, "Account Sign Up :" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign up success, update UI with the signed-in user's information
                    Log.i(TAG, "createUserWithEmail:success");

                    // Add user's Details to Firestore DB
                    // Collection - passengers
                    String email = email();
                    String firstName = firstName();
                    final String lastName = lastName();
                    String strId = idNumber();
                    String phoneNumber = phoneNumber();

                    int idNumber = Integer.parseInt(strId);
                    CollectionReference passengers = db.collection("passengers");

                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("email", email);
                    userDetails.put("first_name", firstName);
                    userDetails.put("last_name", lastName);
                    userDetails.put("id_number", idNumber);
                    userDetails.put("phone_number", phoneNumber);

                    passengers.document(email).set(userDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete() || task.isSuccessful()) {
                                Log.i(TAG, "Passenger Data Created");
                                STATUS = "Success";

                                SharedPreferences sharedPreferences;

                                String email = emailText.getText().toString();
                                String phone = phoneText.getText().toString();
                                String idNum = idText.getText().toString();
                                String names = firstText.getText().toString() + " " + lastText.getText().toString();

                                sharedPreferences = getApplicationContext().getSharedPreferences(PASSENGER_DETAILS, 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("EMAIL", email);
                                editor.putString("PHONE", phone);
                                editor.putString("ID_NUM", idNum);
                                editor.putString("NAMES", names);
                                editor.apply();

                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                updateUI(firebaseUser);
                            } else {
                                Log.i(TAG, "Something went wrong: " + task.getException());
                                STATUS = "Failed";

                                updateUI(null);
                            }
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "createUserWithEmail:failure", task.getException());

                    regFailed.setVisibility(View.VISIBLE);
                    regFailed.setText(task.getException().getMessage());
                    updateUI(null);
                }

                if (!task.isSuccessful()) {
                    regFailed.setText(task.getException().toString());
                }

                hideProgressDialog();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();

        if (user != null) {
            // Sign In User Automatically
            Intent intent = new Intent(SignUpActivity.this, PassengerHome.class);
            startActivity(intent);
            finish();

        }
    }

    private String email() {
        return emailText.getText().toString();
    }

    private String firstName() {
        return firstText.getText().toString();
    }

    private String lastName() {
        return lastText.getText().toString();
    }

    private String idNumber() {
        return idText.getText().toString();
    }

    private String phoneNumber() {
        return phoneText.getText().toString();
    }
}
