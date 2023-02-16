package com.example.reflex_projekt.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="stats2x2")

data class Stats2x2 (
    @PrimaryKey(autoGenerate=true) var _id:Int?,
    @ColumnInfo(name="time2x2")var time2x2: Long,
    @ColumnInfo(name="points2x2")var points2x2: Int){

    @Ignore
    constructor(time2x2: Long, points2x2: Int) : this (null, time2x2, points2x2)
}