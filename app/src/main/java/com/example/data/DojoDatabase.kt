package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserStats::class, WorkoutLog::class], version = 1, exportSchema = false)
abstract class DojoDatabase : RoomDatabase() {
    abstract fun dojoDao(): DojoDao

    companion object {
        @Volatile
        private var INSTANCE: DojoDatabase? = null

        fun getDatabase(context: Context): DojoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DojoDatabase::class.java,
                    "dojo_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
