package com.example.reflex_projekt.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="stats3x3")

data class Stats3x3 (
    @PrimaryKey(autoGenerate=true) var _id:Int?,
    @ColumnInfo(name="time3x3")var time3x3: Long,
    @ColumnInfo(name="points3x3")var points3x3: Int){

    @Ignore
    constructor(time3x3: Long, points3x3: Int) : this (null, time3x3, points3x3)
}