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
    @Query("SELECT * FROM workout WHERE completed = 1")
    fun getCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 1 AND date <= (:today) ORDER BY date LIMIT 1")
    fun getMostRecentlyCompleted(today: Long): LiveData<WorkoutWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 1 ORDER BY CASE WHEN :asc = 1 THEN date END ASC, CASE WHEN :asc = 0 then date END DESC")
    fun getCompletedOrderByDate(asc: Boolean = true): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 1 AND (:from) <= date AND date <= (:to)")
    fun getCompletedBetween(from: Long, to: Long): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 0")
    fun getNonCompleted(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE completed = 0 ORDER BY CASE WHEN :asc = 1 THEN date END ASC, CASE WHEN :asc = 0 then date END DESC")
    fun getNonCompletedOrderByDate(asc: Boolean = true): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout ORDER BY CASE WHEN :asc = 1 THEN date END ASC, CASE WHEN :asc = 0 then date END DESC")
    fun getAllOrderByDate(asc: Boolean = true): LiveData<List<WorkoutWithExercises>>
}