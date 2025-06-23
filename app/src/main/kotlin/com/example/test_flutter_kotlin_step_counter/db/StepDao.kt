package com.example.test_flutter_kotlin_step_counter.db

import androidx.room.*

@Dao
interface StepDao {
    @Query("SELECT * FROM step_record ORDER BY date DESC, time DESC")
    fun getAll(): List<StepRecord>

    @Query("SELECT * FROM step_record WHERE date = :date LIMIT 1")
    fun getByDate(date: String): StepRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record: StepRecord)

    @Update
    fun update(record: StepRecord)

    @Query("DELETE FROM step_record")
    fun deleteAll()
}
