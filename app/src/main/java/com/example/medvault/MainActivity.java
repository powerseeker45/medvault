package com.example.medvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    CardView cardRecords, cardReminders, cardAppointments, cardFamily, cardEmergency, cardProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("MedVault");


        cardRecords = findViewById(R.id.cardRecords);
        cardReminders = findViewById(R.id.cardReminders);
        cardAppointments = findViewById(R.id.cardAppointments);
        cardFamily = findViewById(R.id.cardFamily);
        cardEmergency = findViewById(R.id.cardEmergency);
        cardProfile = findViewById(R.id.cardProfile);

        cardRecords.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            boolean biometricEnabled = prefs.getBoolean("biometric_enabled", false);

            if (biometricEnabled) {
                Intent intent = new Intent(MainActivity.this, BiometricLockActivity.class);
                intent.putExtra("launchAfter", "EncryptedHealthRecordsActivity");
                startActivity(intent);
            } else {
                startActivity(new Intent(MainActivity.this, HealthRecordsActivity.class));
            }
        });
        cardReminders.setOnClickListener(v -> startActivity(new Intent(this, MedicationReminderActivity.class)));
        cardAppointments.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            boolean biometricEnabled = prefs.getBoolean("biometric_enabled", false);

            if (biometricEnabled) {
                Intent intent = new Intent(MainActivity.this, BiometricLockActivity.class);
                intent.putExtra("launchAfter", "AppointmentsActivity");
                startActivity(intent);
            } else {
                startActivity(new Intent(MainActivity.this, AppointmentActivity.class));
            }
        });
        cardFamily.setOnClickListener(v -> startActivity(new Intent(this, FamilyActivity.class)));
        cardEmergency.setOnClickListener(v -> startActivity(new Intent(this, EmergencyQRActivity.class)));
        cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_logout) {
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Show logout confirmation dialog
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    // Handle logout
    private void logout() {
        // Clear any saved user data (e.g., shared preferences or session)
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Navigate back to Login screen
        startActivity(new Intent(this, LoginActivity.class)); // Or SplashActivity if needed
        finish();
    }

}
