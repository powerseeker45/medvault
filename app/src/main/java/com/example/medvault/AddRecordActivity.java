package com.example.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.medvault.AppDatabase;
import com.example.medvault.HealthRecord;
import com.example.medvault.EncryptionUtil;

public class AddRecordActivity extends AppCompatActivity {

    EditText titleInput, notesInput, dateInput;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        titleInput = findViewById(R.id.titleInput);
        notesInput = findViewById(R.id.notesInput);
        dateInput = findViewById(R.id.dateInput);

        db = AppDatabaseInstance.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // This shows default back arrow
            getSupportActionBar().setTitle("Health Records"); // Optional: Set title programmatically
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Navigate to MainActivity (Dashboard)
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }


    public void saveRecord(View view) {
        try {
            String title = EncryptionUtil.encrypt(titleInput.getText().toString());
            String notes = EncryptionUtil.encrypt(notesInput.getText().toString());
            String date = EncryptionUtil.encrypt(dateInput.getText().toString());

            HealthRecord record = new HealthRecord();
            record.title = title;
            record.notes = notes;
            record.date = date;

            db.healthRecordDao().insert(record);
            Toast.makeText(this, "Record saved securely!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Encryption failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
