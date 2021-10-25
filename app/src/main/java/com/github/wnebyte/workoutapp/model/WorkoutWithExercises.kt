package com.github.wnebyte.workoutapp.model

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded
    val workout: Workout,
    @Relation(
        entity = Exercise::class,
        parentColumn = "id",
        entityColumn = "workout"
    )
    val exercises: MutableList<ExerciseWithSets>
) {
    companion object {
        /**
         * Creates a new object with a default workout object and and empty list of exercises.
         */
        fun newInstance(): WorkoutWithExercises =
            WorkoutWithExercises(Workout.newInstance(), mutableListOf())
    }
}