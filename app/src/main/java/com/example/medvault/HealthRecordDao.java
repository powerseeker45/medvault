package com.example.medvault;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.medvault.HealthRecord;

import java.util.List;

@Dao
public interface HealthRecordDao {

    @Insert
    void insert(HealthRecord record);

    @Delete
    void delete(HealthRecord record);

    @Query("SELECT * FROM HealthRecord ORDER BY id DESC")
    List<HealthRecord> getAllRecords();
}

