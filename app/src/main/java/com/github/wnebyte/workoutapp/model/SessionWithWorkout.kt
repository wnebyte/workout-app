package com.github.wnebyte.workoutapp.model

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithWorkout(
    @Embedded
    val session: Session,
    @Relation(
        entity = Workout::class,
        parentColumn = "id",
        entityColumn = "session"
    )
    val workout: WorkoutWithExercises
)