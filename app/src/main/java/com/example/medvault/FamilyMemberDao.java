package com.example.medvault;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import com.example.medvault.FamilyMember;

import java.util.List;

@Dao
public interface FamilyMemberDao {
    @Insert
    void insert(FamilyMember member);

    @Query("SELECT * FROM FamilyMember")
    List<FamilyMember> getAll();

    @Delete
    void delete(FamilyMember member);
}

