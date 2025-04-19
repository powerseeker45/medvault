package com.example.medvault;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.medvault.HealthRecord;

@Database(entities = {
        HealthRecord.class,
        Medication.class,
        Appointment.class,
        FamilyMember.class,
        User.class,
        FamilyHealthRecord.class
}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HealthRecordDao healthRecordDao();
    public abstract MedicationDao medicationDao();
    public abstract AppointmentDao appointmentDao();
    public abstract FamilyMemberDao familyMemberDao();
    public abstract UserDao userDao();
    public abstract FamilyHealthRecordDao familyHealthRecordDao();

}
