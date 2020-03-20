package com.tajicabs.passengers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tajicabs.R;
import com.tajicabs.configuration.Firebase;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.database.UserDetails;
import com.tajicabs.database.UserDetailsDao;

import java.util.Objects;

import static com.tajicabs.configuration.TajiCabs.EMAIL;
import static com.tajicabs.configuration.TajiCabs.FNAME;
import static com.tajicabs.configuration.TajiCabs.LNAME;
import static com.tajicabs.configuration.TajiCabs.PHONE;

public class ProfileActivity extends Firebase {
    private static final String TAG = ProfileActivity.class.getName();
    private AppDatabase appDatabase;
    private EditText accountFName, accountLName, accountEmail, accountPhone;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        accountEmail = findViewById(R.id.accountEmail);
        accountFName = findViewById(R.id.accountFName);
        accountLName = findViewById(R.id.accountLName);
        accountPhone = findViewById(R.id.accountPhone);
        Button saveChanges = findViewById(R.id.saveChanges);

        // Application Database Initialization
        appDatabase = AppDatabase.getDatabase(this);
        getUserDetails();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showProgressDialog();

                String email = email();
                String firstName = firstName();
                String lastName = lastName();
                String phoneNumber = phoneNumber();

                updateDetails(firstName, lastName, email, phoneNumber);
            }
        });
    }

    private void getUserDetails() {
        RWServices rwServices = new RWServices(appDatabase);
        rwServices.getUserDetails();

        accountPhone.setText(PHONE);
        accountEmail.setText(EMAIL);
        accountFName.setText(FNAME);
        accountLName.setText(LNAME);
    }

    private String firstName() {
        return accountFName.getText().toString();
    }

    private String lastName() {
        return accountLName.getText().toString();
    }

    private String email() {
        return accountEmail.getText().toString();
    }

    private String phoneNumber() {
        return accountPhone.getText().toString();
    }

    private void updateDetails(String firstName, String lastName, String email, String phoneNumber) {
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        UserDetailsDao userDetailsDao = appDatabase.userDetailsDao();

        UserDetails userDetails = userDetailsDao.getUserDetails();
        if (userDetails != null) {
            userDetails.first_name = firstName;
            userDetails.last_name = lastName;
            userDetails.email = email;
            userDetails.phone_number = phoneNumber;

            userDetailsDao.updateUserDetails(userDetails);

            Toast.makeText(getApplicationContext(), "Account Details Updated Successfully", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getApplicationContext(), "Account Details Updated Failed", Toast.LENGTH_LONG).show();
        }

        RWServices rwServices = new RWServices(appDatabase);
        rwServices.getUserDetails();
        hideProgressDialog();
    }
}
