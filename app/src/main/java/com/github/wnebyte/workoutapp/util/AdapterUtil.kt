package com.github.wnebyte.workoutapp.util

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
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

        val DIFF_UTIL_EXERCISEWITHSETS_CALLBACK: DiffUtil.ItemCallback<ExerciseWithSets> = object:
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

        val DIFF_UTIL_WORKOUTWITHEXERCISES_CALLBACK: DiffUtil.ItemCallback<WorkoutWithExercises> =
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

        class WorkoutHolder(view: View): RecyclerView.ViewHolder(view) {

            fun bind(wrapper: WorkoutWithExercises) {

            }
        }

        class WorkoutAdapter: ListAdapter<WorkoutWithExercises, WorkoutHolder>
            (DIFF_UTIL_WORKOUTWITHEXERCISES_CALLBACK) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHolder {
                TODO("Not yet implemented")
            }

            override fun onBindViewHolder(holder: WorkoutHolder, position: Int) {
                TODO("Not yet implemented")
            }

        }
    }
}