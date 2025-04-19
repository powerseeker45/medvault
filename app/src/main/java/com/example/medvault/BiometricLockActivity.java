package com.example.medvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricLockActivity extends AppCompatActivity {

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_lock);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean biometricEnabled = prefs.getBoolean("biometric_enabled", false);

        if (!biometricEnabled) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }


        executor = ContextCompat.getMainExecutor(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }

        BiometricManager biometricManager = BiometricManager.from(this);

        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Biometric authentication not supported", Toast.LENGTH_LONG).show();
            finish(); // fallback or exit
        }


        biometricPrompt = new BiometricPrompt(BiometricLockActivity.this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        Toast.makeText(getApplicationContext(), "Access Granted ✅", Toast.LENGTH_SHORT).show();

                        String nextActivity = getIntent().getStringExtra("launchAfter");
                        if ("EncryptedHealthRecordsActivity".equals(nextActivity)) {
                            startActivity(new Intent(BiometricLockActivity.this, HealthRecordsActivity.class));
                        } else {
                            startActivity(new Intent(BiometricLockActivity.this, MainActivity.class));
                        }

                        finish();
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication Failed ❌", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(), "Error: " + errString, Toast.LENGTH_SHORT).show();
                        finish(); // Exit on error
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("MedVault Authentication")
                .setSubtitle("Authenticate to continue")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();


        biometricPrompt.authenticate(promptInfo);
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
