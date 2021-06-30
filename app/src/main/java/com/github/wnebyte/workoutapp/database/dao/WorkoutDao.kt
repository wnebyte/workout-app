package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.wnebyte.workoutapp.model.Workout
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(workout: Workout)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg workout: Workout)

    @Delete
    fun delete(workout: Workout)

    @Delete
    fun delete(vararg workout: Workout)

    @Query("DELETE FROM workout")
    fun deleteAll()

    @Query("SELECT * FROM workout WHERE id=(:id)")
    fun get(id: UUID): LiveData<Workout?>

    @Query("SELECT * FROM workout WHERE session is null")
    fun getTemplates(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE session is not null AND completed = 1")
    fun getCompleted(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE session is not null AND completed = 0")
    fun getNonCompleted(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<Workout>>
}