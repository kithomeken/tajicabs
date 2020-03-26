package com.tajicabs.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tajicabs.R;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.global.Variables;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Variables.ACTIVITY_STATE = 0;
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        RWServices rwServices = new RWServices(appDatabase);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView privacyPolicy, profileCard;
        privacyPolicy = findViewById(R.id.privacyPolicy);
        profileCard = findViewById(R.id.profileCard);

        TextView accountName, accountEmail;
        accountName = findViewById(R.id.accountName);
        accountEmail = findViewById(R.id.accountEmail);

        String firstName = rwServices.getFirstName();
        String lastName = rwServices.getLastName();
        String emailAdd = rwServices.getEmailAdd();
        String stringAccountName = firstName + " " + lastName;

        accountEmail.setText(emailAdd);
        accountName.setText(stringAccountName);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    Variables.ACTIVITY_STATE = 1;

                    Intent intent = new Intent(Settings.this, PrivacyPolicy.class);
                    startActivity(intent);
                }
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    Variables.ACTIVITY_STATE = 1;

                    Intent intent = new Intent(Settings.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Variables.ACTIVITY_STATE = 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Variables.ACTIVITY_STATE = 0;
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
