package com.example.reflex_projekt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Stats3x3Dao {
    @Query("SELECT * FROM stats3x3")
    fun getAll(): MutableList<Stats3x3>

    @Insert
    suspend fun insert(stats3x3: Stats3x3)

    @Query("DELETE FROM stats3x3")
    suspend fun deleteAll()
}