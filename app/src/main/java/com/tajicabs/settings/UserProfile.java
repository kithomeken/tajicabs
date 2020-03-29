package com.tajicabs.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tajicabs.R;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.global.Variables;

import static com.tajicabs.settings.ChangeUserProfile.USER_PROFILE_DIALOG;

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

        final TextView accountFirstName, accountLastName, accountPhone, emailAccount;

        accountFirstName = findViewById(R.id.accountFName);
        accountLastName = findViewById(R.id.accountLastName);
        accountPhone = findViewById(R.id.accountPhone);
        emailAccount = findViewById(R.id.emailAccount);

        accountFirstName.setText(firstName);
        accountLastName.setText(lastName);
        accountPhone.setText(phoneNumber);
        emailAccount.setText(emailAdd);

        RelativeLayout firstNameLayout;

        firstNameLayout = findViewById(R.id.firstNameLayout);
        firstNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.ACTIVITY_STATE == 0) {
                    Variables.ACTIVITY_STATE = 1;
                    Context context = getApplicationContext();

                    String title = "";
                    String data = accountFirstName.getText().toString();
                    int category = 1;

                    DialogFragment dialogFragment = ChangeUserProfile.newInstance(title, data, category);
                    dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), USER_PROFILE_DIALOG);
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
