package com.example.medvault;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.medvault.Appointment;
import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert
    void insert(Appointment appt);

    @Query("SELECT * FROM Appointment ORDER BY date ASC")
    List<Appointment> getAll();

    @Delete
    void delete(Appointment appt);
}

