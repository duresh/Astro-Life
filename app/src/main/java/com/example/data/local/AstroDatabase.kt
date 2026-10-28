package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BirthProfile

@Database(entities = [BirthProfile::class], version = 1, exportSchema = false)
abstract class AstroDatabase : RoomDatabase() {
    abstract fun birthProfileDao(): BirthProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AstroDatabase? = null

        fun getDatabase(context: Context): AstroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstroDatabase::class.java,
                    "astro_life_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
