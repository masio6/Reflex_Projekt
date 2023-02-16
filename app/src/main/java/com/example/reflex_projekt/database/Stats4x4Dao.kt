package com.example.reflex_projekt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Stats4x4Dao {
    @Query("SELECT * FROM stats4x4")
    fun getAll(): MutableList<Stats4x4>

    @Insert
    suspend fun insert(stats4x4: Stats4x4)

    @Query("DELETE FROM stats4x4")
    suspend fun deleteAll()
}