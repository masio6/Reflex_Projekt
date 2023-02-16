package com.example.reflex_projekt.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="stats4x4")

data class Stats4x4 (
    @PrimaryKey(autoGenerate=true) var _id:Int?,
    @ColumnInfo(name="time4x4")var time4x4: Long,
    @ColumnInfo(name="points4x4")var points4x4: Int){

    @Ignore
    constructor(time4x4: Long, points4x4: Int) : this (null, time4x4, points4x4)
}