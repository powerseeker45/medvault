package com.example.medvault;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.medvault.AppDatabase;
import com.example.medvault.Appointment;

import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentsActivity extends AppCompatActivity {

    ListView appointmentsListView;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        appointmentsListView = findViewById(R.id.appointmentsListView);

        db = AppDatabaseInstance.getInstance(this);


        List<Appointment> allAppointments = db.appointmentDao().getAll();

        ArrayList<String> appointmentDetails = new ArrayList<>();
        for (Appointment a : allAppointments) {
            appointmentDetails.add("Doctor: " + a.doctorName +
                    "\nHospital: " + a.hospital +
                    "\nDate: " + a.date +
                    "\nTime: " + a.time +
                    "\nNotes: " + a.notes);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, appointmentDetails);
        appointmentsListView.setAdapter(adapter);
    }
}
