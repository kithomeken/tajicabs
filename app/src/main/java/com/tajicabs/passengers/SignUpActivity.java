package com.tajicabs.passengers;

import android.content.Context;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tajicabs.R;
import com.tajicabs.configuration.Firebase;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.services.MessagingServices;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

import static com.tajicabs.configuration.TajiCabs.EMAIL;
import static com.tajicabs.configuration.TajiCabs.NAMES;
import static com.tajicabs.configuration.TajiCabs.PASSENGER_DETAILS;
import static com.tajicabs.configuration.TajiCabs.PHONE;

public class SignUpActivity extends Firebase implements View.OnClickListener {
    private static final String TAG = "Sign Up Activity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppDatabase appDatabase;

    private TextView regFailed;
    private static String STATUS;

    private EditText firstText;
    private EditText lastText;
    private EditText idText;
    private EditText phoneText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmText;
    private CheckBox termsAndConditions;

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

        // Database Initialization
        appDatabase = AppDatabase.getDatabase(this);

        mAuth = FirebaseAuth.getInstance();
        regFailed = findViewById(R.id.regFailed);
        termsAndConditions = findViewById(R.id.termsAndCondition);

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

       String confirmPwd = confirmText.getText().toString();
       if (TextUtils.isEmpty(confirmPwd)) {
           confirmText.setError("Required.");
           valid = false;
       } else {
           confirmText.setError(null);
       }

       if (!password.equals(confirmPwd)) {
           passwordText.setError("Passwords Do Not Match");
           valid = false;
       } else {
           passwordText.setError(null);
       }

       if (!termsAndConditions.isChecked()) {
           valid = false;
           Toast.makeText(this, "Kindly accept the terms and conditions to continue", Toast.LENGTH_LONG).show();
       }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.i(TAG, "Account Sign Up :" + email);
        if (!validateForm()) {
            return;
        }

        final Button signUpButton = findViewById(R.id.accountSignUp);
        signUpButton.setVisibility(View.GONE);

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign up success, update UI with the signed-in user's information
                Log.i(TAG, "createUserWithEmail:success");

                // Registration Status
                registerTokens registerTokens = new registerTokens(appDatabase);
                registerTokens.execute();
            } else {
                // If sign in fails, display a message to the user.
                Log.i(TAG, "createUserWithEmail:failure", task.getException());

                regFailed.setVisibility(View.VISIBLE);
                regFailed.setText(task.getException().getLocalizedMessage());
                updateUI(null);

                signUpButton.setVisibility(View.VISIBLE);
            }

            if (!task.isSuccessful()) {
                regFailed.setText(task.getException().getLocalizedMessage());

                signUpButton.setVisibility(View.VISIBLE);
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

    public String email() {
        return emailText.getText().toString();
    }

    public String firstName() {
        return firstText.getText().toString();
    }

    public String lastName() {
        return lastText.getText().toString();
    }

    public String phoneNumber() {
        return phoneText.getText().toString();
    }

    private class registerTokens extends AsyncTask<Void, Void, Void> {
        AppDatabase appDatabase;

        public registerTokens(AppDatabase appDatabase) {
            this.appDatabase = appDatabase;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG, "Firebase Instance Created");
            STATUS = "Success";

            // Firebase Messaging Token Registration
            FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Getting Firebase InstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Log.i(TAG, "Firebase InstanceId Token: " +  token);

                    String email = email();
                    String firstName = firstName();
                    String lastName = lastName();
                    String phoneNumber = phoneNumber();

                    NAMES = firstName + " " + lastName;
                    EMAIL = email;
                    PHONE = phoneNumber;

                    // Initialize Read Write Application Database Services
                    RWServices rwServices = new RWServices(appDatabase);
                    rwServices.createUser(email, firstName, lastName, phoneNumber, token);

                    MessagingServices messagingService =  new MessagingServices();
                    Context context = getApplicationContext();
                    messagingService.onNewToken(token, context);

                    // Finish Sign In Activity
                    Intent intent = new Intent(SignUpActivity.this, PassengerHome.class);
                    startActivity(intent);
                    finish();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
