package com.mirvahidagha.betterbet.Activities;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.mirvahidagha.betterbet.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    ChipGroup chipOthers, chipMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




    }

}
