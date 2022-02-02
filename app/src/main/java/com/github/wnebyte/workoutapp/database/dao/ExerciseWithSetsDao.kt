package com.github.wnebyte.workoutapp.database.dao

import java.util.*
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.lifecycle.LiveData
import com.github.wnebyte.workoutapp.model.ExerciseWithSets

@Dao
interface ExerciseWithSetsDao {

    @Transaction
    @Query("SELECT * FROM exercise WHERE id=(:id)")
    fun get(id: UUID): LiveData<ExerciseWithSets?>

    @Transaction
    @Query("SELECT * FROM exercise WHERE workout is null")
    fun getTemplates(): LiveData<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercise WHERE workout is null ORDER BY name ASC")
    fun getTemplatesOrderByName(): LiveData<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercise WHERE workout is not null AND completed = 1")
    fun getCompleted(): LiveData<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercise WHERE workout is not null AND completed = 0")
    fun getNonCompleted(): LiveData<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercise")
    fun getAll(): LiveData<List<ExerciseWithSets>>
}