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
    @Query("SELECT * FROM workout WHERE id=(:id)")
    fun getSuspended(id: UUID): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 1")
    fun getCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 0")
    fun getNonCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 0 ORDER BY date ASC")
    fun getOrderedNonCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<WorkoutWithExercises>>
}