package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

@Dao
interface WorkoutWithExercisesDao {

    @Transaction
    @Query("SELECT * FROM workout WHERE id=(:id)")
    fun get(id: UUID): LiveData<WorkoutWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout WHERE session is null")
    fun getTemplates(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE session is not null AND completed = 1")
    fun getCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE session is not null AND completed = 0")
    fun getNonCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<WorkoutWithExercises>>
}