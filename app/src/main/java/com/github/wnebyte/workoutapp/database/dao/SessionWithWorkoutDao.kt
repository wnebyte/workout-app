package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.wnebyte.workoutapp.model.SessionWithWorkout
import java.util.*

@Dao
interface SessionWithWorkoutDao {

    @Transaction
    @Query("SELECT * FROM session WHERE id=(:id)")
    fun get(id: UUID): LiveData<SessionWithWorkout?>

    @Transaction
    @Query("SELECT * from session WHERE completed = 0")
    fun getNonCompleted(): LiveData<List<SessionWithWorkout>>

    @Transaction
    @Query("SELECT * FROM session WHERE completed = 1")
    fun getCompleted(): LiveData<List<SessionWithWorkout>>

    @Transaction
    @Query("SELECT * FROM session")
    fun getAll(): LiveData<List<SessionWithWorkout>>
}