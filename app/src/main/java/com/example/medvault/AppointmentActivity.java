package com.example.medvault;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.appcompat.widget.Toolbar;
import com.example.medvault.AppDatabase;
import com.example.medvault.Appointment;
import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    EditText doctorInput, hospitalInput, notesInput;
    Button dateBtn, timeBtn, saveBtn;
    String selectedDate = "", selectedTime = "";
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        doctorInput = findViewById(R.id.doctorInput);
        hospitalInput = findViewById(R.id.hospitalInput);
        notesInput = findViewById(R.id.notesInput);
        dateBtn = findViewById(R.id.dateBtn);
        timeBtn = findViewById(R.id.timeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        Button viewAppointmentsBtn = findViewById(R.id.viewAppointmentsBtn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }

        db = AppDatabaseInstance.getInstance(this);


        dateBtn.setOnClickListener(v -> showDatePicker());
        timeBtn.setOnClickListener(v -> showTimePicker());
        saveBtn.setOnClickListener(v -> saveAppointment());
        viewAppointmentsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewAppointmentsActivity.class));
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

    void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
            dateBtn.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            timeBtn.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    void saveAppointment() {
        Appointment appt = new Appointment();
        appt.doctorName = doctorInput.getText().toString();
        appt.hospital = hospitalInput.getText().toString();
        appt.date = selectedDate;
        appt.time = selectedTime;
        appt.notes = notesInput.getText().toString();

        db.appointmentDao().insert(appt);
        Toast.makeText(this, "Appointment saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
