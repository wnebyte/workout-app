package com.github.wnebyte.workoutapp.ui.workoutlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.github.wnebyte.workoutapp.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

private const val TAG = "WorkoutListViewModel"

class WorkoutListViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val workoutListLiveData: LiveData<List<WorkoutWithExercises>> =
        repository.getOrderedNonCompletedWorkoutsWithExercises()
            .distinctUntilChanged()

    fun deleteWorkout(workout: WorkoutWithExercises) {
        Log.i(TAG, "Deleting workout: ${workout.workout.id}")
        repository.deleteWorkout(workout)
        workout.exercises.forEach { exercise ->
            repository.deleteExercise(exercise.exercise)
            repository.deleteSet(exercise.sets)
        }
    }
}