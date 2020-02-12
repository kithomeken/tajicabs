package com.tajicabs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartUp extends AppCompatActivity {
    private CardView driverCard;
    private CardView passengerCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        driverCard = findViewById(R.id.driver);
        passengerCard = findViewById(R.id.passenger);

        driverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartUp.this, DriverSignIn.class);
                startActivity(intent);
            }
        });

        passengerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartUp.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
