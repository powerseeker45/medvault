package com.example.medvault;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.medvault.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
}
