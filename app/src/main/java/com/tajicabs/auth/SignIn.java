package com.tajicabs.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.tajicabs.R;
import com.tajicabs.app.OnBoardingUI;
import com.tajicabs.app.Welcome;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.UserDetails;
import com.tajicabs.database.UserDetailsDao;
import com.tajicabs.global.Constants;
import com.tajicabs.global.Variables;
import com.tajicabs.threads.AuthThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SignIn extends AppCompatActivity {
    private static final String TAG = SignIn.class.getName();

    private EditText accountEmail, accountPassword;
    private RelativeLayout authFailed;
    private TextView accountError;
    private Button accountSignIn;

    private FirebaseAuth firebaseAuth;
    private Context context;
    private UserDetailsDao userDetailsDao;

    private AuthThread authThread;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_sign_in);

        Variables.ACTIVITY_STATE = 0;
        firebaseAuth = FirebaseAuth.getInstance();
        authThread = new AuthThread(SignIn.this, "Authenticating...");

        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        context = getApplicationContext();
        userDetailsDao = appDatabase.userDetailsDao();

        // Relative Layout Declarations
        relativeLayoutDeclaration();

        authFailed = findViewById(R.id.authFailed);
        authFailed.setVisibility(View.GONE);

        accountEmail = findViewById(R.id.accountEmail);
        accountError = findViewById(R.id.accountError);
        accountSignIn = findViewById(R.id.accountSignIn);
        accountPassword = findViewById(R.id.accountPassword);

        accountSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    hideKeyboard(v);
                    passengerSignIn();
                }
            }
        });
    }

    private void relativeLayoutDeclaration() {
        RelativeLayout signUpLink = findViewById(R.id.signUpLink);
        RelativeLayout forgotLink = findViewById(R.id.forgotPasswordLink);

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    Variables.ACTIVITY_STATE = 1;

                    Intent intent = new Intent(getApplicationContext(), SignUp.class);
                    startActivity(intent);
                }
            }
        });

        forgotLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    Variables.ACTIVITY_STATE = 1;

                    Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean valid = true;

        String email = accountEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            accountEmail.setError("Required.");
            valid = false;
        } else {
            accountEmail.setError(null);
        }

        String password = accountPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            accountPassword.setError("Required.");
            valid = false;
        } else {
            accountPassword.setError(null);
        }

        return valid;
    }

    private String accountEmail() {
        return accountEmail.getText().toString().trim();
    }

    private String accountPassword() {
        return accountPassword.getText().toString().trim();
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void passengerSignIn() {
        if (!validateInputs()) {
            return;
        }

        Variables.ACTIVITY_STATE = 1;
        authFailed.setVisibility(View.GONE);
        threadTrial();

        final String email = accountEmail();
        String password = accountPassword();

        firebaseAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Firebase Signing In: success");

                    FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.e(TAG, " getInstanceId failed", task.getException());

                                Variables.ACTIVITY_STATE = 0;
                                accountSignIn.setVisibility(View.VISIBLE);
                                authThread.hideProgressDialog();
                                return;
                            }

                            String firebaseToken = Objects.requireNonNull(task.getResult()).getToken();

                            // Get Account Details
                            accountDetails(firebaseToken);
                        }
                    });
                } else {
                    authFailed.setVisibility(View.VISIBLE);
                    accountError.setText(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    Log.e(TAG, "Authentication Failed: " + task.getException().getLocalizedMessage());
                }

                if (!task.isSuccessful()) {
                    authFailed.setVisibility(View.VISIBLE);
                    accountError.setText(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    Log.e(TAG, "Authentication Failed: " + task.getException().getMessage());
                }

                Variables.ACTIVITY_STATE = 0;
                accountSignIn.setVisibility(View.VISIBLE);
                authThread.hideProgressDialog();
            }
        });
    }

    private void accountDetails(final String firebaseToken) {
        String stringUrl = Constants.API_HEADER + Constants.FETCH_ACCOUNT_DETAILS + "?email=" + accountEmail();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, stringUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(TAG, "JSON Object: " + jsonObject);

                            String emailAdd = jsonObject.getString("email");
                            String firstName = jsonObject.getString("first_name");
                            String lastName = jsonObject.getString("last_name");
                            String phoneNumber = jsonObject.getString("phone_number");

                            // Add Entries to Application Database
                            createUser(emailAdd, firstName, lastName, phoneNumber, firebaseToken);

                            // Update Firebase Token
                            updateFirebaseToken(firebaseToken, emailAdd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "STACKTRACE: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        FirebaseAuth.getInstance().signOut();

                        authFailed.setVisibility(View.VISIBLE);
                        String failedSignUp = "Error creating Taji Cabs account. Something went wrong. \nTry Again Later";
                        accountError.setText(failedSignUp);

                        Variables.ACTIVITY_STATE = 0;
                        // End Main Thread
                        authThread.hideProgressDialog();
                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void createUser(@NonNull String email, @NonNull String first_name,
        @NonNull String last_name, @NonNull String phone_number, @NonNull String firebaseToken) {

        final String userId = UUID.randomUUID().toString();
        UserDetails userDetails = new UserDetails(userId, email, first_name, last_name,
                phone_number, firebaseToken);

        userDetailsDao.createNewUser(userDetails);
        userDetailsDao.getUserDetails();
    }

    private void updateFirebaseToken(final String firebaseToken, final String emailAdd) {
        // Update Firebase Token on Sign In
        RequestQueue queue = Volley.newRequestQueue(context);
        String tajiUrl = Constants.API_HEADER + Constants.UPDATE_FIREBASE_TOKEN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tajiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "RESPONSE: " + response);

                        // Sign In the User
                        finishSignIn();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "VOLLEY ERROR: " + error.getMessage());
                FirebaseAuth.getInstance().signOut();

                authFailed.setVisibility(View.VISIBLE);
                String failedSignUp = "Error creating Taji Cabs account. Something went wrong. \nTry Again Later";
                accountError.setText(failedSignUp);

                Variables.ACTIVITY_STATE = 0;
                // End Main Thread
                authThread.hideProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", firebaseToken);
                params.put("email", emailAdd);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void finishSignIn(){
        Intent intent = new Intent(SignIn.this, OnBoardingUI.class);
        startActivity(intent);

        authThread.hideProgressDialog();
        Variables.ACTIVITY_STATE = 0;
        finish();
    }

    public void threadTrial() {
        // Update the progress bar
        Thread mainThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                // Update the progress bar
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
