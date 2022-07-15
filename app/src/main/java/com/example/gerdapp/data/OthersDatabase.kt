package com.example.gerdapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Others::class], version = 1, exportSchema = false)
abstract class OthersDatabase: RoomDatabase() {
    abstract fun othersDao(): OthersDao

    companion object {
        @Volatile
        private var INSTANCE: OthersDatabase? = null
        fun getDatabase(context: Context): OthersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OthersDatabase::class.java,
                    "others_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}