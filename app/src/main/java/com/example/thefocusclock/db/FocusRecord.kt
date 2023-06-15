package com.example.thefocusclock.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "focus_record_table")
data class FocusRecord (
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val date:String,
    val mode:String,
    val timeMinutes:String
        )