package com.example.medvault;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.appcompat.widget.Toolbar;
import com.example.medvault.AppDatabase;
import com.example.medvault.Medication;

import java.util.Calendar;

public class MedicationActivity extends AppCompatActivity {

    EditText nameInput, dosageInput, timeInput, pillsLeftInput, refillThresholdInput;
    Button setReminderBtn;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        nameInput = findViewById(R.id.nameInput);
        dosageInput = findViewById(R.id.dosageInput);
        timeInput = findViewById(R.id.timeInput);
        pillsLeftInput = findViewById(R.id.pillsLeftInput);
        refillThresholdInput = findViewById(R.id.refillThresholdInput);
        setReminderBtn = findViewById(R.id.setReminderBtn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }

        db = AppDatabaseInstance.getInstance(this);

        setReminderBtn.setOnClickListener(v -> saveMedication());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    void saveMedication() {
        String name = nameInput.getText().toString();
        String dosage = dosageInput.getText().toString();
        String time = timeInput.getText().toString();  // Ex: "08:30"
        int pillsLeft = Integer.parseInt(pillsLeftInput.getText().toString());
        int threshold = Integer.parseInt(refillThresholdInput.getText().toString());

        Medication med = new Medication();
        med.name = name;
        med.dosage = dosage;
        med.time = time;
        med.pillsLeft = pillsLeft;
        med.refillThreshold = threshold;

        db.medicationDao().insert(med);
        scheduleAlarm(time, name, dosage);
        Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show();
        finish();
    }

    void scheduleAlarm(String timeStr, String medName, String dosage) {
        String[] timeParts = timeStr.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("med_name", medName);
        intent.putExtra("dosage", dosage);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
