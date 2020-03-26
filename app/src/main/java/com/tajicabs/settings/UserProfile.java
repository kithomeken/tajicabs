package com.tajicabs.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.tajicabs.R;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.global.Variables;

public class UserProfile extends AppCompatActivity {
    private RWServices rwServices;
    private TextView accountName, accountEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        rwServices = new RWServices(appDatabase);

        String firstName = rwServices.getFirstName();
        String lastName = rwServices.getLastName();
        String phoneNumber = rwServices.getPhoneNumber();
        String emailAdd = rwServices.getEmailAdd();
        String stringName = firstName + " " + lastName;

        accountName = findViewById(R.id.accountName);
        accountEmail = findViewById(R.id.accountEmail);

        accountName.setText(stringName);
        accountEmail.setText(emailAdd);

        TextView accountFirstName, accountLastName, accountPhone, emailAccount;

        accountFirstName = findViewById(R.id.accountFName);
        accountLastName = findViewById(R.id.accountLastName);
        accountPhone = findViewById(R.id.accountPhone);
        emailAccount = findViewById(R.id.emailAccount);

        accountFirstName.setText(firstName);
        accountLastName.setText(lastName);
        accountPhone.setText(phoneNumber);
        emailAccount.setText(emailAdd);
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
