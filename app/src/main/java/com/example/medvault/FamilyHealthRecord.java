package com.example.medvault;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FamilyHealthRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int memberId; // Foreign key to FamilyMember

    public String title;
    public String notes;
    public String date;
}
