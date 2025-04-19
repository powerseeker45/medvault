package com.example.medvault;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.medvault.Medication;

import java.util.List;

@Dao
public interface MedicationDao {
    @Insert
    void insert(Medication med);

    @Query("SELECT * FROM Medication")
    List<Medication> getAll();

    @Delete
    void delete(Medication med);
}

