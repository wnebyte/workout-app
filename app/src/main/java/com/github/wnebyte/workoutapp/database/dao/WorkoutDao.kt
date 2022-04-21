package com.github.wnebyte.workoutapp.database.dao

import java.util.*
import androidx.room.*
import android.database.Cursor
import androidx.lifecycle.LiveData
import com.github.wnebyte.workoutapp.model.Workout

@Dao
interface WorkoutDao : IDao<Workout> {

    @Query("DELETE FROM workout")
    fun deleteAll()

    @Query("SELECT * FROM workout WHERE id=(:id)")
    fun get(id: UUID): LiveData<Workout?>

    @Query("SELECT * FROM workout WHERE completed = 1")
    fun getCompleted(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE completed = 1 ORDER BY CASE WHEN :asc = 1 THEN date END ASC, CASE WHEN :asc = 0 then date END DESC")
    fun getCompletedOrderByDate(asc: Boolean = true): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE completed = 0")
    fun getNonCompleted(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE completed = 0 ORDER BY CASE WHEN :asc = 1 THEN date END ASC, CASE WHEN :asc = 0 then date END DESC")
    fun getNonCompletedOrderByDate(asc: Boolean = true): LiveData<List<Workout>>

    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout")
    fun getAllRaw(): Cursor

    @Query("SELECT * FROM workout ORDER BY CASE WHEN :asc = 1 THEN date END ASC, CASE WHEN :asc = 0 then date END DESC")
    fun getAllOrderByDate(asc: Boolean = true): LiveData<List<Workout>>

    @Query("SELECT COUNT(*) FROM workout")
    fun getCount(): Long
}