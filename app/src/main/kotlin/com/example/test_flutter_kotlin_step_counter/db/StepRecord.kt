package com.example.test_flutter_kotlin_step_counter.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_record")
data class StepRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,   // yyyy-MM-dd
    val step: Int       // 前日分の歩数
)
