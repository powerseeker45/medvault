package com.example.medvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        setTitle("Profile");

        // Enable the back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Initialize views
        nameTextView = findViewById(R.id.textViewName);
        emailTextView = findViewById(R.id.textViewEmail);

        // Get saved email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MedVaultPrefs", MODE_PRIVATE);
        String email = prefs.getString("user_email", null);

        if (email != null) {
            AppDatabase db = AppDatabaseInstance.getInstance(this); // Use your singleton
            userDao = db.userDao();

            new Thread(() -> {
                User user = userDao.getUserByEmail(email);
                if (user != null) {
                    runOnUiThread(() -> {
                        nameTextView.setText(user.name);
                        emailTextView.setText(user.email);
                    });
                }
            }).start();
        }
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
