package com.github.wnebyte.workoutapp.ui

import androidx.recyclerview.widget.DiffUtil
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.Workout
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

class AdapterUtil {

    companion object {

        val DIFF_UTIL_SET_CALLBACK: DiffUtil.ItemCallback<Set> = object:
            DiffUtil.ItemCallback<Set>() {

            override fun areItemsTheSame(
                oldItem: Set,
                newItem: Set
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Set,
                newItem: Set
            ): Boolean {
                return oldItem == newItem
            }
        }

        val DIFF_UTIL_WORKOUT_CALLBACK: DiffUtil.ItemCallback<Workout> = object:
            DiffUtil.ItemCallback<Workout>() {

            override fun areItemsTheSame(
                oldItem: Workout,
                newItem: Workout
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Workout,
                newItem: Workout
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }

        val DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK: DiffUtil.ItemCallback<ExerciseWithSets> = object:
            DiffUtil.ItemCallback<ExerciseWithSets>() {

            override fun areItemsTheSame(
                oldItem: ExerciseWithSets,
                newItem: ExerciseWithSets
            ): Boolean {
                return oldItem.exercise.id == newItem.exercise.id
            }

            override fun areContentsTheSame(
                oldItem: ExerciseWithSets,
                newItem: ExerciseWithSets
            ): Boolean {
                return oldItem == newItem
            }
        }

        val DIFF_UTIL_WORKOUT_WITH_EXERCISES_CALLBACK: DiffUtil.ItemCallback<WorkoutWithExercises> =
            object: DiffUtil.ItemCallback<WorkoutWithExercises>() {

                override fun areItemsTheSame(
                oldItem: WorkoutWithExercises,
                newItem: WorkoutWithExercises
            ): Boolean {
                return oldItem.workout.id == newItem.workout.id
            }

            override fun areContentsTheSame(
                oldItem: WorkoutWithExercises,
                newItem: WorkoutWithExercises
            ): Boolean {
                return oldItem == newItem
            }
        }

        val DIFF_UTIL_LONG_CALLBACK: DiffUtil.ItemCallback<Long> =
            object: DiffUtil.ItemCallback<Long>() {

                override fun areItemsTheSame(oldItem: Long, newItem: Long): Boolean {
                    TODO("Not yet implemented")
                }

                override fun areContentsTheSame(oldItem: Long, newItem: Long): Boolean {
                    TODO("Not yet implemented")
                }

            }
    }
}