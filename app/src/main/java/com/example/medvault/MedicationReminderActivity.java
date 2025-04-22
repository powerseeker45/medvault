package com.example.medvault;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import android.app.TimePickerDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MedicationReminderActivity extends AppCompatActivity {

    EditText medicineName;
    TextView selectedTime;
    Button pickTimeBtn, setReminderBtn;
    ListView reminderListView;

    int hour, minute;
    final ArrayList<String> reminders = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_reminder);

        medicineName = findViewById(R.id.medicineName);
        selectedTime = findViewById(R.id.selectedTime);
        pickTimeBtn = findViewById(R.id.pickTimeBtn);
        setReminderBtn = findViewById(R.id.setReminderBtn);
        reminderListView = findViewById(R.id.reminderListView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminders);
        reminderListView.setAdapter(adapter);

        pickTimeBtn.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, h, m) -> {
                hour = h;
                minute = m;
                selectedTime.setText(String.format("Selected Time: %02d:%02d", hour, minute));
            }, 12, 0, true);
            timePickerDialog.show();
        });

        setReminderBtn.setOnClickListener(v -> {
            String name = medicineName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("medicineName", name);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) System.currentTimeMillis(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    reminders.add(name + " at " + String.format("%02d:%02d", hour, minute));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Enable exact alarm permission in settings", Toast.LENGTH_LONG).show();
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                reminders.add(name + " at " + String.format("%02d:%02d", hour, minute));
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
