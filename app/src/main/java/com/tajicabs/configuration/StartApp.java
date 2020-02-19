package com.tajicabs.configuration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tajicabs.passengers.PassengerHome;
import com.tajicabs.passengers.SignInActivity;

import static com.tajicabs.configuration.TajiCabs.EMAIL;
import static com.tajicabs.configuration.TajiCabs.IDNUM;
import static com.tajicabs.configuration.TajiCabs.NAMES;
import static com.tajicabs.configuration.TajiCabs.PASSENGER_DETAILS;
import static com.tajicabs.configuration.TajiCabs.PHONE;

public class StartApp extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String TAG = StartApp.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            // Get User Details
            sharedPreferences = getSharedPreferences(PASSENGER_DETAILS, Context.MODE_PRIVATE);

            if (sharedPreferences.contains("EMAIL") && sharedPreferences.contains("NAMES")
                    && sharedPreferences.contains("PHONE")) {

                //TODO: Load Preferences
                EMAIL = sharedPreferences.getString("EMAIL", "");
                NAMES = sharedPreferences.getString("NAMES", "");
                PHONE = sharedPreferences.getString("PHONE", "");
                IDNUM = sharedPreferences.getString("ID_NUM", "");
            }

            // Go To Home
            Intent intent = new Intent(StartApp.this, PassengerHome.class);
            startActivity(intent);
            finish();
        } else{
            // Go To Sign In
            Intent intent = new Intent(StartApp.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}