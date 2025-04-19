package com.example.medvault;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String doctorName;
    public String hospital;
    public String date; // Format: dd-MM-yyyy
    public String time; // Format: HH:mm
    public String notes;
}

