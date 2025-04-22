package com.example.medvault;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

        // Permission checks
        checkAndRequestPermissions();

        // Alarm exact permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                showExactAlarmPermissionDialog();
            }
        }

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

    private void checkAndRequestPermissions() {
        // POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // CAMERA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1002);
        }

        // Storage (Android 6–10)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1003);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void showExactAlarmPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This app needs permission to set exact alarms for medication reminders. Please enable it in settings.")
                .setCancelable(false)
                .setPositiveButton("Allow", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
