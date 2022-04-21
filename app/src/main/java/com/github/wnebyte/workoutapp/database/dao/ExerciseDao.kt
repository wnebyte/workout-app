package com.github.wnebyte.workoutapp.database.dao

import android.database.Cursor
import java.util.*
import androidx.room.*
import androidx.lifecycle.LiveData
import com.github.wnebyte.workoutapp.model.Exercise

@Dao
interface ExerciseDao : IDao<Exercise> {

    @Query("DELETE FROM exercise")
    fun deleteAll()

    @Query("SELECT * FROM exercise WHERE id=(:id)")
    fun get(id: UUID): LiveData<Exercise?>

    @Query("SELECT * FROM exercise WHERE workout is null")
    fun getTemplates(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE workout is not null AND completed = 1")
    fun getCompleted(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE workout is not null AND completed = 0")
    fun getNonCompleted(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercise")
    fun getAll(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercise")
    fun getAllRaw(): Cursor
}