package com.example.medvault;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FamilyHealthRecordDao {
    @Insert
    void insert(FamilyHealthRecord record);

    @Query("SELECT * FROM FamilyHealthRecord WHERE memberId = :memberId")
    List<FamilyHealthRecord> getRecordsForMember(int memberId);
}
