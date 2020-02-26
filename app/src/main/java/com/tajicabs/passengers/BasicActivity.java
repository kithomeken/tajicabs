package com.tajicabs.passengers;

import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tajicabs.R;

public class BasicActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textViewBottomSheetState;
    Button toggleBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {

    }
}
