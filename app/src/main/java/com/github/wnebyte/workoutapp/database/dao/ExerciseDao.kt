package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.wnebyte.workoutapp.model.Exercise
import java.util.*

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg exercise: Exercise)

    @Delete
    fun delete(exercise: Exercise)

    @Delete
    fun delete(vararg exercise: Exercise)

    @Update
    fun update(exercise: Exercise)

    @Update
    fun update(vararg exercise: Exercise)

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
}