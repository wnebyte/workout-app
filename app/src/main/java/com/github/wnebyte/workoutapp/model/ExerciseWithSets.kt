package com.github.wnebyte.workoutapp.model

import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class ExerciseWithSets(
    @Embedded
    val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "exercise"
    )
    val sets: MutableList<Set>
) {

    companion object {
        /**
         *
         */
        fun newInstance(workout: UUID? = null): ExerciseWithSets =
            ExerciseWithSets(Exercise.newInstance(workout), mutableListOf())

        /**
         * Returns a copy of the specified ExerciseWithSets with a new id and
         * the specified fk.
         * @param exerciseWithSets the ExerciseWithSets to be copied
         * @param workout the fk to associate with the copy
         * @return a special copy of the specified ExerciseWithSets
         */
        fun copyOf(exerciseWithSets: ExerciseWithSets, workout: UUID): ExerciseWithSets {
            val exercise = Exercise.copyOf(exerciseWithSets.exercise, workout)
            return ExerciseWithSets(
                exercise,
                Set.copyOf(exerciseWithSets.sets, exercise.id)
            )
        }
    }
}
