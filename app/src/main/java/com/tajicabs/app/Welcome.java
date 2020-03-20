package com.tajicabs.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tajicabs.R;
import com.tajicabs.auth.SignIn;
import com.tajicabs.auth.SignUp;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_welcome);
        Button signIn, signUp;

        signIn = findViewById(R.id.SignIn);
        signUp = findViewById(R.id.SignUp);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });
    }
}
