package com.example.medvault;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FamilyMember {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int age;
    public String relation;
}

