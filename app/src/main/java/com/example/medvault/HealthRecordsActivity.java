package com.example.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.medvault.RecordAdapter;
import com.example.medvault.AppDatabase;
import com.example.medvault.HealthRecord;

import java.util.List;

public class HealthRecordsActivity extends AppCompatActivity {

    ListView listView;
    List<HealthRecord> records;
    RecordAdapter adapter;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_records);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }

        listView = findViewById(R.id.recordsList);

        db = AppDatabaseInstance.getInstance(this);

        try {
            EncryptionUtil.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAndDisplayRecords(); // Initial load
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    public void openAddRecord(View view) {
        startActivity(new Intent(this, AddRecordActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayRecords();
    }

    private void loadAndDisplayRecords() {
        records = db.healthRecordDao().getAllRecords();

        for (HealthRecord record : records) {
            try {
                record.title = EncryptionUtil.decrypt(record.title);
                record.notes = EncryptionUtil.decrypt(record.notes);
                record.date = EncryptionUtil.decrypt(record.date);
            } catch (Exception e) {
                record.title = "Error decrypting";
                record.notes = "Error decrypting";
                record.date = "Error decrypting";
            }
        }

        adapter = new RecordAdapter(this, records);
        listView.setAdapter(adapter);
    }

}
