package com.example.medvault;

import android.content.Context;
import androidx.room.Room;
import com.example.medvault.AppDatabase;

public class AppDatabaseInstance {
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "medvault_db"
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
