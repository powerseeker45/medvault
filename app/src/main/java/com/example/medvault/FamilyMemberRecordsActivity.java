package com.example.medvault;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class FamilyMemberRecordsActivity extends AppCompatActivity {

    int memberId;
    AppDatabase db;
    ListView listView;
    EditText titleInput, notesInput, dateInput;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_records);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Family Member Records");
        }

        memberId = getIntent().getIntExtra("memberId", -1);
        db = AppDatabaseInstance.getInstance(this);

        titleInput = findViewById(R.id.titleInput);
        notesInput = findViewById(R.id.notesInput);
        dateInput = findViewById(R.id.dateInput);
        saveBtn = findViewById(R.id.saveBtn);
        listView = findViewById(R.id.familyRecordsList);

        try {
            EncryptionUtil.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveBtn.setOnClickListener(v -> saveRecord());
        loadRecords();
    }

    private void saveRecord() {
        try {
            FamilyHealthRecord record = new FamilyHealthRecord();
            record.memberId = memberId;
            record.title = EncryptionUtil.encrypt(titleInput.getText().toString());
            record.notes = EncryptionUtil.encrypt(notesInput.getText().toString());
            record.date = EncryptionUtil.encrypt(dateInput.getText().toString());

            db.familyHealthRecordDao().insert(record);
            Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show();
            loadRecords();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecords() {
        List<FamilyHealthRecord> all = db.familyHealthRecordDao().getRecordsForMember(memberId);
        ArrayList<String> displayList = new ArrayList<>();
        for (FamilyHealthRecord r : all) {
            try {
                String title = EncryptionUtil.decrypt(r.title);
                String date = EncryptionUtil.decrypt(r.date);
                displayList.add(title + " - " + date);
            } catch (Exception e) {
                displayList.add("Decryption error");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
