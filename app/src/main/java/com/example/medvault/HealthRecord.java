package com.example.medvault;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HealthRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String notes;
    public String date;
}
