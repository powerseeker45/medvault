package com.example.medvault;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String dosage;
    public String time;  // Format: "HH:mm"
    public int pillsLeft;
    public int refillThreshold;
}

