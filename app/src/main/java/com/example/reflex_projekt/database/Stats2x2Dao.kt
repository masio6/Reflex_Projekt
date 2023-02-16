package com.example.reflex_projekt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Stats2x2Dao {
    @Query("SELECT * FROM stats2x2")
    fun getAll(): MutableList<Stats2x2>

    @Insert
    suspend fun insert(stats2x2: Stats2x2)

    @Query("DELETE FROM stats2x2")
    suspend fun deleteAll()
}