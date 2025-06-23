package com.example.test_flutter_kotlin_step_counter.db

import androidx.room.*

@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stepRecord: StepRecord): Long

    @Update
    fun update(stepRecord: StepRecord)

    @Query("SELECT * FROM step_record WHERE date = :date LIMIT 1")
    fun getByDate(date: String): StepRecord?

    @Query("SELECT * FROM step_record ORDER BY date DESC")
    fun getAll(): List<StepRecord>

    @Query("DELETE FROM step_record")
    fun deleteAll()
}
