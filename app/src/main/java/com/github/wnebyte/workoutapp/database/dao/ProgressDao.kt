package com.github.wnebyte.workoutapp.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProgressDao {

    @Query(
        "SELECT * FROM workout INNER JOIN exercise ON exercise.workout == workout.id INNER JOIN `set` ON `set`.exercise == exercise.id WHERE workout.completed = 1 AND (:min) <= workout.date AND workout.date <= (:max)"
    )
    fun test(min: Long, max: Long)
}