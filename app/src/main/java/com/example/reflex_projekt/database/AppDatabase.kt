package com.example.reflex_projekt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Stats2x2 :: class, Stats3x3 :: class, Stats4x4 :: class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun stats2x2Dao(): Stats2x2Dao
    abstract fun stats3x3Dao(): Stats3x3Dao
    abstract fun stats4x4Dao(): Stats4x4Dao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "settings"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }
}