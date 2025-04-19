package com.example.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;

public class SettingsActivity extends AppCompatActivity {

    Switch switchBiometric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Settings");

        // Enable the back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switchBiometric = findViewById(R.id.switchBiometric);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean biometricEnabled = prefs.getBoolean("biometric_enabled", false);
        switchBiometric.setChecked(biometricEnabled);

        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("biometric_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Biometric Enabled" : "Biometric Disabled", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}
